package com.chason.base.learning.lms.store;

import lombok.Data;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * SSL table索引信息
 */
@Data
public class TableMetaInfo {

    // 版本号
    private long version;
    // 数据起始点
    private long dataStart;
    // 数据长度
    private long dataLen;
    // 索引起始点
    private long indexStart;
    // 索引长度
    private long indexLen;
    // 分段大小
    private long partSize;

    /**
     * 写入索引信息到文件中
     *
     * @param randomAccessFile
     */
    public void writeFile(RandomAccessFile randomAccessFile) {
        try {
            randomAccessFile.writeLong(partSize);
            randomAccessFile.writeLong(dataStart);
            randomAccessFile.writeLong(dataLen);
            randomAccessFile.writeLong(indexStart);
            randomAccessFile.writeLong(indexLen);
            randomAccessFile.writeLong(version);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从SStable中读取索引信息
     *
     * @param randomAccessFile
     * @return
     */
    public static TableMetaInfo readFromFile(RandomAccessFile randomAccessFile) {
        try {
            TableMetaInfo tableMetaInfo = new TableMetaInfo();
            long filePosition = randomAccessFile.length();

            randomAccessFile.seek(filePosition - 8);
            tableMetaInfo.setVersion(randomAccessFile.readLong());

            randomAccessFile.seek(filePosition - 8);
            tableMetaInfo.setIndexLen(randomAccessFile.readLong());

            randomAccessFile.seek(filePosition - 8);
            tableMetaInfo.setIndexStart(randomAccessFile.readLong());

            randomAccessFile.seek(filePosition - 8);
            tableMetaInfo.setDataLen(randomAccessFile.readLong());

            randomAccessFile.seek(filePosition - 8);
            tableMetaInfo.setDataStart(randomAccessFile.readLong());

            randomAccessFile.seek(filePosition - 8);
            tableMetaInfo.setPartSize(randomAccessFile.readLong());
            return tableMetaInfo;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
