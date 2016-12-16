package com.iyestin.java.collections.offheap;

/**
 * OffHeap serializer <br>
 *     Serialize the object to and from a byte array.
 */
public interface OffHeapSerializer<T> {

    /**
     * Serialize the object to byte array.
     * @return
     */
    byte[] serialize(T t);

    /**
     * deserialize the byte array to object.
     * @return
     */
    T deserialize(byte[] bytes);
}
