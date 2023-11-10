package edu.utdallas.heartstohearts.network;

import androidx.annotation.Nullable;

/**
 * Convenience method for a one-argument callback for use with asynchronous calls.
 * <p>
 * For example, can be used to call back with the result of a query, or only be called upon hitting
 * an exception.
 *
 * @param <ArgType>: the type of the single argument to the callback
 */
public interface Callback<ArgType> {
    void call(ArgType x);
}

class CallbackUtils {

    /**
     * If an error-handling callback is not null, call it; otherwise rethrow the error as a
     * runtime exception.
     *
     * @param cb
     * @param e
     * @param <T> type of the exception. In most cases can be left implicit.
     */
    public static <T extends Exception> void callOrThrow(@Nullable Callback<T> cb, T e) {
        if (cb == null) {
            throw new RuntimeException(e);
        } else {
            cb.call(e);
        }
    }
}