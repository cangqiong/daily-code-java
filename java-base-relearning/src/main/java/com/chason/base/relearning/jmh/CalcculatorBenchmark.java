package com.chason.base.relearning.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1)
@Measurement(iterations = 1)
@State(Scope.Benchmark)
public class CalcculatorBenchmark {

    @Param({"10000", "100000", "1000000"})
    private int length;

    private int[] numbers;

    private Calculator singleCalculator;
    private Calculator multiCalculator;

    @Benchmark
    public long singleCalculator() {
        return singleCalculator.sum(numbers);
    }

    @Benchmark
    public long multiCalculator() {
        return multiCalculator.sum(numbers);
    }

    @Setup
    public void prepare() {
        numbers = IntStream.rangeClosed(1, length).toArray();
        singleCalculator = new SingleCalculator();
        multiCalculator = new MultiCalculator(Runtime.getRuntime().availableProcessors());
    }

    @TearDown
    public void shutdown() {
        singleCalculator.shutdown();
        multiCalculator.shutdown();
    }

    public static void main(String[] args) throws RunnerException {
//        numbers = IntStream.rangeClosed(1,1000000).toArray();
//        singleCalculator = new SingleCalculator();
//        multiCalculator = new MultiCalculator(Runtime.getRuntime().availableProcessors());
//        System.out.println(singleCalculator.sum(numbers));
//        System.out.println(multiCalculator.sum(numbers));
        Options options = new OptionsBuilder()
                .include(CalcculatorBenchmark.class.getSimpleName())
                .forks(1)
                .build();
        Collection<RunResult> results = new Runner(options).run();
    }


}
