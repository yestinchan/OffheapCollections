package com.iyestin.java.collections.offheap.array;

import com.iyestin.java.collections.offheap.OffHeapSerializer;
import com.iyestin.java.collections.offheap.base.DirectByteBuffer;

/**
 * Simple off-heap array.
 */
public class SimpleOffHeapArray<T> extends AbstractOffHeapArray<T> {

    private static final int LENGTH_SIZE = 4;

    private int capacity = -1;

    private DirectByteBuffer byteBuffer;


    public SimpleOffHeapArray(int elements, long totalSize, OffHeapSerializer<T> serializer) {
        super(serializer);
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public byte[] getInBytes(int index) {
        return new byte[0];
    }

    @Override
    public void putInBytes(int index, byte[] bytes) {

    }
}
