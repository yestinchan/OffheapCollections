package com.iyestin.java.collections.offheap.array;

import com.iyestin.java.collections.offheap.OffHeapSerializer;
import org.junit.Test;

/**
 * Test cases for fixed off-heap array.
 */
public class FixedOffHeapArrayTest {

    @Test
    public void simpleTest() {
        FixedOffHeapArray<String> testArray = new FixedOffHeapArray<>(100, 3, new OffHeapSerializer<String>() {
            @Override
            public byte[] serialize(String integer) {
                return integer.getBytes();
            }

            @Override
            public String deserialize(byte[] bytes) {
                return new String(bytes);
            }
        });
        testArray.put(0, "123");
        testArray.put(1, "223");
        testArray.put(2, "323");
        System.out.println(testArray.get(0));
//        testArray.put(0, "1234");
//        System.out.println(testArray.get(1));
    }

}
