package com.anmol.apps;

public class Handler {
    private final Looper mLooper;
    private final MessageQueue mQueue;
    private final Callback mCallback;

    public Handler(final Looper looper) {
        this(looper, null);
    }

    public Handler(final Looper looper, final Callback callback) {
        mLooper = looper;
        mQueue = looper.queue();
        mCallback = callback;
    }

    public Handler(final Callback callback) {
        mLooper = Looper.myLooper();
        if (mLooper == null) {
            throw new RuntimeException("Can't create handler inside thread that has not called base.Looper.prepare()");
        }
        mQueue = mLooper.queue();
        mCallback = callback;
    }

    public interface Callback {
        boolean handleMessage(Message msg);
    }

    public void handleMessage(final Message msg) {
    }

    public void dispatchMessage(final Message msg) {
        if (msg.callback() != null) {
            msg.callback().run();
        } else {
            if (mCallback != null) {
                if (mCallback.handleMessage(msg)) {
                    return;
                }
            }
            handleMessage(msg);
        }
    }

    public final Message obtainMessage() {
        return Message.obtain(this);
    }

    public final Message obtainMessage(int what) {
        return Message.obtain(this, what);
    }

    public final Message obtainMessage(int what, Object obj) {
        return Message.obtain(this, what, obj);
    }

    public final Message obtainMessage(int what, int arg1, int arg2) {
        return Message.obtain(this, what, arg1, arg2);
    }

    public final Message obtainMessage(int what, int arg1, int arg2, Object obj) {
        return Message.obtain(this, what, arg1, arg2, obj);
    }

    public final boolean post(final Runnable r) {
        return sendMessageDelayed(getPostMessage(r), 0);
    }

    public final boolean postAtTime(Runnable r, long uptimeMillis) {
        return sendMessageAtTime(getPostMessage(r), uptimeMillis);
    }

    public final boolean postAtTime(Runnable r, Object token, long uptimeMillis) {
        return sendMessageAtTime(getPostMessage(r, token), uptimeMillis);
    }

    public final boolean postDelayed(Runnable r, long delayMillis) {
        return sendMessageDelayed(getPostMessage(r), delayMillis);
    }

    public final boolean postAtFrontOfQueue(Runnable r) {
        return sendMessageAtFrontOfQueue(getPostMessage(r));
    }

    public final void removeCallbacks(final Runnable r) {
        mQueue.removeMessages(this, r, null);
    }

    public final void removeCallbacks(final Runnable r, final Object token) {
        mQueue.removeMessages(this, r, token);
    }

    public final boolean sendMessage(final Message msg) {
        return sendMessageDelayed(msg, 0);
    }

    public final boolean sendEmptyMessage(final int what) {
        return sendEmptyMessageDelayed(what, 0);
    }

    public final boolean sendEmptyMessageDelayed(final int what, final long delayMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        return sendMessageDelayed(msg, delayMillis);
    }

    public final boolean sendEmptyMessageAtTime(final int what, final long uptimeMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        return sendMessageAtTime(msg, uptimeMillis);
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
            RuntimeException e = new RuntimeException(
                    this + " sendMessageAtTime() called with no mQueue");
            Log.w("base.Looper", e.getMessage(), e);
            return false;
        }
        return enqueueMessage(queue, msg, uptimeMillis);
    }

    public final boolean sendMessageAtFrontOfQueue(Message msg) {
        MessageQueue queue = mQueue;
        if (queue == null) {
            RuntimeException e = new RuntimeException(
                    this + " sendMessageAtTime() called with no mQueue");
            Log.w("base.Looper", e.getMessage(), e);
            return false;
        }
        return enqueueMessage(queue, msg, 0);
    }

    private boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis) {
        msg.setTarget(this);
        return queue.enqueueMessage(msg, uptimeMillis);
    }

    public final void removeMessages(int what) {
        mQueue.removeMessages(this, what, null);
    }

    public final void removeMessages(int what, Object object) {
        mQueue.removeMessages(this, what, object);
    }

    public final void removeCallbacksAndMessages(Object token) {
        mQueue.removeCallbacksAndMessages(this, token);
    }

    public final boolean hasMessages(int what) {
        return mQueue.hasMessages(this, what, null);
    }

    public final boolean hasMessages(int what, Object object) {
        return mQueue.hasMessages(this, what, object);
    }

    public final boolean hasCallbacks(Runnable r) {
        return mQueue.hasMessages(this, r, null);
    }

    public final Looper getLooper() {
        return mLooper;
    }

    private static Message getPostMessage(Runnable r) {
        Message m = Message.obtain();
        m.setCallback(r);
        return m;
    }

    private static Message getPostMessage(Runnable r, Object token) {
        Message m = Message.obtain();
        m.obj = token;
        m.setCallback(r);
        return m;
    }
}