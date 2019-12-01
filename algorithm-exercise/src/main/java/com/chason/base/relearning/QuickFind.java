package com.chason.base.relearning;

/**
 * Quick find算法
 * Author: chason
 * Date: 2019/12/1 11:27
 **/
public class QuickFind extends UnionFind {

    // 对id数组任一值id[i],i为对应的整数，id[i]为联通集合的标识
    private int[] id;

    public QuickFind(int n) {
        super(n);
        id = new int[n];
        // 初识时，将每个联通的对象设置为自身
        for (int i = 0; i < n; i++) {
            id[i] = i;
        }
    }

    @Override
    void union(int p, int q) {
        int pid = id[p];
        int qid = id[q];
        if (pid == qid) return;
        for (int i = 0; i < id.length; i++) {
            if (id[i] == pid) {
                id[i] = qid;
            }
        }
        count--;
    }

    @Override
    boolean connected(int p, int q) {
        return id[p] == id[q];
    }

    @Override
    int find(int p) {
        return id[p];
    }

    @Override
    int count() {
        return count;
    }

}
