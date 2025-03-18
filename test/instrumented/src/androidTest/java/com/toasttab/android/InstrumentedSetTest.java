package com.toasttab.android;

import static org.junit.Assert.assertEquals;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.toasttab.android.stub.desugar_java_util_DesugarSet;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.HashSet;

@RunWith(AndroidJUnit4ClassRunner.class)
public class InstrumentedSetTest {
    @Test
    public void test() throws Exception {
        assertEquals(
                new HashSet<>(Arrays.asList("a", "b", "c")),
                new desugar_java_util_DesugarSet().of("a", "b", "c")
        );
    }
}
