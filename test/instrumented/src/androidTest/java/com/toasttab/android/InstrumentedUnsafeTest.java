/*
 * Copyright (c) 2025. Toast Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.toasttab.android;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.toasttab.android.stub.desugar_sun_misc_DesugarUnsafe;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Exercises the entire surface of the generated {@link desugar_sun_misc_DesugarUnsafe} wrapper.
 *
 * <p>The wrapper is a thin pass-through to {@code sun.misc.Unsafe}, so each test drives a wrapper
 * method and asserts the observable effect on real objects, arrays, or off-heap memory. Note the
 * asymmetry in the underlying API surface that shapes these tests:
 *
 * <ul>
 *   <li>On-heap accessors are the {@code (Object, long, value)} forms, verified against scratch
 *       object fields and array elements.
 *   <li>Off-heap getters are the 1-arg {@code getX(long)} forms; there is no 1-arg off-heap
 *       {@code putX} and {@code copyMemory} is only the off-heap-to-off-heap form, so off-heap
 *       memory can only be seeded via {@code setMemory} (a uniform byte fill) and {@code copyMemory}.
 * </ul>
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class InstrumentedUnsafeTest {
    public static class Holder {
        public boolean constructed;
        public int intField;
        public long longField;
        public Object objField;
        public byte byteField;
        public short shortField;
        public char charField;
        public float floatField;
        public double doubleField;

        public Holder() {
            constructed = true;
        }
    }

    private Object getUnsafe() throws Exception {
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");

        for (Field f : unsafeClass.getDeclaredFields()) {
            if (f.getType() == unsafeClass && Modifier.isStatic(f.getModifiers())) {
                f.setAccessible(true);
                return f.get(null);
            }
        }

        throw new IllegalStateException("cannot retrieve an instance of Unsafe");
    }

    private desugar_sun_misc_DesugarUnsafe caller() throws Exception {
        return new desugar_sun_misc_DesugarUnsafe(getUnsafe());
    }

    private long fieldOffset(desugar_sun_misc_DesugarUnsafe caller, String name) throws Exception {
        long offset = caller.objectFieldOffset(Holder.class.getDeclaredField(name));
        assertTrue("objectFieldOffset should be positive", offset > 0);
        return offset;
    }

    @Test
    public void allocateInstance() throws Exception {
        desugar_sun_misc_DesugarUnsafe caller = caller();

        Object instance = caller.allocateInstance(Holder.class);

        assertTrue(instance instanceof Holder);
        // allocateInstance bypasses the constructor, so the flag it would have set stays false.
        assertFalse(((Holder) instance).constructed);
    }

    @Test
    public void arrays() throws Exception {
        desugar_sun_misc_DesugarUnsafe caller = caller();

        int base = caller.arrayBaseOffset(int[].class);
        int scale = caller.arrayIndexScale(int[].class);
        assertTrue(base >= 0);
        assertTrue(scale > 0);

        int[] array = new int[3];
        long slot1 = (long) base + 1L * scale;

        caller.putInt(array, slot1, 99);
        assertEquals(99, array[1]);
        assertEquals(99, caller.getInt(array, slot1));
    }

    @Test
    public void heapIntAccessors() throws Exception {
        desugar_sun_misc_DesugarUnsafe caller = caller();
        Holder h = new Holder();
        long off = fieldOffset(caller, "intField");

        caller.putInt(h, off, 10);
        assertEquals(10, h.intField);
        assertEquals(10, caller.getInt(h, off));

        caller.putIntVolatile(h, off, 20);
        assertEquals(20, h.intField);
        assertEquals(20, caller.getIntVolatile(h, off));

        caller.putOrderedInt(h, off, 30);
        assertEquals(30, h.intField);
    }

    @Test
    public void heapLongAccessors() throws Exception {
        desugar_sun_misc_DesugarUnsafe caller = caller();
        Holder h = new Holder();
        long off = fieldOffset(caller, "longField");

        caller.putLong(h, off, 10L);
        assertEquals(10L, h.longField);
        assertEquals(10L, caller.getLong(h, off));

        caller.putLongVolatile(h, off, 20L);
        assertEquals(20L, h.longField);
        assertEquals(20L, caller.getLongVolatile(h, off));

        caller.putOrderedLong(h, off, 30L);
        assertEquals(30L, h.longField);
    }

    @Test
    public void heapObjectAccessors() throws Exception {
        desugar_sun_misc_DesugarUnsafe caller = caller();
        Holder h = new Holder();
        long off = fieldOffset(caller, "objField");

        caller.putObject(h, off, "a");
        assertSame("a", h.objField);
        assertSame("a", caller.getObject(h, off));

        caller.putObjectVolatile(h, off, "b");
        assertSame("b", h.objField);
        assertSame("b", caller.getObjectVolatile(h, off));

        caller.putOrderedObject(h, off, "c");
        assertSame("c", h.objField);
    }

    @Test
    public void heapSubwordAccessors() throws Exception {
        desugar_sun_misc_DesugarUnsafe caller = caller();
        Holder h = new Holder();

        caller.putByte(h, fieldOffset(caller, "byteField"), (byte) 0x11);
        assertEquals(0x11, h.byteField);

        caller.putShort(h, fieldOffset(caller, "shortField"), (short) 0x2222);
        assertEquals(0x2222, h.shortField);

        caller.putChar(h, fieldOffset(caller, "charField"), 'Z');
        assertEquals('Z', h.charField);

        caller.putFloat(h, fieldOffset(caller, "floatField"), 3.5f);
        assertEquals(3.5f, h.floatField, 0.0f);

        caller.putDouble(h, fieldOffset(caller, "doubleField"), 2.5d);
        assertEquals(2.5d, h.doubleField, 0.0d);
    }

    @Test
    public void compareAndSwap() throws Exception {
        desugar_sun_misc_DesugarUnsafe caller = caller();
        Holder h = new Holder();

        long intOff = fieldOffset(caller, "intField");
        caller.putInt(h, intOff, 1);
        assertTrue(caller.compareAndSwapInt(h, intOff, 1, 2));
        assertEquals(2, h.intField);
        assertFalse(caller.compareAndSwapInt(h, intOff, 1, 3));
        assertEquals(2, h.intField);

        long longOff = fieldOffset(caller, "longField");
        caller.putLong(h, longOff, 1L);
        assertTrue(caller.compareAndSwapLong(h, longOff, 1L, 2L));
        assertEquals(2L, h.longField);
        assertFalse(caller.compareAndSwapLong(h, longOff, 1L, 3L));
        assertEquals(2L, h.longField);

        long objOff = fieldOffset(caller, "objField");
        caller.putObject(h, objOff, "a");
        assertTrue(caller.compareAndSwapObject(h, objOff, "a", "b"));
        assertSame("b", h.objField);
        assertFalse(caller.compareAndSwapObject(h, objOff, "a", "c"));
        assertSame("b", h.objField);
    }

    @Test
    public void offHeapMemory() throws Exception {
        desugar_sun_misc_DesugarUnsafe caller = caller();

        long src = caller.allocateMemory(8);
        long dst = caller.allocateMemory(8);
        assertNotEquals(0L, src);
        assertNotEquals(0L, dst);
        try {
            // A uniform byte fill makes every off-heap read endianness-independent.
            caller.setMemory(src, 8, (byte) 0);
            assertEquals(0L, (long) caller.getByte(src));
            assertEquals(0L, (long) caller.getShort(src));
            assertEquals(0L, (long) caller.getChar(src));
            assertEquals(0L, (long) caller.getInt(src));
            assertEquals(0L, caller.getLong(src));
            assertEquals(0.0f, caller.getFloat(src), 0.0f);
            assertEquals(0.0d, caller.getDouble(src), 0.0d);

            caller.setMemory(src, 8, (byte) 0x7F);
            assertEquals(0x7FL, (long) caller.getByte(src));
            assertEquals(0x7F7FL, (long) caller.getShort(src));
            assertEquals(0x7F7FL, (long) caller.getChar(src));
            assertEquals(0x7F7F7F7FL, (long) caller.getInt(src));
            assertEquals(0x7F7F7F7F7F7F7F7FL, caller.getLong(src));
            assertEquals(Float.intBitsToFloat(0x7F7F7F7F), caller.getFloat(src), 0.0f);
            assertEquals(Double.longBitsToDouble(0x7F7F7F7F7F7F7F7FL), caller.getDouble(src), 0.0d);

            // copyMemory is off-heap-to-off-heap only; seed src, then copy over a zeroed dst.
            caller.setMemory(src, 8, (byte) 0x2A);
            caller.setMemory(dst, 8, (byte) 0);
            caller.copyMemory(src, dst, 8);
            assertEquals(0x2AL, (long) caller.getByte(dst));
            assertEquals(0x2A2A2A2A2A2A2A2AL, caller.getLong(dst));
        } finally {
            caller.freeMemory(src);
            caller.freeMemory(dst);
        }
    }

    @Test
    public void machineInfo() throws Exception {
        desugar_sun_misc_DesugarUnsafe caller = caller();

        int addressSize = caller.addressSize();
        assertTrue("address size should be 4 or 8", addressSize == 4 || addressSize == 8);
        assertTrue("page size should be positive", caller.pageSize() > 0);
    }

    @Test
    public void fences() throws Exception {
        desugar_sun_misc_DesugarUnsafe caller = caller();

        // No observable state to assert; simply confirm the pass-through does not throw.
        caller.loadFence();
        caller.storeFence();
        caller.fullFence();
    }

    @Test
    public void parkUnpark() throws Exception {
        desugar_sun_misc_DesugarUnsafe caller = caller();

        // Grant this thread a permit, then a permit-consuming park returns immediately.
        caller.unpark(Thread.currentThread());
        caller.park(false, 0L);

        // A relative-timeout park is also bounded even without a permit.
        caller.park(false, 1_000_000L);
    }
}
