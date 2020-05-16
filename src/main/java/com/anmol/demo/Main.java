package com.anmol.demo;

import com.anmol.apps.Handler;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        final MainThread mainHandlerThread = new MainThread("MainHandlerThread");
        mainHandlerThread.start();

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " from T1_1");
            new Handler(mainHandlerThread.getLooper()).post(() -> {
                System.out.println(Thread.currentThread().getName() + " from T1_2");
            });
        }, "workerThread1").start();
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " from T2_1");
            new Handler(mainHandlerThread.getLooper()).post(() -> {
                System.out.println(Thread.currentThread().getName() + " from T2_2");
            });
        }, "workerThread2").start();
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " from T3_1");
            new Handler(mainHandlerThread.getLooper()).post(() -> {
                System.out.println(Thread.currentThread().getName() + " from T3_2");
            });
        }, "workerThread3").start();
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " from T3_1");
            new Handler(mainHandlerThread.getLooper()).sendEmptyMessage(4);
        }, "workerThread4").start();

//        mainHandlerThread.quit();
    }
}
