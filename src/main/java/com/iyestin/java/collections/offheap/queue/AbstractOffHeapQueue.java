package com.iyestin.java.collections.offheap.queue;

import com.iyestin.java.collections.offheap.OffHeapSerializer;

/**
 * Abstract off-heap queue
 */
public abstract class AbstractOffHeapQueue<T> implements OffHeapQueue<T> {

    protected OffHeapSerializer<T> serializer;

    public AbstractOffHeapQueue(OffHeapSerializer<T> serializer) {
        this.serializer = serializer;
    }

    @Override
    public void put(T t) {
        putInBytes(serializer.serialize(t));
    }

    @Override
    public T take() {
        byte[] result = takeInBytes();
        return result == null ? null : serializer.deserialize(result);
    }

    @Override
    public T peek() {
        byte[] result = peekInBytes();
        return result == null ? null : serializer.deserialize(result);
    }
}
