package com.anmol.apps;

public final class MessageQueue {

    private Message mMessages;
    private boolean mStopping;

    Message next() {
        for (; ; ) {
            synchronized (this) {
                // Try to retrieve the next message.  Return if found.
                final long now = System.currentTimeMillis();
                final Message msg = mMessages;
                if (msg != null) {
                    if (now >= msg.when()) {
                        // Got a message.
                        mMessages = msg.next;
                        msg.next = null;
                        msg.markInUse();
                        return msg;
                    }
                }

                if (mStopping) {
                    return null;
                }
            }
        }
    }

    void stop(final boolean safe) {
        synchronized (this) {
            if (mStopping) {
                return;
            }
            mStopping = true;

            if (safe) {
                removeAllFutureMessagesLocked();
            } else {
                removeAllMessagesLocked();
            }

            // We can assume mPtr != 0 because mStopping was previously false.
            notifyAll();
        }
    }

    boolean enqueueMessage(Message msg, long when) {
        if (msg.target() == null) {
            throw new IllegalArgumentException("Message must have a target().");
        }
        if (msg.isInUse()) {
            throw new IllegalStateException(msg + " This message is already in use.");
        }

        synchronized (this) {
            if (mStopping) {
                msg.recycle();
                return false;
            }

            msg.markInUse();
            msg.setWhen(when);
            Message p = mMessages;
            if (p == null || when == 0 || when < p.when()) {
                msg.next = p;
                mMessages = msg;
            } else {
                Message prev;
                do {
                    prev = p;
                    p = p.next;
                } while (p != null && when >= p.when());
                msg.next = p;
                prev.next = msg;
            }
            notifyAll();
        }
        return true;
    }

    boolean hasMessages(Publisher h, int what, Object object) {
        if (h == null) {
            return false;
        }
        synchronized (this) {
            Message p = mMessages;
            while (p != null) {
                if (p.target() == h && p.what == what && (object == null || p.obj == object)) {
                    return true;
                }
                p = p.next;
            }
            return false;
        }
    }

    boolean hasMessages(Publisher h, Runnable r, Object object) {
        if (h == null) {
            return false;
        }

        synchronized (this) {
            Message p = mMessages;
            while (p != null) {
                if (p.target() == h && p.callback() == r && (object == null || p.obj == object)) {
                    return true;
                }
                p = p.next;
            }
            return false;
        }
    }

    void removeMessages(Publisher h, int what, Object object) {
        if (h == null) {
            return;
        }

        synchronized (this) {
            Message p = mMessages;

            // Remove all messages at front.
            while (p != null && p.target() == h && p.what == what
                    && (object == null || p.obj == object)) {
                Message n = p.next;
                mMessages = n;
                p.recycleUnchecked();
                p = n;
            }

            // Remove all messages after front.
            while (p != null) {
                Message n = p.next;
                if (n != null) {
                    if (n.target() == h && n.what == what
                            && (object == null || n.obj == object)) {
                        Message nn = n.next;
                        n.recycleUnchecked();
                        p.next = nn;
                        continue;
                    }
                }
                p = n;
            }
        }
    }

    void removeMessages(Publisher h, Runnable r, Object object) {
        if (h == null || r == null) {
            return;
        }

        synchronized (this) {
            Message p = mMessages;

            // Remove all messages at front.
            while (p != null && p.target() == h && p.callback() == r
                    && (object == null || p.obj == object)) {
                Message n = p.next;
                mMessages = n;
                p.recycleUnchecked();
                p = n;
            }

            // Remove all messages after front.
            while (p != null) {
                Message n = p.next;
                if (n != null) {
                    if (n.target() == h && n.callback() == r
                            && (object == null || n.obj == object)) {
                        Message nn = n.next;
                        n.recycleUnchecked();
                        p.next = nn;
                        continue;
                    }
                }
                p = n;
            }
        }
    }

    void removeCallbacksAndMessages(Publisher h, Object object) {
        if (h == null) {
            return;
        }

        synchronized (this) {
            Message p = mMessages;

            // Remove all messages at front.
            while (p != null && p.target() == h
                    && (object == null || p.obj == object)) {
                Message n = p.next;
                mMessages = n;
                p.recycleUnchecked();
                p = n;
            }

            // Remove all messages after front.
            while (p != null) {
                Message n = p.next;
                if (n != null) {
                    if (n.target() == h && (object == null || n.obj == object)) {
                        Message nn = n.next;
                        n.recycleUnchecked();
                        p.next = nn;
                        continue;
                    }
                }
                p = n;
            }
        }
    }

    private void removeAllMessagesLocked() {
        Message p = mMessages;
        while (p != null) {
            Message n = p.next;
            p.recycleUnchecked();
            p = n;
        }
        mMessages = null;
    }

    private void removeAllFutureMessagesLocked() {
        final long now = System.currentTimeMillis();
        Message p = mMessages;
        if (p != null) {
            if (p.when() > now) {
                removeAllMessagesLocked();
            } else {
                Message n;
                for (; ; ) {
                    n = p.next;
                    if (n == null) {
                        return;
                    }
                    if (n.when() > now) {
                        break;
                    }
                    p = n;
                }
                p.next = null;
                do {
                    p = n;
                    n = p.next;
                    p.recycleUnchecked();
                } while (n != null);
            }
        }
    }
}