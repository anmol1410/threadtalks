package com.anmol.demo;

import com.anmol.apps.Handler;
import com.anmol.apps.HandlerThread;

public class MainThread extends HandlerThread {
    public MainThread(final String name) {
        super(name);
    }

    @Override
    protected void onLooperPrepared() {

        new Handler(msg -> {
            System.out.println("Inside OnLooperPrep" + msg);
            return true;
        });
    }
}
