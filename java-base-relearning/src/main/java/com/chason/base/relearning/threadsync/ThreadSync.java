package com.chason.base.relearning.threadsync;

import java.util.concurrent.*;

/**
 * 线程同步机制
 * Author: chason
 * Date: 2019/9/21 23:25
 **/
public class ThreadSync {

    static ExecutorService executor = Executors.newCachedThreadPool();

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        Callable<String> simpleTask =
                ()->{
//                    try {
                    System.out.println("SimpleTask started");

                    for (int i=1;i<1000000000;i++){}
                    if(Thread.currentThread().isInterrupted()){
                        System.out.println("Interrupted end");
                        return null;
                    }
//                    Thread.sleep(3000);
                    System.out.println("SimpleTask end");
//                    }catch (InterruptedException e){
//                        System.out.println("Interrupted end");
//                        return "";
//                    }
                    return "";
                };


        Future<String> result = executor.submit(simpleTask);
        executor.shutdownNow();
//        Thread.sleep(1000);
        result.cancel(true);
        System.out.println("Main thread end");
    }
}


