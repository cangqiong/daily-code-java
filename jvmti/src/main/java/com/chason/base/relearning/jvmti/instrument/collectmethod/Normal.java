package com.chason.base.relearning.jvmti.instrument.collectmethod;

import java.util.Random;

public class Normal {
    public void print() {
        System.out.println("test...");
    }

    public void printRandom() {
        System.out.println("test random:" + new Random().nextDouble());
    }
}
