package com.chason.base.learning.lms.service;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LmsKvStoreTest {

    @Test
    public void set() throws IOException {
        KvStore kvStore = new LmsKvStore("/logs/db/", 4, 3);
        for (int i = 0; i < 11; i++) {
            kvStore.set(i + "", i + "");
        }
        for (int i = 0; i < 11; i++) {
            assertEquals(i + "", kvStore.get(i + ""));
        }
        for (int i = 0; i < 11; i++) {
            kvStore.rm(i + "");
        }
        for (int i = 0; i < 11; i++) {
            assertNull(kvStore.get(i + ""));
        }
        kvStore.close();
        kvStore = new LmsKvStore("/logs/db/", 4, 3);
        for (int i = 0; i < 11; i++) {
            assertNull(kvStore.get(i + ""));
        }
        kvStore.close();
    }


}