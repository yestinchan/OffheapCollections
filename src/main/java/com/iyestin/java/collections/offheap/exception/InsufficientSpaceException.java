package com.iyestin.java.collections.offheap.exception;

/**
 *
 */
public class InsufficientSpaceException extends RuntimeException {
    public InsufficientSpaceException() {
    }

    public InsufficientSpaceException(String message) {
        super(message);
    }

    public InsufficientSpaceException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientSpaceException(Throwable cause) {
        super(cause);
    }

    public InsufficientSpaceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
