package com.chason.base.relearning;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * QuickFind 测试类
 * Author: chason
 * Date: 2019/12/1 11:44
 **/
public class QucikFindTest {

    private static String relativeFileName = "data/mediumUF.txt";
    private static String dataFilePath = QucikFindTest.class.getClassLoader().getResource(relativeFileName).getPath();

    public static void main(String[] args) throws IOException {
        File dataFile = new File(dataFilePath);
        List<String> stringList = FileUtils.readLines(dataFile);
        QuickFind quickFind = new QuickFind(Integer.parseInt(stringList.get(0)));
        for (String str : stringList) {
            if (str.split(" ").length > 1) {
                quickFind.union(Integer.parseInt(str.split(" ")[0]), Integer.parseInt(str.split(" ")[1]));
            }
        }

        System.out.printf("总的非联通集为:[%d]\n", quickFind.count());
        System.out.printf("[%d]与[%d]是否联通:[%b]\n", 370, 204, quickFind.connected(370, 204));
    }
}
