package com.chason.base.learning.lms.service;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.chason.base.learning.lms.store.SSTable;
import com.chason.base.learning.lms.store.domain.Command;
import com.chason.base.learning.lms.store.domain.RmCommand;
import com.chason.base.learning.lms.store.domain.SetCommand;
import com.chason.base.learning.lms.util.ConvertUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.chason.base.learning.lms.store.conf.LmsKvStoreConstants.RW_MODE;

/**
 * LMS的KV存储
 */
public class LmsKvStore implements KvStore {

    public static final String TABLE = ".table";
    public static final String WAL = "wal";
    public static final String WAL_TMP = "walTmp";

    /**
     * 数据目录
     */
    private final String dataDir;

    /**
     * 持久化阈值
     */
    private final int storeThreshold;


    /**
     * 数据分区大小
     */
    private final int partSize;

    /**
     * 内存表
     */
    private TreeMap<String, Command> index;

    /**
     * 不可变内存表，用于持久化内存表的暂存数据
     */
    private TreeMap<String, Command> immutableIndex;

    /**
     * SSTable列表
     */
    private final LinkedList<SSTable> ssTables;

    /**
     * 暂存数据的日志句柄
     */
    private RandomAccessFile wal;

    /**
     * 暂存数据日志文件
     */
    private File walFile;

    /**
     * 读写锁
     */
    private ReadWriteLock indexLock;

    public LmsKvStore(String dataDir, int storeThreshold, int partSize) {
        try {
            this.dataDir = dataDir;
            this.storeThreshold = storeThreshold;
            this.partSize = partSize;
            this.indexLock = new ReentrantReadWriteLock();
            File dir = new File(dataDir);
            File[] files = dir.listFiles();
            ssTables = new LinkedList<>();
            index = new TreeMap<>();
            if (files == null || files.length == 0) {
                walFile = new File(dataDir + WAL);
                wal = new RandomAccessFile(walFile, RW_MODE);
                return;
            }
            // 从大到小加载SSTable
            TreeMap<Long, SSTable> ssTableTreeMap = new TreeMap<>(Comparator.reverseOrder());
            for (File file : files) {
                String fileName = file.getName();
                if (file.isFile()) {
                    // 从暂存的WAL中恢复数据，一般是持久化SSTable过程中才会留下walTmp
                    if (fileName.equals(WAL_TMP)) {
                        restoreFromWal(new RandomAccessFile(file, RW_MODE));
                    } else if (fileName.equals(TABLE)) {
                        int dotIndex = fileName.indexOf(".");
                        long time = Long.parseLong(fileName.substring(0, dotIndex));
                        ssTableTreeMap.put(time, SSTable.createFromFile(file.getAbsolutePath()));
                    } else if (fileName.equals(WAL)) {
                        // 加载WAL文件
                        walFile = file;
                        wal = new RandomAccessFile(file, RW_MODE);
                        restoreFromWal(wal);
                    }
                }
                ssTables.addAll(ssTableTreeMap.values());
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从暂存日志中恢复数据
     *
     * @param wal
     */
    private void restoreFromWal(RandomAccessFile wal) {
        try {
            long len = wal.length();
            long start = 0;
            wal.seek(start);
            while (start < len) {
                // 先读取数据大小
                int valLen = wal.readInt();
                // 根据数据大小读取数据
                byte[] bytes = new byte[valLen];
                wal.read(bytes);
                JSONObject jsonObject = JSONUtil.parseObj(new String(bytes, CharsetUtil.UTF_8));
                Command command = ConvertUtil.jsonToCommand(jsonObject);
                if (command != null) {
                    index.put(command.getKey(), command);
                }
                start += 4;
                start += valLen;
            }
            wal.seek(wal.length());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(String key, String value) {
        try {
            Command setCommand = new SetCommand(key, value);
            byte[] bytes = JSONUtil.toJsonStr(setCommand).getBytes(CharsetUtil.UTF_8);
            indexLock.writeLock().lock();
            // 先保存数据到WAL中
            wal.writeInt(bytes.length);
            wal.write(bytes);
            index.put(key, setCommand);

            // 如果超过阈值，切换索引，并且持久化到SSTable中
            if (index.size() > storeThreshold) {
                switchIndex();
                storeToSsTable();
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            indexLock.writeLock().unlock();
        }
    }

    /**
     * 保存数据到ssTable
     */
    private void storeToSsTable() {
        try {
            //ssTable按照时间命名，这样可以保证名称递增
            SSTable ssTable = SSTable.createFromIndex(dataDir + System.currentTimeMillis() + TABLE, partSize, immutableIndex);
            ssTables.addFirst(ssTable);
            //持久化完成删除暂存的内存表和WAL_TMP
            immutableIndex = null;
            File tmpWal = new File(dataDir + WAL_TMP);
            if (tmpWal.exists()) {
                if (!tmpWal.delete()) {
                    throw new RuntimeException("删除文件失败: walTmp");
                }
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    /**
     * 切换内存表，新建一个内存表，老的暂存起来
     */
    private void switchIndex() {
        try {
            indexLock.writeLock().lock();
            // 切换内存表
            immutableIndex = index;
            index = new TreeMap<>();

            wal.close();
            // 切换内存表也要切换wal文件

            File tmpWal = new File(dataDir + WAL_TMP);
            if (tmpWal.exists()) {
                if (!tmpWal.delete()) {
                    throw new RuntimeException("删除重命名文件失败:walTmp");
                }
            }
            walFile = new File(dataDir + WAL);
            if (!walFile.renameTo(tmpWal)) {
                throw new RuntimeException("重命名wal文件失败:walTmp");
            }
            wal = new RandomAccessFile(walFile, RW_MODE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String get(String key) {
        try {
            indexLock.readLock().lock();
            // 1. 先从索引中查找
            Command command = index.get(key);
            // 2. 再尝试从不可变索引中查询，此时可能处于持久化sstable中
            if (command == null && immutableIndex != null) {
                command = immutableIndex.get(key);
            }
            // 3. 索引中没有，尝试从sstable,从新的sstable找到老的
            if (command == null) {
                for (SSTable ssTable : ssTables) {
                    command = ssTable.query(key);
                    if (command != null) {
                        break;
                    }
                }
            }
            if (command != null) {
                if (command instanceof SetCommand) {
                    return ((SetCommand) command).getVal();
                }
                if (command instanceof RmCommand) {
                    return null;
                }
                return null;
            } else {
                return null;
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            indexLock.readLock().unlock();
        }
    }

    @Override
    public void rm(String key) {
        try {
            Command rmCommand = new RmCommand(key);
            byte[] bytes = JSONUtil.toJsonStr(rmCommand).getBytes(StandardCharsets.UTF_8);
            indexLock.writeLock().lock();
            // 先保存数据到WAL中
            wal.writeInt(bytes.length);
            wal.write(bytes);
            index.put(key, rmCommand);

            // 如果超过阈值，切换索引，并且持久化到SSTable中
            if (index.size() > storeThreshold) {
                switchIndex();
                storeToSsTable();
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            indexLock.writeLock().unlock();
        }
    }


    @Override
    public void close() throws IOException {
        wal.close();
        for (SSTable ssTable : ssTables) {
            ssTable.close();
        }
    }
}
