package com.chason.base.relearning;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 缓存测试类
 * Author: chason
 * Date: 2020/5/27 23:15
 **/
public class CacheTest {

    private  static Cache<Long, AtomicLong> cache;
     static {
         cache = CacheBuilder.newBuilder().expireAfterAccess(2, TimeUnit.MINUTES)
                 .build(new CacheLoader<Long, AtomicLong>() {
                     @Override
                     public AtomicLong load(Long key) throws Exception {
                         return new AtomicLong(0);
                     }
                 });
    }

    public static void main(String[] args) {
        cache.put(0L,new AtomicLong(2));
        cache.cleanUp();
        System.out.println(cache.get(0l).s);
    }
}
