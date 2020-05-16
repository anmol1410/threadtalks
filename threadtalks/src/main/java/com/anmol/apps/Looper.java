package com.anmol.apps;

public final class Looper {

    private static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<>();

    private final MessageQueue mQueue;
    private final Thread mThread;

    public static void prepare() {
        if (sThreadLocal.get() != null) {
            throw new RuntimeException("Only one Looper may be created per thread");
        }
        sThreadLocal.set(new Looper());
    }

    public static void loop() {
        final Looper me = myLooper();
        if (me == null) {
            throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
        }

        for (; ; ) {
            final Message msg = me.mQueue.next(); // Might block
            if (msg == null) {
                // No message indicates that the message queue is quitting.
                return;
            }
            msg.target().dispatchMessage(msg);
            msg.recycleUnchecked();
        }
    }

    public static Looper myLooper() {
        return sThreadLocal.get();
    }

    public static MessageQueue myQueue() {
        return myLooper().mQueue;
    }

    private Looper() {
        mQueue = new MessageQueue();
        mThread = Thread.currentThread();
    }

    public boolean isCurrentThread() {
        return Thread.currentThread() == mThread;
    }

    public void quit() {
        mQueue.quit(false);
    }

    public void quitSafely() {
        mQueue.quit(true);
    }

    public Thread getThread() {
        return mThread;
    }

    public MessageQueue queue() {
        return mQueue;
    }
}
