package com.chason.base.relearning.jmh;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Calculator
 *
 * @author XiongNeng
 * @version 1.0
 * @since 2018/1/7
 */
public class MultiCalculator implements Calculator {

    private int nThread;

    private ExecutorService executor;

    public MultiCalculator(int nThread) {
        this.nThread = nThread;
        executor = Executors.newFixedThreadPool(nThread);
    }

    @Override
    public long sum(int[] numbers) {
        long result = 0;
        int avg = numbers.length / nThread;
        CompletableFuture<Long>[] completableFutures = new CompletableFuture[nThread];
        for (int i = 0; i < nThread; i++) {
            int startIndex = avg * i;
            int endIndex = startIndex + avg;
            if (endIndex + avg > numbers.length) {
                endIndex = numbers.length;
            }
            int finalEndIndex = endIndex;
            CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> {
                long sum = 0;
                for (int j = startIndex; j < finalEndIndex; j++) {
                    sum += numbers[j];
                }
                return sum;
            }, executor);
            completableFutures[i] = future;
        }
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(completableFutures);
        CompletableFuture<Long> allPageContentsFuture = allFutures.thenApply(v ->
                Arrays.stream(completableFutures).map(CompletableFuture::join)
                        .reduce(0L, Long::sum));
        try {
            return allPageContentsFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }
}