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
