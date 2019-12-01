package com.chason.base.relearning;

/**
 * 并查集算法：判断节点之间的动态联通性，给定一系列整数对，判断给定的两个对象是否联通的问题
 * <p>
 * 抽象建模：
 * 1. 有一组不相交的对象集合
 * 2. union:联通两个对象
 * 3. find：判断两个对象之间是否存在一条联通的通路
 * <p>
 * 应用场景：
 * Author: chason
 * Date: 2019/12/1 11:13
 **/
public abstract class UnionFind {

    // 对象总个数
    protected int n;
    // 非联通对象集合个数
    protected int count;

    public UnionFind(int n) {
        this.n = n;
        this.count = n;
    }

    /**
     * 联通p与q
     *
     * @param p
     * @param q
     */
    abstract void union(int p, int q);

    /**
     * 判断p与q是否关联
     *
     * @param p
     * @param q
     */
    abstract boolean connected(int p, int q);

    /**
     * 获取p所在的集合
     *
     * @param p
     * @return
     */
    abstract int find(int p);

    /**
     * 统计不联通的集合个数
     *
     * @return
     */
    abstract int count();
}
