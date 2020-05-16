package com.anmol.apps;

import java.lang.reflect.Modifier;

/**
 * A base.Handler allows you to send and process {@link Message} and Runnable
 * objects associated with a thread's {@link MessageQueue}.  Each base.Handler
 * instance is associated with a single thread and that thread's message
 * queue.  When you create a new base.Handler, it is bound to the thread /
 * message queue of the thread that is creating it -- from that point on,
 * it will deliver messages and runnables to that message queue and execute
 * them as they come out of the message queue.
 *
 * <p>There are two main uses for a base.Handler: (1) to schedule messages and
 * runnables to be executed as some point in the future; and (2) to enqueue
 * an action to be performed on a different thread than your own.
 *
 * <p>Scheduling messages is accomplished with the
 * {@link #post}, {@link #postAtTime(Runnable, long)},
 * {@link #postDelayed}, {@link #sendEmptyMessage},
 * {@link #sendMessage}, {@link #sendMessageAtTime}, and
 * {@link #sendMessageDelayed} methods.  The <em>post</em> versions allow
 * you to enqueue Runnable objects to be called by the message queue when
 * they are received; the <em>sendMessage</em> versions allow you to enqueue
 * a {@link Message} object containing a bundle of data that will be
 * processed by the base.Handler's {@link #handleMessage} method (requiring that
 * you implement a subclass of base.Handler).
 *
 * <p>When posting or sending to a base.Handler, you can either
 * allow the item to be processed as soon as the message queue is ready
 * to do so, or specify a delay before it gets processed or absolute time for
 * it to be processed.  The latter two allow you to implement timeouts,
 * ticks, and other timing-based behavior.
 *
 * <p>When a
 * process is created for your application, its main thread is dedicated to
 * running a message queue that takes care of managing the top-level
 * application objects (activities, broadcast receivers, etc) and any windows
 * they create.  You can create your own threads, and communicate back with
 * the main application thread through a base.Handler.  This is done by calling
 * the same <em>post</em> or <em>sendMessage</em> methods as before, but from
 * your new thread.  The given Runnable or base.Message will then be scheduled
 * in the base.Handler's message queue and processed when appropriate.
 */
public class Handler {
    /*
     * Set this flag to true to detect anonymous, local or member classes
     * that extend this base.Handler class and that are not static. These kind
     * of classes can potentially create leaks.
     */
    private static final boolean FIND_POTENTIAL_LEAKS = false;
    private static final String TAG = "base.Handler";

    /**
     * Callback interface you can use when instantiating a base.Handler to avoid
     * having to implement your own subclass of base.Handler.
     * <p>
     * android.os.base.Message base.Message
     *
     * @return True if no further handling is desired
     */
    public interface Callback {
        public boolean handleMessage(Message msg);
    }

    /**
     * Subclasses must implement this to receive messages.
     */
    public void handleMessage(Message msg) {
    }

    /**
     * Handle system messages here.
     */
    public void dispatchMessage(Message msg) {
        if (msg.callback != null) {
            handleCallback(msg);
        } else {
            if (mCallback != null) {
                if (mCallback.handleMessage(msg)) {
                    return;
                }
            }
            handleMessage(msg);
        }
    }

    /**
     * Default constructor associates this handler with the {@link Looper} for the
     * current thread.
     * <p>
     * If this thread does not have a looper, this handler won't be able to receive messages
     * so an exception is thrown.
     */
    public Handler() {
        this(null, false);
    }

    /**
     * Constructor associates this handler with the {@link Looper} for the
     * current thread and takes a callback interface in which you can handle
     * messages.
     * <p>
     * If this thread does not have a looper, this handler won't be able to receive messages
     * so an exception is thrown.
     *
     * @param callback The callback interface in which to handle messages, or null.
     */
    public Handler(Callback callback) {
        this(callback, false);
    }

    /**
     * Use the provided {@link Looper} instead of the default one.
     *
     * @param looper The looper, must not be null.
     */
    public Handler(Looper looper) {
        this(looper, null, false);
    }

