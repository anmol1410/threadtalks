package com.anmol.apps;

import java.io.Serializable;

public final class Message implements Serializable {

    private static final int FLAG_IN_USE = 1;
    private static final int FLAGS_TO_CLEAR_ON_COPY_FROM = FLAG_IN_USE;
    private int flags;

    private long when;

    private Object data;

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
        final Message m = obtain();
        m.callback = orig.callback;
        if (orig.data != null) {
            m.data = orig.data;
        }
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
        when = 0;
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

        if (o.data != null) {
            this.data = o.data;
        } else {
            this.data = null;
        }
    }

    public Object data() {
        return data;
    }

    public void setData(final Object data) {
        this.data = data;
    }

    public long when() {
        return when;
    }

    public void setWhen(final long when) {
        this.when = when;
    }

    public Runnable callback() {
        return callback;
    }

    public void setCallback(final Runnable callback) {
        this.callback = callback;
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
