package com.anmol.apps;

public class ConsumerThread extends Thread {
    private Consumer mConsumer;


    protected void onRun() {
    }

    @Override
    public final void run() {
        Consumer.init();
        synchronized (this) {
            mConsumer = Consumer.myConsumer();
            notifyAll();
        }
        onRun();
        Consumer.consume();
    }

    public Consumer consumer() {
        if (!isAlive()) {
            return null;
        }

        // If the thread has been started, wait until the looper has been created.
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
}