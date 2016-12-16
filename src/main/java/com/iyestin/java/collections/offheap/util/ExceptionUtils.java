package com.iyestin.java.collections.offheap.util;

/**
 * Exception util
 */
public class ExceptionUtils {

    /**
     * raise runtime exception if condition triggered.
     * @param condition
     * @param ex
     */
    public static void raiseRuntimeExceptionIf(boolean condition, RuntimeException ex) {
        if (condition) {
            throw ex;
        }
    }

}
