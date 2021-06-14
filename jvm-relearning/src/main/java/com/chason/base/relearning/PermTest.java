package com.chason.base.relearning;

import java.util.ArrayList;
import java.util.List;

/**
 * 永久代测试
 * JVM Args: -Xmx64m -XX:MetaspaceSize=32m -XX:MaxMetaspaceSize=32m
 * Author: chason
 * Date: 2020/3/15 17:27
 **/
public class PermTest {
    static String strConst = "String const";

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        while (true) {
            String str = strConst + strConst;
            strConst = str;
            list.add(str.intern());
        }
    }
}
