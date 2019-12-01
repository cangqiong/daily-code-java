package com.chason.base.relearning;

/**
 * Quick union算法
 * Author: chason
 * Date: 2019/12/1 11:27
 **/
public class QuickUnion extends UnionFind {

    // 对id数组任一值parent[i],i为对应的整数，parent[i]为所在树的根
    private int[] parent;

    public QuickUnion(int n) {
        super(n);
        parent = new int[n];
        // 初识时，将每个联通的根设置为自身
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
    }

    @Override
    void union(int p, int q) {
        int pRoot = find(p);
        int qRoot = find(q);
        if (pRoot == qRoot) return;
        // 将p所在的树变成q所在树的节点,即p所在树的根节点作为q的根节点的子树
        parent[pRoot] = qRoot;
        count--;
    }

    @Override
    boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    @Override
    int find(int p) {
        while (p != parent[p]) {
            p = parent[p];
        }
        return p;
    }


    @Override
    int count() {
        return count;
    }

}
