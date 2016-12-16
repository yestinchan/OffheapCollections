package com.iyestin.java.collections.offheap.base;

import com.iyestin.java.collections.offheap.exception.InsufficientSpaceException;
import com.iyestin.java.collections.offheap.util.ExceptionUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 */
public class DirectByteBuffer {

    private long position = 0;

    private ByteBuffer[] byteBuffers;

    boolean bigEndian = true;

    private int cellThreshold = Integer.MAX_VALUE;

    private long capacity;


    /**
     * direct byte buffer
     * @param allocateSize
     */
    public DirectByteBuffer(long allocateSize) {
        this.capacity = allocateSize;
        int size = (int) (allocateSize / cellThreshold);
        int left = (int) (allocateSize % cellThreshold);
        if (left != 0) {
            size ++;
        }

        byteBuffers = new ByteBuffer[size];
        for (int i =0; i < size; i ++) {
            if (left != 0 && i == size -1) {
                byteBuffers[i] = ByteBuffer.allocateDirect(left);
            } else {
                byteBuffers[i] = ByteBuffer.allocateDirect(cellThreshold);
            }
        }
    }

    public ByteOrder order() {
        return bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
    }

    public DirectByteBuffer order(ByteOrder order) {
        bigEndian = (order == ByteOrder.BIG_ENDIAN);
        // set order.
        for (ByteBuffer bf : byteBuffers) {
            bf.order(order);
        }
        return this;
    }

    public long capacity() {
        return this.capacity;
    }

    public void putInt(int i) {
        // We will not fill the bytes here
        if (getByteBufferIndex(position) == getByteBufferIndex(position + 4)) {
            // Okay then.
            getByteBuffer(position).putInt(i);
        } else {
            _putInt(position, i);
        }
        position += 4;
    }

    public int getInt(long index) {
        if (getByteBufferIndex(index) == getByteBufferIndex(index + 4)) {
            return getByteBuffer(index).getInt();
        } else {
            return _getInt(index);
        }
    }

    public int getInt() {
        int retVal = getInt(position);
        position += 4;
        return retVal;
    }

    public byte get() {
        byte b = get(position);
        position ++;
        return b;
    }

    public void put(byte b) {
        // will be no problem.
        put(position, b);
        position ++;
    }

    public byte get(long index) {
        return getByteBuffer(index).get();
    }

    public void put(long index, byte b) {
        getByteBuffer(index).put(b);
    }

    public void put(byte[] src, int offset, int length) {
        if (getByteBufferIndex(position) == getByteBufferIndex(position + length -1)) {
            // just copy
            getByteBuffer(position).put(src, offset, length);
            position += length;
        } else {
            ExceptionUtils.raiseRuntimeExceptionIf(
                    getByteBufferIndex(position + length -1) >= byteBuffers.length,
                    new InsufficientSpaceException("not enough space for length : " + length));
            // copy by step.
            int thisLength = (int) (cellThreshold - getByteBufferPosition(position));
            int remaining = length;
            int thisOffset = offset;
            while (remaining > 0) {
                remaining -= thisLength;
                getByteBuffer(position).put(src, thisOffset, thisLength);

                position += thisLength;
                thisOffset += thisLength;

                thisLength = Math.min((int) (cellThreshold - getByteBufferPosition(position)), remaining);
                // set position
                if (getByteBufferIndex(position) < byteBuffers.length) {
                    getByteBuffer(position).position(getByteBufferPosition(position));
                }

            }

        }
    }


    public void get(byte[] dst, int offset, int length) {
        if (getByteBufferIndex(position) == getByteBufferIndex(position + length)) {
            getByteBuffer(position).get(dst, offset, length);
            position += length;
        } else {
            ExceptionUtils.raiseRuntimeExceptionIf(
                    getByteBufferIndex(position + length -1) >= byteBuffers.length,
                    new InsufficientSpaceException("not enough space for length : " + length));
            // read by step.
            int thisLength = (int) (cellThreshold - getByteBufferPosition(position));
            int remaining = length;
            int thisOffset = offset;
            while (remaining > 0) {
                remaining -= thisLength;
                getByteBuffer(position).get(dst, thisOffset, thisLength);

                position += thisLength;
                thisOffset += thisLength;

                thisLength = Math.min((int) (cellThreshold - getByteBufferPosition(position)), remaining);
                // set position
                if (getByteBufferIndex(position) < byteBuffers.length) {
                    getByteBuffer(position).position(getByteBufferPosition(position));
                }

            }
        }
    }

    public void putLong(long i) {
        // We will not fill the bytes here
        if (getByteBufferIndex(position) == getByteBufferIndex(position + 8)) {
            // Okay then.
            getByteBuffer(position).putLong(i);
        } else {
            _putLong(position, i);
        }
        position += 8;
    }

