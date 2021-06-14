package com.chason.base.relearning.jmh;

/**
 * Calculator
 *
 * @author XiongNeng
 * @version 1.0
 * @since 2018/1/7
 */
public class SingleCalculator implements Calculator {

    @Override
    public long sum(int[] numbers) {
        long result = 0;
        for (int i = 0; i < numbers.length; i++) {
            result += numbers[i];
        }
        return result;
    }

    @Override
    public void shutdown() {

    }
}