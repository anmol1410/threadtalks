package com.anmol.apps;

import java.io.Serializable;

public final class Message implements Serializable {
    public int what;

    public int arg1;

    public int arg2;

    public Object obj;

    public int sendingUid = -1;

    private static final int FLAG_IN_USE = 1;
    private static final int FLAGS_TO_CLEAR_ON_COPY_FROM = FLAG_IN_USE;

    private int flags;

    private long when;

    private Object data;

    private Publisher target;

    private Runnable callback;

    // sometimes we store linked lists of these things
    /*package*/ Message next;

    private static final Object sPoolSync = new Object();
    private static Message sPool;
    private static int sPoolSize = 0;

    private static final int MAX_POOL_SIZE = 50;

    public static Message obtain() {
        synchronized (sPoolSync) {
            if (sPool != null) {
                Message m = sPool;
                sPool = m.next;
                m.next = null;
                m.flags = 0; // clear in-use flag
                sPoolSize--;
                return m;
            }
        }
        return new Message();
    }

    public static Message obtain(Message orig) {
        Message m = obtain();
        m.what = orig.what;
        m.arg1 = orig.arg1;
        m.arg2 = orig.arg2;
        m.obj = orig.obj;
        m.sendingUid = orig.sendingUid;
        if (orig.data != null) {
            m.data = orig.data;
        }
        m.target = orig.target;
        m.callback = orig.callback;

        return m;
    }

    public static Message obtain(Publisher h) {
        Message m = obtain();
        m.target = h;
        return m;
    }

    public static Message obtain(Publisher h, Runnable callback) {
        Message m = obtain();
        m.target = h;
        m.callback = callback;

        return m;
    }

    public static Message obtain(Publisher h, int what) {
        Message m = obtain();
        m.target = h;
        m.what = what;

        return m;
    }

    public static Message obtain(Publisher h, int what, Object obj) {
        Message m = obtain();
        m.target = h;
        m.what = what;
        m.obj = obj;

        return m;
    }

    public static Message obtain(Publisher h, int what, int arg1, int arg2) {
        Message m = obtain();
        m.target = h;
        m.what = what;
        m.arg1 = arg1;
        m.arg2 = arg2;

        return m;
    }

    public static Message obtain(Publisher h, int what,
                                 int arg1, int arg2, Object obj) {
        Message m = obtain();
        m.target = h;
        m.what = what;
        m.arg1 = arg1;
        m.arg2 = arg2;
        m.obj = obj;

        return m;
    }

    public void recycle() {
        if (isInUse()) {
            throw new IllegalStateException("This message cannot be recycled because it is still in use.");
        }
        recycleUnchecked();
    }

    void recycleUnchecked() {
        flags = FLAG_IN_USE;
        what = 0;
        arg1 = 0;
        arg2 = 0;
        obj = null;

        sendingUid = -1;
        when = 0;
        target = null;
        callback = null;
        data = null;

        synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
                next = sPool;
                sPool = this;
                sPoolSize++;
            }
        }
    }

    public void copyFrom(Message o) {
        this.flags = o.flags & ~FLAGS_TO_CLEAR_ON_COPY_FROM;
        this.what = o.what;
        this.arg1 = o.arg1;
        this.arg2 = o.arg2;
        this.obj = o.obj;

        this.sendingUid = o.sendingUid;

        if (o.data != null) {
            this.data = o.data;
        } else {
            this.data = null;
        }
    }

    public long when() {
        return when;
    }

    public void setWhen(final long when) {
        this.when = when;
    }

    public void setTarget(Publisher target) {
        this.target = target;
    }

    public Publisher target() {
        return target;
    }

    public Runnable callback() {
        return callback;
    }

    public void setCallback(final Runnable callback) {
        this.callback = callback;
    }

    public Object getData() {
        return data;
    }

    public Object peekData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void sendToTarget() {
        target.sendMessage(this);
    }

    public boolean isInUse() {
        return ((flags & FLAG_IN_USE) == FLAG_IN_USE);
    }

    public void markInUse() {
        flags |= FLAG_IN_USE;
    }

    private Message() {
    }
}