    /**
     * Use the provided {@link Looper} instead of the default one and take a callback
     * interface in which to handle messages.
     *
     * @param looper   The looper, must not be null.
     * @param callback The callback interface in which to handle messages, or null.
     */
    public Handler(Looper looper, Callback callback) {
        this(looper, callback, false);
    }

    /**
     * Use the {@link Looper} for the current thread
     * and set whether the handler should be asynchronous.
     * <p>
     * Handlers are synchronous by default unless this constructor is used to make
     * one that is strictly asynchronous.
     * <p>
     * Asynchronous messages represent interrupts or events that do not require global ordering
     * with respect to synchronous messages.  Asynchronous messages are not subject to
     * the synchronization barriers introduced by base.MessageQueue#enqueueSyncBarrier(long).
     *
     * @param async If true, the handler calls {@link Message#setAsynchronous(boolean)} for
     *              each {@link Message} that is sent to it or {@link Runnable} that is posted to it.
     * @hide
     */
    public Handler(boolean async) {
        this(null, async);
    }

    /**
     * Use the {@link Looper} for the current thread with the specified callback interface
     * and set whether the handler should be asynchronous.
     * <p>
     * Handlers are synchronous by default unless this constructor is used to make
     * one that is strictly asynchronous.
     * <p>
     * Asynchronous messages represent interrupts or events that do not require global ordering
     * with respect to synchronous messages.  Asynchronous messages are not subject to
     * the synchronization barriers introduced by base.MessageQueue#enqueueSyncBarrier(long).
     *
     * @param callback The callback interface in which to handle messages, or null.
     * @param async    If true, the handler calls {@link Message#setAsynchronous(boolean)} for
     *                 each {@link Message} that is sent to it or {@link Runnable} that is posted to it.
     * @hide
     */
    public Handler(Callback callback, boolean async) {
        if (FIND_POTENTIAL_LEAKS) {
            final Class<? extends Handler> klass = getClass();
            if ((klass.isAnonymousClass() || klass.isMemberClass() || klass.isLocalClass()) &&
                    (klass.getModifiers() & Modifier.STATIC) == 0) {
                Log.w(TAG, "The following base.Handler class should be static or leaks might occur: " +
                        klass.getCanonicalName());
            }
        }

        mLooper = Looper.myLooper();
        if (mLooper == null) {
            throw new RuntimeException(
                    "Can't create handler inside thread that has not called base.Looper.prepare()");
        }
        mQueue = mLooper.mQueue;
        mCallback = callback;
        mAsynchronous = async;
    }

    /**
     * Use the provided {@link Looper} instead of the default one and take a callback
     * interface in which to handle messages.  Also set whether the handler
     * should be asynchronous.
     * <p>
     * Handlers are synchronous by default unless this constructor is used to make
     * one that is strictly asynchronous.
     * <p>
     * Asynchronous messages represent interrupts or events that do not require global ordering
     * with respect to synchronous messages.  Asynchronous messages are not subject to
     * the synchronization barriers introduced by base.MessageQueue#enqueueSyncBarrier(long).
     *
     * @param looper   The looper, must not be null.
     * @param callback The callback interface in which to handle messages, or null.
     * @param async    If true, the handler calls {@link Message#setAsynchronous(boolean)} for
     *                 each {@link Message} that is sent to it or {@link Runnable} that is posted to it.
     * @hide
     */
    public Handler(Looper looper, Callback callback, boolean async) {
        mLooper = looper;
        mQueue = looper.mQueue;
        mCallback = callback;
        mAsynchronous = async;
    }

