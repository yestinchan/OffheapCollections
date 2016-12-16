package com.iyestin.java.collections.offheap.queue;

import com.iyestin.java.collections.offheap.OffHeapSerializer;
import org.junit.Test;

/**
 * Simple off-heap queue test .
 */
public class SimpleOffHeapQueueTest {

    @Test
    public void simpleTest() {
        SimpleOffHeapQueue<String> queue = new SimpleOffHeapQueue<>(60, new OffHeapSerializer<String>() {
            @Override
            public byte[] serialize(String s) {
                return s.getBytes();
            }

            @Override
            public String deserialize(byte[] bytes) {
                return new String(bytes);
            }
        });
        queue.put("Index1");
        queue.describe();
        queue.put("index2");
        queue.describe();
        queue.put("index2");
        queue.describe();
        queue.put("index2");
        queue.describe();
        queue.take();
        System.out.println("taken");
        queue.describe();
        queue.put("index45");
        queue.describe();
        queue.put("index45");
        queue.describe();
        System.out.println("last!");
//        queue.put("ddxxxddxd");
//        queue.describe();
    }
}
