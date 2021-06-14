package com.chason.base.relearning.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3)
@Measurement(iterations = 5,time = 1,timeUnit = TimeUnit.SECONDS)
@Threads(4)
@Fork(1)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class StringBuilderBenchmark {

    @Benchmark
    public void testStringAdd(){
        String str = "";
        for (int i = 0; i < 10; i++) {
            str+=i;
        }
        print(str);
    }

    @Benchmark
    public void testStringBuilderAdd(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(i);
        }
        print(sb.toString());
    }

    @Benchmark
    public void testStringBufferAdd(){
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 10; i++) {
            sb.append(i);
        }
        print(sb.toString());
    }

    private void print(String str) {
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(StringBuilderBenchmark.class.getSimpleName())
                .output("D:/Benchmark.log")
                .build();
        new Runner(options).run();
    }
}
