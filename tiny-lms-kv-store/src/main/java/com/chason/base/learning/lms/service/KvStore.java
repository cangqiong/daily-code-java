package com.chason.base.learning.lms.service;

import java.io.Closeable;

/**
 * LMS的KV存储
 */
public interface KvStore extends Closeable {

    /**
     * 保存数据
     * @param key
     * @param value
     */
    void set(String key,String value);

    /**
     * 查询数据
     * @param key
     */
    String get(String key);

    /**
     * 删除数据
     * @param key
     */
    void rm(String key);

}
