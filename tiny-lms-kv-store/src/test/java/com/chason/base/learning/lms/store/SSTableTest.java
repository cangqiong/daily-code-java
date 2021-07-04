package com.chason.base.learning.lms.store;

import com.chason.base.learning.lms.store.domain.Command;
import com.chason.base.learning.lms.store.domain.RmCommand;
import com.chason.base.learning.lms.store.domain.SetCommand;
import org.junit.Test;

import java.util.TreeMap;

public class SSTableTest {

    @Test
    public void createFromIndex() {
        TreeMap<String, Command> index = new TreeMap<>();
        for (int i = 0; i < 10; i++) {
            SetCommand setCommand = new SetCommand("key" + i, "value" + i);
            index.put(setCommand.getKey(), setCommand);
        }
        index.put("key100", new SetCommand("key100", "value100"));
        index.put("key100", new RmCommand("key100"));
        SSTable ssTable = SSTable.createFromIndex("test.txt", 3, index);
    }

    @Test
    public void query() {
        SSTable ssTable = SSTable.createFromFile("test.txt");
        System.out.println(ssTable.query("key0"));
        System.out.println(ssTable.query("key5"));
        System.out.println(ssTable.query("key9"));
        System.out.println(ssTable.query("key100"));
    }
}