    public long getLong() {
        long retVal = -1;
        if (getByteBufferIndex(position) == getByteBufferIndex(position + 4)) {
            retVal = getByteBuffer(position).getLong();
        } else {
            retVal = _getLong(position);
        }
        position += 8;
        return retVal;
    }

    public void putShort(short i) {
        if (getByteBufferIndex(position) == getByteBufferIndex(position + 2)) {
            // Okay then.
            getByteBuffer(position).putShort(i);
        } else {
            _putShort(position, i);
        }
        position += 2;
    }

    public short getShort() {
        short retVal = -1;
        if (getByteBufferIndex(position) == getByteBufferIndex(position + 4)) {
            retVal = getByteBuffer(position).getShort();
        } else {
            retVal = _getShort(position);
        }
        position += 2;
        return retVal;
    }

    public void position(long position) {
        this.position = position;
        getByteBuffer(position).position(getByteBufferPosition(position));
    }

    public long position() {
        return position;
    }

    ByteBuffer getByteBuffer(long index) {
        ByteBuffer bb = byteBuffers[getByteBufferIndex(index)];
        bb.position(getByteBufferPosition(index));
        return bb;
    }

    int getByteBufferIndex(long index) {
        return (int) (index / cellThreshold);
    }

    int getByteBufferPosition(long index) {
        return (int) (index % cellThreshold);
    }

    private long _getLong(long index) {
        if (bigEndian) {
            return makeLong(get(index), get(index + 1), get(index + 2), get(index + 3),
                    get(index + 4), get(index + 5), get(index + 6), get(index + 7));
        } else {
            return makeLong(get(index + 7), get(index + 6), get(index + 5), get(index + 4),
                    get(index + 3), get(index + 2), get(index + 1), get(index));
        }
    }

    private int _getInt(long index){
        if(bigEndian) {
            return makeInt(get(index), get(index + 1), get(index + 2), get(index + 3));
        } else {
            return makeInt(get(index + 3), get(index + 2), get(index + 1), get(index));
        }
    }

    private short _getShort(long index) {
        if (bigEndian) {
            return makeShort(get(index), get(index + 1));
        } else {
            return makeShort(get(index + 1), get(index));
        }
    }

    short makeShort(byte b1, byte b0) {
        return (short)((b1 << 8) | (b0 & 0xff));
    }

    int makeInt(byte b3, byte b2, byte b1, byte b0) {
        return (((b3       ) << 24) |
                ((b2 & 0xff) << 16) |
                ((b1 & 0xff) <<  8) |
                ((b0 & 0xff)      ));
    }

    long makeLong(byte b7, byte b6, byte b5, byte b4,
                   byte b3, byte b2, byte b1, byte b0) {
        return ((((long)b7       ) << 56) |
                (((long)b6 & 0xff) << 48) |
                (((long)b5 & 0xff) << 40) |
                (((long)b4 & 0xff) << 32) |
                (((long)b3 & 0xff) << 24) |
                (((long)b2 & 0xff) << 16) |
                (((long)b1 & 0xff) <<  8) |
                (((long)b0 & 0xff)      ));
    }


    private void _putInt(long index, int x) {
        if (bigEndian) {
            put(index,(byte) (x >> 24));
            put(index + 1,(byte) (x >> 16));
            put(index + 2,(byte) (x >> 8));
            put(index + 3,(byte) (x));
        } else {
            put(index,(byte) (x));
            put(index + 1,(byte) (x >> 8));
            put(index + 2,(byte) (x >> 16));
            put(index + 3,(byte) (x >> 24));
        }
    }

    private void _putLong(long index, long x) {
        if (bigEndian) {
            put(index, (byte) (x >> 56));
            put(index + 1, (byte) (x >> 48));
            put(index + 2,(byte) (x >> 40));
            put(index + 3,(byte) (x >> 32));
            put(index + 4,(byte) (x >> 24));
            put(index + 5,(byte) (x >> 16));
            put(index + 6,(byte) (x >> 8));
            put(index + 7,(byte) (x));
        } else {
            put(index,(byte) (x));
            put(index + 1,(byte) (x >> 8));
            put(index + 2,(byte) (x >> 16));
            put(index + 3,(byte) (x >> 24));
            put(index + 4,(byte) (x >> 32));
            put(index + 5,(byte) (x >> 40));
            put(index + 6,(byte) (x >> 48));
            put(index + 7,(byte) (x >> 56));
        }
    }

    private void _putShort(long index, short x) {
        if (bigEndian) {
            put(index,(byte) (x >> 8));
            put(index + 1,(byte) (x));
        } else {
            put(index,(byte) (x));
            put(index + 1,(byte) (x >> 8));
        }
    }

    void describe() {
        System.out.println("==============================");
        for (ByteBuffer byteBuffer : byteBuffers) {
            System.out.println("byteBuffer"+byteBuffer);
            for (int i =0; i < byteBuffer.capacity(); i++) {
                System.out.print(byteBuffer.get(i)+"\t");
            }
            System.out.println();
        }
        System.out.println();
    }
}
