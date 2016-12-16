package com.iyestin.java.collections.offheap.queue;

/**
 *
 */
public interface OffHeapQueue<T>{

    int size();

    void put(T t);

    void putInBytes(byte[] bytes);

    T take();

    byte[] takeInBytes();

    T peek();

    byte[] peekInBytes();
}
