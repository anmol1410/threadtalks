package com.anmol.apps;

public final class Consumer {

    private static final ThreadLocal<Consumer> sThreadLocal = new ThreadLocal<>();

    private final MessageQueue mQueue;
    private final Thread mThread;

    private Consumer() {
        mQueue = new MessageQueue();
        mThread = Thread.currentThread();
    }

    static void init() {
        if (sThreadLocal.get() != null) {
            throw new RuntimeException("Only one Spinner may be created per thread");
        }
        sThreadLocal.set(new Consumer());
    }

    public static void consume() {
        final Consumer me = myConsumer();
        if (me == null) {
            throw new RuntimeException("No Consumer::Consumer.init() wasn't called on this thread.");
        }

        for (; ; ) {
            final Message msg = me.mQueue.next(); // Might block
            if (msg == null) {
                // No message indicates that the message queue is quitting.
                return;
            }
            if (msg.callback() != null) {
                msg.callback().run();
            } else {
                ((ConsumerThread) myConsumer().getThread()).onMessage(msg);
            }
            msg.recycleUnchecked();
        }
    }

    public static Consumer myConsumer() {
        return sThreadLocal.get();
    }

    public static MessageQueue myQueue() {
        return myConsumer().mQueue;
    }

    public boolean isCurrentThread() {
        return Thread.currentThread() == mThread;
    }

    public void stop() {
        mQueue.stop(false);
    }

    public void stopSafely() {
        mQueue.stop(true);
    }

    public Thread getThread() {
        return mThread;
    }

    public MessageQueue queue() {
        return mQueue;
    }
}
