package com.iyestin.java.collections.offheap.array;

import com.iyestin.java.collections.offheap.OffHeapSerializer;
import com.iyestin.java.collections.offheap.base.DirectByteBuffer;
import com.iyestin.java.collections.offheap.util.ExceptionUtils;

/**
 * Fixed off-heap array.
 */
public class FixedOffHeapArray<T> extends AbstractOffHeapArray<T> {

    private static final int LENGTH_SIZE = 4;

    private int capacity = -1;
    private int elementSize = -1;
    private int actualSize = -1;

    private DirectByteBuffer byteBuffer;


    public FixedOffHeapArray(int capacity, int elementSize, OffHeapSerializer<T> serializer) {
        super(serializer);
        ExceptionUtils.raiseRuntimeExceptionIf(capacity < 0,
                new IllegalArgumentException("capacity can not be less than 0"));
        ExceptionUtils.raiseRuntimeExceptionIf(elementSize < 0,
                new IllegalArgumentException("element size can not be less than 0"));
        this.capacity = capacity;
        this.elementSize = elementSize;
        this.actualSize = elementSize + LENGTH_SIZE;
        this.byteBuffer = new DirectByteBuffer((long)capacity * actualSize);
    }


    @Override
    public int size() {
        return capacity;
    }

    @Override
    public byte[] getInBytes(int index) {
        ExceptionUtils.raiseRuntimeExceptionIf(index > capacity || index < 0,
                new ArrayIndexOutOfBoundsException());
        byteBuffer.position(index * actualSize);
        int thisLength = byteBuffer.getInt();
        ExceptionUtils.raiseRuntimeExceptionIf(thisLength > elementSize,
                new IllegalStateException("data has been corrupted"));
        byte[] arr = new byte[thisLength];
        byteBuffer.get(arr, 0, thisLength);
        return arr;
    }

    @Override
    public void putInBytes(int index, byte[] bytes) {
        ExceptionUtils.raiseRuntimeExceptionIf(index > capacity -1 || index < 0,
                new ArrayIndexOutOfBoundsException());
        ExceptionUtils.raiseRuntimeExceptionIf(bytes.length > elementSize,
                new IllegalArgumentException("the length of the bytes can not larger than element size: " + elementSize)
        );
        byteBuffer.position(index * actualSize);
        byteBuffer.putInt(bytes.length);
        byteBuffer.put(bytes, 0, bytes.length);
    }

}
