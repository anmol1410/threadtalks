package com.anmol.demo;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        final MainThread threadOne = new MainThread("Thread_1");
        final MainThread threadTwo = new MainThread("Thread_2");

        threadOne.setToSendDataTo(threadTwo);
        threadTwo.setToSendDataTo(threadOne);

        threadOne.start();
        threadTwo.start();
//
//        new Publisher(threadOne).post();
//
//        new Thread(() -> {
//            System.out.println(Thread.currentThread().getName() + " from T1_1");
//            new Publisher(threadOne.consumer()).post(() -> {
//                System.out.println(Thread.currentThread().getName() + " from T1_2");
//            });
//        }, "workerThread1").start();
//        new Thread(() -> {
//            System.out.println(Thread.currentThread().getName() + " from T2_1");
//            new Publisher(threadOne.consumer()).post(() -> {
//                System.out.println(Thread.currentThread().getName() + " from T2_2");
//            });
//        }, "workerThread2").start();
//        new Thread(() -> {
//            System.out.println(Thread.currentThread().getName() + " from T3_1");
//            new Publisher(threadOne.consumer()).post(() -> {
//                System.out.println(Thread.currentThread().getName() + " from T3_2");
//            });
//        }, "workerThread3").start();
//        new Thread(() -> {
//            System.out.println(Thread.currentThread().getName() + " from T3_1");
//            new Publisher(threadOne.consumer()).sendEmptyMessage(4);
//        }, "workerThread4").start();

//        threadOne.quit();
    }
}
