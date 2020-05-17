package com.anmol.apps;

public class ConsumerThread extends Thread {
    private Consumer mConsumer;
    private MessageQueue mQueue;

    protected void onRun() {
    }

    @Override
    public final void run() {
        Consumer.init();
        synchronized (this) {
            mConsumer = Consumer.myConsumer();
            mQueue = mConsumer.queue();
            notifyAll();
        }
        onRun();
        Consumer.consume();
    }

    public Consumer consumer() {
        if (!isAlive()) {
            return null;
        }

        synchronized (this) {
            while (isAlive() && mConsumer == null) {
                try {
                    wait();
                } catch (InterruptedException ignored) {
                }
            }
        }
        return mConsumer;
    }

    public boolean close() {
        final Consumer consumer = consumer();
        if (consumer != null) {
            consumer.stop();
            return true;
        }
        return false;
    }

    public boolean closeSafely() {
        Consumer consumer = consumer();
        if (consumer != null) {
            consumer.stopSafely();
            return true;
        }
        return false;
    }

    // Write Operations

    public final boolean post(final Runnable r) {
        return sendMessageDelayed(getPostMessage(r), 0);
    }

    public final boolean postAtTime(Runnable r, long uptimeMillis) {
        return sendMessageAtTime(getPostMessage(r), uptimeMillis);
    }

    public final boolean postDelayed(Runnable r, long delayMillis) {
        return sendMessageDelayed(getPostMessage(r), delayMillis);
    }

    public final boolean postAtFrontOfQueue(Runnable r) {
        return sendMessageAtFrontOfQueue(getPostMessage(r));
    }

    public final void removeCallbacks(final Runnable r) {
        mQueue.removeMessages(r, null);
    }

    public final void removeCallbacks(final Runnable r, final Object token) {
        mQueue.removeMessages(r, token);
    }

    public final boolean sendMessage(final Message msg) {
        return sendMessageDelayed(msg, 0);
    }

    public final boolean sendEmptyMessage(final int what) {
        return sendEmptyMessageDelayed(what, 0);
    }

    public final boolean sendEmptyMessageDelayed(final int what, final long delayMillis) {
        return sendMessageDelayed(Message.obtain(), delayMillis);
    }

    public final boolean sendEmptyMessageAtTime(final int what, final long uptimeMillis) {
        return sendMessageAtTime(Message.obtain(), uptimeMillis);
    }

    public final boolean sendMessageDelayed(final Message msg, long delayMillis) {
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        return sendMessageAtTime(msg, System.currentTimeMillis() + delayMillis);
    }

    public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
        MessageQueue queue = mQueue;
        if (queue == null) {
            return false;
        }
        return enqueueMessage(queue, msg, uptimeMillis);
    }

    public final boolean sendMessageAtFrontOfQueue(Message msg) {
        MessageQueue queue = mQueue;
        if (queue == null) {
            return false;
        }
        return enqueueMessage(queue, msg, 0);
    }

    private boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis) {
        return queue.enqueueMessage(msg, uptimeMillis);
    }

    public final void removeMessages(int what) {
        mQueue.removeMessages(null);
    }

    public final void removeMessages(int what, Object object) {
        mQueue.removeMessages(object);
    }

    public final void removeCallbacksAndMessages(Object data) {
        mQueue.removeCallbacksAndMessages(data);
    }

    public final boolean hasMessages() {
        return mQueue.hasMessages(null);
    }

    public final boolean hasMessages(Object object) {
        return mQueue.hasMessages(object);
    }

    public final boolean hasCallbacks(Runnable r) {
        return mQueue.hasMessages(r, null);
    }

    public final Consumer getLooper() {
        return mConsumer;
    }

    private static Message getPostMessage(Runnable r) {
        Message m = Message.obtain();
        m.setCallback(r);
        return m;
    }

    // Normal Thread constructors
    public ConsumerThread() {
    }

    public ConsumerThread(final Runnable target) {
        super(target);
    }

    public ConsumerThread(final ThreadGroup group, final Runnable target) {
        super(group, target);
    }

    public ConsumerThread(final String name) {
        super(name);
    }

    public ConsumerThread(final ThreadGroup group, final String name) {
        super(group, name);
    }

    public ConsumerThread(final Runnable target, final String name) {
        super(target, name);
    }

    public ConsumerThread(final ThreadGroup group, final Runnable target, final String name) {
        super(group, target, name);
    }

    public ConsumerThread(final ThreadGroup group, final Runnable target, final String name, final long stackSize) {
        super(group, target, name, stackSize);
    }

    protected void onMessage(final Message msg) {

    }
}