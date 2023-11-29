package desugar.sun.misc;

import java.lang.reflect.Field;

public final class DesugarUnsafe {
    public <T> T allocateInstance(Class<T> clazz) {
        return null;
    }

    public int arrayBaseOffset(Class<?> clazz) {
        return 0;
    }

    public int arrayIndexScale(Class<?> clazz) {
        return 0;
    }

    public boolean compareAndSwapInt(Object obj, long offset, int expectedValue, int newValue) {
        return false;
    }

    public boolean compareAndSwapLong(Object obj, long offset, long expectedValue, long newValue) {
        return false;
    }

    public boolean compareAndSwapObject(Object obj, long offset, Object expectedValue, Object newValue) {
        return false;
    }

    public int getInt(Object obj, long offset) {
        return 0;
    }

    public int getIntVolatile(Object obj, long offset) {
        return 0;
    }

    public long getLong(Object obj, long offset) {
        return 0;
    }

    public long getLongVolatile(Object obj, long offset) {
        return 0;
    }

    public Object getObject(Object obj, long offset) {
        return null;
    }

    public Object getObjectVolatile(Object obj, long offset) {
        return null;
    }

    public long objectFieldOffset(Field field) {
        return 0;
    }

    public void park(boolean absolute, long time) {

    }

    public void unpark(Object obj) {

    }


    public void putInt(Object obj, long offset, int newValue) {

    }


    public void putIntVolatile(Object obj, long offset, int newValue) {

    }


    public void putLong(Object obj, long offset, long newValue) {

    }


    public void putLongVolatile(Object obj, long offset, long newValue) {

    }

    public void putObject(Object obj, long offset, Object newValue) {

    }


    public void putObjectVolatile(Object obj, long offset, Object newValue) {

    }


    public void putOrderedInt(Object obj, long offset, int newValue) {

    }


    public void putOrderedLong(Object obj, long offset, long newValue) {

    }


    public void putOrderedObject(Object obj, long offset, Object newValue) {

    }
}
