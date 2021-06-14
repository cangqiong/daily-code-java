package com.chason.base.relearning;

import java.util.ArrayList;
import java.util.List;

/**
 * VM Args: -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError
 * xms:堆内存大小 xmx:堆内存最大
 * Java堆内存溢出测试
 * Author: chason
 * Date: 2020/3/15 16:49
 **/
public class HeapOOM {
    static class OOMObject{

    }

    public static void main(String[] args) {
        f();
        List<OOMObject> list = new ArrayList<>();
        while (true){
            list.add(new OOMObject());
        }
    }

    public static void f() {
        String[] a = new String[2];
        Object[] b = a;
        a[0] = "hi";
        b[1] = Integer.valueOf(42);
    }
}
