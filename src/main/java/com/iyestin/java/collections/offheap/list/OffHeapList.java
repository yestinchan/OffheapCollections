package com.iyestin.java.collections.offheap.list;

import com.iyestin.java.collections.offheap.array.OffHeapArray;

/**
 * Off-heap list
 */
public interface OffHeapList<T> extends OffHeapArray<T> {

    void add(T t);

    void addInBytes(byte[] bytes);

    int remove(int index);

    int indexOf(T t);

    int indexOfInBytes(byte[] bytes);

    int remove(T t);

    int removeInBytes(byte[] bytes);

    void compact();
}
