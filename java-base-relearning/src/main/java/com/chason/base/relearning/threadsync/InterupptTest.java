package com.chason.base.relearning.threadsync;

/**
 * 中断测试
 * Author: chason
 * Date: 2019/9/22 0:10
 **/
public class InterupptTest {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {


            while (true) {
                System.out.println("dfdf");
                Thread.yield();

                // 响应中断
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Java技术栈线程被中断，程序退出。");
                    return;
                }
            }




        });
        thread.start();
        Thread.sleep(2000);
        thread.interrupt();
    }
}
