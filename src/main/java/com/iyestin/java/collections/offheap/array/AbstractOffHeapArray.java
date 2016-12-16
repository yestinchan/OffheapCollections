package com.iyestin.java.collections.offheap.array;

import com.iyestin.java.collections.offheap.OffHeapSerializer;

/**
 * Abstract off-heap array.
 */
public abstract class AbstractOffHeapArray<T> implements OffHeapArray<T> {

    protected OffHeapSerializer<T> serializer;

    public AbstractOffHeapArray(OffHeapSerializer<T> serializer) {
        this.serializer = serializer;
    }

    @Override
    public T get(int index) {
        return serializer.deserialize(getInBytes(index));
    }

    @Override
    public void put(int index, T t) {
        putInBytes(index, serializer.serialize(t));
    }

}
