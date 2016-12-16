package com.iyestin.java.collections.offheap.base;

import org.junit.Test;

import java.util.Arrays;

/**
 * Created by yestin on 2016/12/16.
 */
public class DirectByteBufferTest {

    @Test
    public void simpleTest() {
        DirectByteBuffer byteBuffer = new DirectByteBuffer(20);
//        byteBuffer.put((byte)12);
//        byteBuffer.describe();
//        byteBuffer.put((byte) 44);
//        byteBuffer.describe();
//        byteBuffer.putInt(123);
//        byteBuffer.describe();
//        byteBuffer.putLong(Long.MAX_VALUE);
//        byteBuffer.describe();
//        byteBuffer.putShort((short) 89);
//        byteBuffer.describe();
        byte[] bytes = new byte[]{
                1, 2, 3, 4 ,5, 6, 7, 8, 9, 10, 11, 12, 13, 14
        };
        byte[] bytes2 = new byte[]{
                11,12,13,14,15, 16
        };
        byteBuffer.put(bytes, 0, 14);
        byteBuffer.describe();

        byteBuffer.position(0);
        byte[] holder = new byte[20];
        byteBuffer.get(holder, 0,14);
        System.out.println(Arrays.toString(holder));

        byteBuffer.position(14);

        byteBuffer.put(bytes2, 0, bytes2.length);
        byteBuffer.describe();
    }

}