    /**
     * {@hide}
     */
    public String getTraceName(Message message) {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName()).append(": ");
        if (message.callback != null) {
            sb.append(message.callback.getClass().getName());
        } else {
            sb.append("#").append(message.what);
        }
        return sb.toString();
    }

    /**
     * Returns a string representing the name of the specified message.
     * The default implementation will either return the class name of the
     * message callback if any, or the hexadecimal representation of the
     * message "what" field.
     *
     * @param message The message whose name is being queried
     */
    public String getMessageName(Message message) {
        if (message.callback != null) {
            return message.callback.getClass().getName();
        }
        return "0x" + Integer.toHexString(message.what);
    }

    /**
     * Returns a new base.Message base.Message from the global message pool. More efficient than
     * creating and allocating new instances. The retrieved message has its handler set to this instance (base.Message.target == this).
     * If you don't want that facility, just call base.Message.obtain() instead.
     */
    public final Message obtainMessage() {
        return Message.obtain(this);
    }

    /**
     * Same as {@link #obtainMessage()}, except that it also sets the what member of the returned base.Message.
     *
     * @param what Value to assign to the returned base.Message.what field.
     * @return A base.Message from the global message pool.
     */
    public final Message obtainMessage(int what) {
        return Message.obtain(this, what);
    }

    /**
     * Same as {@link #obtainMessage()}, except that it also sets the what and obj members
     * of the returned base.Message.
     *
     * @param what Value to assign to the returned base.Message.what field.
     * @param obj  Value to assign to the returned base.Message.obj field.
     * @return A base.Message from the global message pool.
     */
    public final Message obtainMessage(int what, Object obj) {
        return Message.obtain(this, what, obj);
    }

    /**
     * Same as {@link #obtainMessage()}, except that it also sets the what, arg1 and arg2 members of the returned
     * base.Message.
     *
     * @param what Value to assign to the returned base.Message.what field.
     * @param arg1 Value to assign to the returned base.Message.arg1 field.
     * @param arg2 Value to assign to the returned base.Message.arg2 field.
     * @return A base.Message from the global message pool.
     */
    public final Message obtainMessage(int what, int arg1, int arg2) {
        return Message.obtain(this, what, arg1, arg2);
    }

    /**
     * Same as {@link #obtainMessage()}, except that it also sets the what, obj, arg1,and arg2 values on the
     * returned base.Message.
     *
     * @param what Value to assign to the returned base.Message.what field.
     * @param arg1 Value to assign to the returned base.Message.arg1 field.
     * @param arg2 Value to assign to the returned base.Message.arg2 field.
     * @param obj  Value to assign to the returned base.Message.obj field.
     * @return A base.Message from the global message pool.
     */
    public final Message obtainMessage(int what, int arg1, int arg2, Object obj) {
        return Message.obtain(this, what, arg1, arg2, obj);
    }

    /**
     * Causes the Runnable r to be added to the message queue.
     * The runnable will be run on the thread to which this handler is
     * attached.
     *
     * @param r The Runnable that will be executed.
     * @return Returns true if the Runnable was successfully placed in to the
     * message queue.  Returns false on failure, usually because the
     * looper processing the message queue is exiting.
     */
    public final boolean post(Runnable r) {
        return sendMessageDelayed(getPostMessage(r), 0);
    }

    /**
     * Causes the Runnable r to be added to the message queue, to be run
     * at a specific time given by <var>uptimeMillis</var>.
     * <b>The time-base is SystemClock#uptimeMillis.</b>
     * Time spent in deep sleep will add an additional delay to execution.
     * The runnable will be run on the thread to which this handler is attached.
     *
     * @param r            The Runnable that will be executed.
     * @param uptimeMillis The absolute time at which the callback should run,
     *                     using theSystemClock#uptimeMillis time-base.
     * @return Returns true if the Runnable was successfully placed in to the
     * message queue.  Returns false on failure, usually because the
     * looper processing the message queue is exiting.  Note that a
     * result of true does not mean the Runnable will be processed -- if
     * the looper is quit before the delivery time of the message
     * occurs then the message will be dropped.
     */
    public final boolean postAtTime(Runnable r, long uptimeMillis) {
        return sendMessageAtTime(getPostMessage(r), uptimeMillis);
    }

    /**
     * Causes the Runnable r to be added to the message queue, to be run
     * at a specific time given by <var>uptimeMillis</var>.
     * <b>The time-base is SystemClock#uptimeMillis.</b>
     * Time spent in deep sleep will add an additional delay to execution.
     * The runnable will be run on the thread to which this handler is attached.
     *
     * @param r            The Runnable that will be executed.
     * @param uptimeMillis The absolute time at which the callback should run,
     *                     using the SystemClock#uptimeMillis time-base.
     * @return Returns true if the Runnable was successfully placed in to the
     * message queue.  Returns false on failure, usually because the
     * looper processing the message queue is exiting.  Note that a
     * result of true does not mean the Runnable will be processed -- if
     * the looper is quit before the delivery time of the message
     * occurs then the message will be dropped.
     */
    public final boolean postAtTime(Runnable r, Object token, long uptimeMillis) {
        return sendMessageAtTime(getPostMessage(r, token), uptimeMillis);
    }

    /**
     * Causes the Runnable r to be added to the message queue, to be run
     * after the specified amount of time elapses.
     * The runnable will be run on the thread to which this handler
     * is attached.
     * <b>The time-base is SystemClock#uptimeMillis.</b>
     * Time spent in deep sleep will add an additional delay to execution.
     *
     * @param r           The Runnable that will be executed.
     * @param delayMillis The delay (in milliseconds) until the Runnable
     *                    will be executed.
     * @return Returns true if the Runnable was successfully placed in to the
     * message queue.  Returns false on failure, usually because the
     * looper processing the message queue is exiting.  Note that a
     * result of true does not mean the Runnable will be processed --
     * if the looper is quit before the delivery time of the message
     * occurs then the message will be dropped.
     */
    public final boolean postDelayed(Runnable r, long delayMillis) {
        return sendMessageDelayed(getPostMessage(r), delayMillis);
    }

    /**
     * Posts a message to an object that implements Runnable.
     * Causes the Runnable r to executed on the next iteration through the
     * message queue. The runnable will be run on the thread to which this
     * handler is attached.
     * <b>This method is only for use in very special circumstances -- it
     * can easily starve the message queue, cause ordering problems, or have
     * other unexpected side-effects.</b>
     *
     * @param r The Runnable that will be executed.
     * @return Returns true if the message was successfully placed in to the
     * message queue.  Returns false on failure, usually because the
     * looper processing the message queue is exiting.
     */
    public final boolean postAtFrontOfQueue(Runnable r) {
        return sendMessageAtFrontOfQueue(getPostMessage(r));
    }

    /**
     * Runs the specified task synchronously.
     * <p>
     * If the current thread is the same as the handler thread, then the runnable
     * runs immediately without being enqueued.  Otherwise, posts the runnable
     * to the handler and waits for it to complete before returning.
     * </p><p>
     * This method is dangerous!  Improper use can result in deadlocks.
     * Never call this method while any locks are held or use it in a
     * possibly re-entrant manner.
     * </p><p>
     * This method is occasionally useful in situations where a background thread
     * must synchronously await completion of a task that must run on the
     * handler's thread.  However, this problem is often a symptom of bad design.
     * Consider improving the design (if possible) before resorting to this method.
     * </p><p>
     * One example of where you might want to use this method is when you just
     * set up a base.Handler thread and need to perform some initialization steps on
     * it before continuing execution.
     * </p><p>
     * If timeout occurs then this method returns <code>false</code> but the runnable
     * will remain posted on the handler and may already be in progress or
     * complete at a later time.
     * </p><p>
     * When using this method, be sure to use {@link Looper#quitSafely} when
     * quitting the looper.  Otherwise {@link #runWithScissors} may hang indefinitely.
     * (TODO: We should fix this by making base.MessageQueue aware of blocking runnables.)
     * </p>
     *
     * @param r       The Runnable that will be executed synchronously.
     * @param timeout The timeout in milliseconds, or 0 to wait indefinitely.
     * @return Returns true if the Runnable was successfully executed.
     * Returns false on failure, usually because the
     * looper processing the message queue is exiting.
     * @hide This method is prone to abuse and should probably not be in the API.
     * If we ever do make it part of the API, we might want to rename it to something
     * less funny like runUnsafe().
     */
    public final boolean runWithScissors(final Runnable r, long timeout) {
        if (r == null) {
            throw new IllegalArgumentException("runnable must not be null");
        }
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout must be non-negative");
        }

        if (Looper.myLooper() == mLooper) {
            r.run();
            return true;
        }

        BlockingRunnable br = new BlockingRunnable(r);
        return br.postAndWait(this, timeout);
    }

    /**
     * Remove any pending posts of Runnable r that are in the message queue.
     */
    public final void removeCallbacks(Runnable r) {
        mQueue.removeMessages(this, r, null);
    }

    /**
     * Remove any pending posts of Runnable <var>r</var> with Object
     * <var>token</var> that are in the message queue.  If <var>token</var> is null,
     * all callbacks will be removed.
     */
    public final void removeCallbacks(Runnable r, Object token) {
        mQueue.removeMessages(this, r, token);
    }

    /**
     * Pushes a message onto the end of the message queue after all pending messages
     * before the current time. It will be received in {@link #handleMessage},
     * in the thread attached to this handler.
     *
     * @return Returns true if the message was successfully placed in to the
     * message queue.  Returns false on failure, usually because the
     * looper processing the message queue is exiting.
     */
    public final boolean sendMessage(Message msg) {
        return sendMessageDelayed(msg, 0);
    }

    /**
     * Sends a base.Message containing only the what value.
     *
     * @return Returns true if the message was successfully placed in to the
     * message queue.  Returns false on failure, usually because the
     * looper processing the message queue is exiting.
     */
    public final boolean sendEmptyMessage(int what) {
        return sendEmptyMessageDelayed(what, 0);
    }

    /**
     * Sends a base.Message containing only the what value, to be delivered
     * after the specified amount of time elapses.
     *
     * @return Returns true if the message was successfully placed in to the
     * message queue.  Returns false on failure, usually because the
     * looper processing the message queue is exiting.
     */
    public final boolean sendEmptyMessageDelayed(int what, long delayMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        return sendMessageDelayed(msg, delayMillis);
    }

    /**
     * Sends a base.Message containing only the what value, to be delivered
     * at a specific time.
     *
     * @return Returns true if the message was successfully placed in to the
     * message queue.  Returns false on failure, usually because the
     * looper processing the message queue is exiting.
     */

    public final boolean sendEmptyMessageAtTime(int what, long uptimeMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        return sendMessageAtTime(msg, uptimeMillis);
    }

    /**
     * Enqueue a message into the message queue after all pending messages
     * before (current time + delayMillis). You will receive it in
     * {@link #handleMessage}, in the thread attached to this handler.
     *
     * @return Returns true if the message was successfully placed in to the
     * message queue.  Returns false on failure, usually because the
     * looper processing the message queue is exiting.  Note that a
     * result of true does not mean the message will be processed -- if
     * the looper is quit before the delivery time of the message
     * occurs then the message will be dropped.
     */
    public final boolean sendMessageDelayed(Message msg, long delayMillis) {
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        return sendMessageAtTime(msg, System.currentTimeMillis() + delayMillis);
    }

    /**
     * Enqueue a message into the message queue after all pending messages
     * before the absolute time (in milliseconds) <var>uptimeMillis</var>.
     * <b>The time-base is SystemClock#uptimeMillis.</b>
     * Time spent in deep sleep will add an additional delay to execution.
     * You will receive it in {@link #handleMessage}, in the thread attached
     * to this handler.
     *
     * @param uptimeMillis The absolute time at which the message should be
     *                     delivered, using the
     *                     SystemClock#uptimeMillis time-base.
     * @return Returns true if the message was successfully placed in to the
     * message queue.  Returns false on failure, usually because the
     * looper processing the message queue is exiting.  Note that a
     * result of true does not mean the message will be processed -- if
     * the looper is quit before the delivery time of the message
     * occurs then the message will be dropped.
     */
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

    /**
     * Enqueue a message at the front of the message queue, to be processed on
     * the next iteration of the message loop.  You will receive it in
     * {@link #handleMessage}, in the thread attached to this handler.
     * <b>This method is only for use in very special circumstances -- it
     * can easily starve the message queue, cause ordering problems, or have
     * other unexpected side-effects.</b>
     *
     * @return Returns true if the message was successfully placed in to the
     * message queue.  Returns false on failure, usually because the
     * looper processing the message queue is exiting.
     */
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
        msg.target = this;
        if (mAsynchronous) {
            msg.setAsynchronous(true);
        }
        return queue.enqueueMessage(msg, uptimeMillis);
    }

    /**
     * Remove any pending posts of messages with code 'what' that are in the
     * message queue.
     */
    public final void removeMessages(int what) {
        mQueue.removeMessages(this, what, null);
    }

    /**
     * Remove any pending posts of messages with code 'what' and whose obj is
     * 'object' that are in the message queue.  If <var>object</var> is null,
     * all messages will be removed.
     */
    public final void removeMessages(int what, Object object) {
        mQueue.removeMessages(this, what, object);
    }

    /**
     * Remove any pending posts of callbacks and sent messages whose
     * <var>obj</var> is <var>token</var>.  If <var>token</var> is null,
     * all callbacks and messages will be removed.
     */
    public final void removeCallbacksAndMessages(Object token) {
        mQueue.removeCallbacksAndMessages(this, token);
    }

    /**
     * Check if there are any pending posts of messages with code 'what' in
     * the message queue.
     */
    public final boolean hasMessages(int what) {
        return mQueue.hasMessages(this, what, null);
    }

    /**
     * Check if there are any pending posts of messages with code 'what' and
     * whose obj is 'object' in the message queue.
     */
    public final boolean hasMessages(int what, Object object) {
        return mQueue.hasMessages(this, what, object);
    }

    /**
     * Check if there are any pending posts of messages with callback r in
     * the message queue.
     *
     * @hide
     */
    public final boolean hasCallbacks(Runnable r) {
        return mQueue.hasMessages(this, r, null);
    }

    // if we can get rid of this method, the handler need not remember its loop
    // we could instead export a getMessageQueue() method... 
    public final Looper getLooper() {
        return mLooper;
    }

