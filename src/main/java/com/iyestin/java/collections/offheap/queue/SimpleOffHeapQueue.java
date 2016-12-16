package com.iyestin.java.collections.offheap.queue;

import com.iyestin.java.collections.offheap.base.DirectByteBuffer;
import com.iyestin.java.collections.offheap.exception.InsufficientSpaceException;
import com.iyestin.java.collections.offheap.OffHeapSerializer;
import com.iyestin.java.collections.offheap.util.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple off-heap queue
 */
public class SimpleOffHeapQueue<T> extends AbstractOffHeapQueue<T> {

    private static final int LENGTH_SIZE = 4;

    private DirectByteBuffer byteBuffer;

    private long writePointer = 0;

    private List<Long> elementsPointer = new ArrayList<>();

    public SimpleOffHeapQueue(int sizeInBytes, OffHeapSerializer<T> serializer) {
        super(serializer);
        this.byteBuffer = new DirectByteBuffer(sizeInBytes);
    }

    @Override
    public int size() {
        return elementsPointer.size();
    }

    @Override
    public void putInBytes(byte[] bytes) {
        ExceptionUtils.raiseRuntimeExceptionIf(bytes.length + LENGTH_SIZE > byteBuffer.capacity(),
                new IllegalArgumentException("bytes to large"));

        int remaining = -1;
        if (elementsPointer.size() > 0 && writePointer < elementsPointer.get(0)) {
            remaining = (int) (bytes.length - (elementsPointer.get(0) - writePointer - LENGTH_SIZE));
        } else {
            remaining = (int) (bytes.length - (byteBuffer.capacity() - writePointer - LENGTH_SIZE));
        }

        long pointerAfterWrite = (writePointer + LENGTH_SIZE + bytes.length) % byteBuffer.capacity();

        if (remaining <= 0 ){
            ExceptionUtils.raiseRuntimeExceptionIf(
                    elementsPointer.size() > 0 && pointerAfterWrite < elementsPointer.get(0),
                    new InsufficientSpaceException("no space left for bytes length:" + bytes.length));
        } else {
            ExceptionUtils.raiseRuntimeExceptionIf(
                    elementsPointer.size() > 0 && pointerAfterWrite > elementsPointer.get(0),
                    new InsufficientSpaceException("no space left for bytes length:" + bytes.length));
        }

        elementsPointer.add(writePointer);
        byteBuffer.position(writePointer);
        byteBuffer.putInt(bytes.length);
        byteBuffer.put(bytes, 0 , remaining > 0 ? (int) (bytes.length - remaining) : bytes.length);
        if (remaining > 0) {
            byteBuffer.position(0);
            byteBuffer.put(bytes, bytes.length - remaining, remaining);
        }
        writePointer = byteBuffer.position();
    }

    @Override
    public byte[] takeInBytes() {
        byte[] results = peekInBytes();
        elementsPointer.remove(0);
        return results;
    }

    @Override
    public byte[] peekInBytes() {
        if (elementsPointer.size() ==0) {
            return null;
        }
        byteBuffer.position(elementsPointer.get(0));
        int len = byteBuffer.getInt();
        byte[] bytes = new byte[len];
        int remaining = (int) (len - (byteBuffer.capacity() - byteBuffer.position()));
        byteBuffer.get(bytes, 0, remaining > 0 ? len - remaining : len);
        if (remaining > 0) {
            byteBuffer.get(bytes, len - remaining, remaining);
        }
        return bytes;
    }

    void describe() {
        System.out.println("queue description==========================");
        int j = 0;
        for (int i =0 ; i < byteBuffer.capacity(); i++) {
            if (j < elementsPointer.size() && elementsPointer.get(j) == i) {
                System.out.print("|" + j + "=>\t");
                j ++;
            } else {
                System.out.print("\t");
            }
            if (i % 100 == 99) {
                System.out.println(byteBuffer.get(i));
            } else {
                System.out.print(byteBuffer.get(i));
            }
        }
        System.out.println("");
    }


}
