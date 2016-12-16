package com.iyestin.java.collections.offheap.list;

import com.iyestin.java.collections.offheap.OffHeapSerializer;
import com.iyestin.java.collections.offheap.base.DirectByteBuffer;
import com.iyestin.java.collections.offheap.util.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SimpleOffHeapList<T> extends AbstractOffHeapList<T> {

    private static final int LENGTH_SIZE = 4;

    private List<long[]> recyclableSegments = new ArrayList<>();

    private List<Long> elementsPointer = new ArrayList<>();

    private DirectByteBuffer byteBuffer;

    private long tailPointer = 0;

    /**
     *
     * @param sizeInBytes
     * @param serializer
     */
    public SimpleOffHeapList(long sizeInBytes, OffHeapSerializer<T> serializer) {
        super(serializer);
        this.byteBuffer = new DirectByteBuffer(sizeInBytes);
    }

    @Override
    public void addInBytes(byte[] bytes) {
        putInBytes(elementsPointer.size(), bytes);
    }

    @Override
    public int remove(int index) {
        ExceptionUtils.raiseRuntimeExceptionIf(index >= elementsPointer.size(),
                new IndexOutOfBoundsException());
        // mark
        recyclableSegments.add(new long[]{
                // start point.
                elementsPointer.get(index),
                // length.
                (long) LENGTH_SIZE + byteBuffer.getInt(elementsPointer.get(index))
        });
        // remove index
        elementsPointer.remove(index);
        return index;
    }

    @Override
    public int indexOfInBytes(byte[] bytes) {
        int i = 0;
        boolean found = false;
        for (; i < elementsPointer.size(); i ++) {
            int length = byteBuffer.getInt(elementsPointer.get(i));
            if (length == bytes.length) {
                // bay be equals
                boolean equals = true;
                for (int j = 0; j < length; j++) {
                    if (byteBuffer.get(elementsPointer.get(i) + j) != bytes[j]) {
                        equals = false;
                        break;
                    }
                }
                if(equals) {
                    found = true;
                    break;
                }
            }
        }
        return found ? i : -1;
    }

    @Override
    public int removeInBytes(byte[] bytes) {
        int index = indexOfInBytes(bytes);
        return index == -1 ? -1 : remove(index);
    }

    @Override
    public void compact() {
        // recycle all useless segments

    }

    @Override
    public int size() {
        return elementsPointer.size();
    }

    @Override
    public byte[] getInBytes(int index) {
        int len = byteBuffer.getInt(elementsPointer.get(index));
        byte[] bytes = new byte[len];
        byteBuffer.get(bytes, 0, len);
        return bytes;
    }

    @Override
    public void putInBytes(int index, byte[] bytes) {
        if (elementsPointer.size() == index) {
            // append.
            elementsPointer.add(tailPointer);
            writeBytes(tailPointer, bytes);
            tailPointer += LENGTH_SIZE + bytes.length;
        } else {
            // replace
            long originIndex = elementsPointer.get(index);
            int len = byteBuffer.getInt(originIndex);
            if (len > bytes.length) {
                // put it in.
                writeBytes(originIndex, bytes);
                recyclableSegments.add(new long[]{originIndex + LENGTH_SIZE + bytes.length, bytes.length - len});
            }
        }
    }

    protected void writeBytes(long position, byte[] bytes) {
        if (bytes.length > byteBuffer.capacity() - position) {
            //TODO try to find recycled segments.

        }
        byteBuffer.position(position);
        byteBuffer.putInt(bytes.length);
        byteBuffer.put(bytes, 0, bytes.length);
    }
}
