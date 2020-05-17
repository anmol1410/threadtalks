package com.anmol.demo;

import com.anmol.apps.ConsumerThread;
import com.anmol.apps.Message;

public class MainThread extends ConsumerThread {

    private ConsumerThread toSendDataTo;

    public MainThread(final String name) {
        super(name);
    }

    @Override
    protected void onRun() {
        toSendDataTo.post(() -> System.out.println("Sent by " + getName() + ", and Received by: " + Thread.currentThread().getName()));
        Message obtain = Message.obtain();
        obtain.setData("Sent by " + getName());
        toSendDataTo.sendMessage(obtain);
    }

    @Override
    protected void onMessage(final Message msg) {
        System.out.println(msg.data() + ", and Received by: " + Thread.currentThread().getName());
    }

    public void setToSendDataTo(final ConsumerThread toSendDataTo) {
        this.toSendDataTo = toSendDataTo;
    }
}