//    public final void dump(Printer pw, String prefix) {
//        pw.println(prefix + this + " @ " + SystemClock.uptimeMillis());
//        if (mLooper == null) {
//            pw.println(prefix + "looper uninitialized");
//        } else {
//            mLooper.dump(pw, prefix + "  ");
//        }
//    }

    @Override
    public String toString() {
        return "base.Handler (" + getClass().getName() + ") {"
                + Integer.toHexString(System.identityHashCode(this))
                + "}";
    }

    private static Message getPostMessage(Runnable r) {
        Message m = Message.obtain();
        m.callback = r;
        return m;
    }

    private static Message getPostMessage(Runnable r, Object token) {
        Message m = Message.obtain();
        m.obj = token;
        m.callback = r;
        return m;
    }

    private static void handleCallback(Message message) {
        message.callback.run();
    }

    final Looper mLooper;
    final MessageQueue mQueue;
    final Callback mCallback;
    final boolean mAsynchronous;

    private static final class BlockingRunnable implements Runnable {
        private final Runnable mTask;
        private boolean mDone;

        public BlockingRunnable(Runnable task) {
            mTask = task;
        }

        @Override
        public void run() {
            try {
                mTask.run();
            } finally {
                synchronized (this) {
                    mDone = true;
                    notifyAll();
                }
            }
        }

        public boolean postAndWait(Handler handler, long timeout) {
            if (!handler.post(this)) {
                return false;
            }

            synchronized (this) {
                if (timeout > 0) {
                    final long expirationTime = System.currentTimeMillis() + timeout;
                    while (!mDone) {
                        long delay = expirationTime - System.currentTimeMillis();
                        if (delay <= 0) {
                            return false; // timeout
                        }
                        try {
                            wait(delay);
                        } catch (InterruptedException ex) {
                        }
                    }
                } else {
                    while (!mDone) {
                        try {
                            wait();
                        } catch (InterruptedException ex) {
                        }
                    }
                }
            }
            return true;
        }
    }
}