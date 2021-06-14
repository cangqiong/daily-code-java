package com.chason.base.relearning;

import java.util.ArrayList;
import java.util.List;

/**
 * 永久代测试
 * JVM Args: -Xmx64m -XX:MetaspaceSize=32m -XX:MaxMetaspaceSize=32m
 * Author: chason
 * Date: 2020/3/15 17:27
 **/
public class JavaTest {

    public static void main(String[] args) {
        String[] a = new String[2];
        Object[] b = a;
        a[0] = "hi";
        b[1] = Integer.valueOf(42);
    }
}
