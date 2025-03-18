package com.toasttab.android;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;


import com.toasttab.android.stub.desugar_sun_misc_DesugarUnsafe;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@RunWith(AndroidJUnit4ClassRunner.class)
public class InstrumentedUnsafeTest {
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

    @Test
    public void test() throws Exception {
        desugar_sun_misc_DesugarUnsafe caller = (desugar_sun_misc_DesugarUnsafe) desugar_sun_misc_DesugarUnsafe.class.getConstructors()[0].newInstance(getUnsafe());

        caller.allocateInstance(String.class);
        caller.arrayBaseOffset(int[].class);
    }
}
