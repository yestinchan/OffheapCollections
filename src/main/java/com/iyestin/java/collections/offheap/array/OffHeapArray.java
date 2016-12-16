package com.iyestin.java.collections.offheap.array;

/**
 * Off-heap array
 */
public interface OffHeapArray<T> {

    int size();

    T get(int index);

    byte[] getInBytes(int index);

    void put(int index, T t);

    void putInBytes(int index, byte[] bytes);
}
