package com.chason.base.learning.lms.store;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.chason.base.learning.lms.store.domain.Command;
import com.chason.base.learning.lms.store.domain.Position;
import com.chason.base.learning.lms.store.domain.RmCommand;
import com.chason.base.learning.lms.store.domain.SetCommand;
import com.chason.base.learning.lms.util.ConvertUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Optional;
import java.util.TreeMap;

import static com.chason.base.learning.lms.store.conf.LmsKvStoreConstants.RW_MODE;


@Slf4j
public class SSTable implements Closeable {

    /**
     * 表索引信息
     */
    private TableMetaInfo tableMetaInfo;

    /**
     * 文件稀疏索引
     */
    private TreeMap<String, Position> sparseIndex;

    /**
     * 文件句柄
     */
    private RandomAccessFile tableSSTableFile;

    /**
     * 文件路径
     */
    private final String filePath;

    public SSTable(String filePath, int partSize) {
        this.tableMetaInfo = new TableMetaInfo();
        tableMetaInfo.setPartSize(partSize);
        this.filePath = filePath;
        try {
            this.tableSSTableFile = new RandomAccessFile(filePath, RW_MODE);
            tableSSTableFile.seek(0);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        sparseIndex = new TreeMap<>();
    }

    public static SSTable createFromFile(String filePath) {
        SSTable ssTable = new SSTable(filePath, 0);
        ssTable.restoreFromFile();
        return ssTable;
    }

    public static SSTable createFromIndex(String filePath, int partSize, TreeMap<String, Command> immutableIndex) {
        SSTable ssTable = new SSTable(filePath, partSize);
        ssTable.initFromIndex(immutableIndex);
        return ssTable;
    }

    /**
     * 查询对应值
     *
     * @param key
     * @return
     */
    public Command query(String key) {
        try {
            LinkedList<Position> spareKeyPositionList = new LinkedList<>();

            Position lastSmallPosition = null;
            Position firstBigPosition = null;

            // 从索引中找到最后一个小于等于key的位置，已经第一个大于key的位置
            for (String k : sparseIndex.keySet()) {
                if (k.compareTo(key) <= 0) {
                    lastSmallPosition = sparseIndex.get(k);
                } else {
                    firstBigPosition = sparseIndex.get(k);
                    break;
                }
            }

            if (lastSmallPosition != null) {
                spareKeyPositionList.add(lastSmallPosition);
            }

            if (firstBigPosition != null) {
                spareKeyPositionList.add(firstBigPosition);
            }
            if (spareKeyPositionList.size() == 0) {
                return null;
            }
            log.info("[SSTable][query][spareKeyPositionList]:[{}]", spareKeyPositionList);
            Position firstKeyPosition = spareKeyPositionList.getFirst();
            Position lastKeyPosition = spareKeyPositionList.getLast();
            long start = 0;
            long len = 0;
            start = firstKeyPosition.getStart();
            if (firstKeyPosition.equals(lastKeyPosition)) {
                len = firstKeyPosition.getLen();
            } else {
                len = lastKeyPosition.getStart() + lastKeyPosition.getLen() - start;
            }
            // 如果key如果必定位于区间内，所以只需要读取区间的数据，减少io
            byte[] dataPart = new byte[(int) len];
            tableSSTableFile.seek(start);
            tableSSTableFile.read(dataPart);
            int pStart = 0;
            // 读取分区数据
            for (Position position : spareKeyPositionList) {
                JSONObject dataPartJson = JSONUtil.parseObj(new String(dataPart, pStart, (int) position.getLen()));
                log.info("[SSTable][query][dataPartJson]:[{}]", dataPartJson);
                if (dataPartJson.containsKey(key)) {
                    JSONObject value = dataPartJson.getJSONObject(key);
                    return ConvertUtil.jsonToCommand(value);
                }
                pStart += position.getLen();
            }
            return null;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    /**
     * 从内存表转化为ssTable
     *
     * @param index
     */
    private void initFromIndex(TreeMap<String, Command> index) {
        try {
            JSONObject partData = new JSONObject(true);
            tableMetaInfo.setDataStart(tableSSTableFile.getFilePointer());
            for (Command command : index.values()) {
                //处理set命令
                if (command instanceof SetCommand) {
                    SetCommand set = (SetCommand) command;
                    partData.set(set.getKey(), set);
                }
                //处理rm命令
                if (command instanceof RmCommand) {
                    RmCommand rm = (RmCommand) command;
                    partData.set(rm.getKey(), rm);
                }

                //达到分段数量，开始写入数据段
                if (partData.size() >= tableMetaInfo.getPartSize()) {
                    writeDataPart(partData);
                }
            }
            //遍历完之后如果有剩余的数据（尾部数据不一定达到分段条件）写入文件
            if (partData.size() > 0) {
                writeDataPart(partData);
            }
            long dataPartLen = tableSSTableFile.getFilePointer() - tableMetaInfo.getDataStart();
            tableMetaInfo.setDataLen(dataPartLen);
            //保存稀疏索引
            byte[] indexBytes = JSONUtil.toJsonStr(sparseIndex).getBytes(StandardCharsets.UTF_8);
            tableMetaInfo.setIndexStart(tableSSTableFile.getFilePointer());
            tableSSTableFile.write(indexBytes);
            tableMetaInfo.setIndexLen(indexBytes.length);
            log.info("[SsTable][initFromIndex][sparseIndex]: {}", sparseIndex);

            //保存文件索引
            tableMetaInfo.writeFile(tableSSTableFile);
            log.info("[SsTable][initFromIndex]: {},{}", filePath, tableMetaInfo);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    /**
     * 将数据分区写入文件
     *
     * @param partData
     * @throws IOException
     */
    private void writeDataPart(JSONObject partData) throws IOException {
        byte[] partDataBytes = partData.toString().getBytes(StandardCharsets.UTF_8);
        long start = tableSSTableFile.getFilePointer();
        tableSSTableFile.write(partDataBytes);

        //记录数据段的第一个key到稀疏索引中
        Optional<String> firstKey = partData.keySet().stream().findFirst();
        firstKey.ifPresent(s -> sparseIndex.put(s, new Position(start, partDataBytes.length)));
        partData.clear();
    }

    private void restoreFromFile() {
        try {
            // 读取表元数据信息
            TableMetaInfo tableMetaInfo = TableMetaInfo.readFromFile(tableSSTableFile);
            this.tableMetaInfo = tableMetaInfo;
            log.info("[SSTable][restoreFromFile][tableMetaInfo]:[{}]", tableMetaInfo);
            byte[] indexBytes = new byte[(int) tableMetaInfo.getIndexLen()];

            // 读取索引信息
            tableSSTableFile.seek(tableMetaInfo.getIndexStart());
            tableSSTableFile.read(indexBytes);

            String indexStr = new String(indexBytes, CharsetUtil.UTF_8);
            log.info("[SSTable][restoreFromFile][indexStr]:[{}]", indexStr);

            sparseIndex = JSONUtil.toBean(indexStr, new TypeReference<TreeMap<String, Position>>() {
            }, false);
            log.info("[SSTable][restoreFromFile][sparseIndex]:[{}]", sparseIndex);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void close() throws IOException {
        tableSSTableFile.close();
    }


}
