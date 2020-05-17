package com.anmol.demo;

import com.anmol.apps.ConsumerThread;
import com.anmol.apps.Publisher;

public class MainThread extends ConsumerThread {

    private ConsumerThread toSendDataTo;

    public MainThread(final String name) {
        super(name);
    }

    @Override
    protected void onRun() {
        new Publisher(toSendDataTo).post(() -> System.out.println("Sent by " + getName() + ", and Received by: " + Thread.currentThread().getName()));
    }

    public void setToSendDataTo(final ConsumerThread toSendDataTo) {
        this.toSendDataTo = toSendDataTo;
    }
}
