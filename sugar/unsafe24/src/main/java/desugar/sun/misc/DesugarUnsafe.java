package desugar.sun.misc;

import java.lang.reflect.Field;

public final class DesugarUnsafe {
    public int addressSize() {
        return 0;
    }
    
    public int pageSize() {
        return 0;
    }

    public long allocateMemory(long bytes) {
        return 0;
    }

    public void freeMemory(long address) { }

    public void setMemory(long address, long bytes, byte value) { }

    public byte getByte(long address) {
        return 0;
    }

    public void putByte(Object obj, long offset, byte newValue) { }

    public short getShort(long address) {
        return 0;
    }

    public void putShort(Object obj, long offset, short newValue) { }

    public char getChar(long address) {
        return 0;
    }

    public void putChar(Object obj, long offset, char newValue) { }

    public int getInt(long address) {
        return 0;
    }

    public long getLong(long address) {
        return 0;
    }

    public float getFloat(long address) {
        return 0;
    }

    public void putFloat(Object obj, long offset, float newValue) { }

    public double getDouble(long address) {
        return 0;
    }
    
    public void putDouble(Object obj, long offset, double newValue) { }
    
    public void copyMemory(long srcAddr, long dstAddr, long bytes) { }

    public int getAndAddInt(Object o, long offset, int delta) {
        return 0;
    }

    public long getAndAddLong(Object o, long offset, long delta) {
        return 0;
    }
    
    public int getAndSetInt(Object o, long offset, int newValue) {
        return 0;
    }

    public long getAndSetLong(Object o, long offset, long newValue) {
        return 0;
    }
    
    public Object getAndSetObject(Object o, long offset, Object newValue) {
        return 0;
    }

    public void loadFence() { }

    public void storeFence() { }

    public void fullFence() { }
}
