package com.iyestin.java.collections.offheap.list;

import com.iyestin.java.collections.offheap.OffHeapSerializer;

/**
 * Created by yestin on 2016/12/16.
 */
public abstract class AbstractOffHeapList<T> implements OffHeapList<T> {

    protected OffHeapSerializer<T> serializer;

    public AbstractOffHeapList(OffHeapSerializer<T> serializer) {
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

    @Override
    public void add(T t) {
        addInBytes(serializer.serialize(t));
    }

    @Override
    public int indexOf(T t) {
        return indexOfInBytes(serializer.serialize(t));
    }

    @Override
    public int remove(T t) {
        return removeInBytes(serializer.serialize(t));
    }
}
