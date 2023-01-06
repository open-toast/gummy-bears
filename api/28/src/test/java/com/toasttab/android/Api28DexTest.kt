package com.toasttab.android

import com.google.common.truth.Truth.assertThat
import com.toasttab.android.signature.test.D8Runner
import org.junit.Test

class Api28DexTest {
    @Test
    fun `API28 desugaring should succeed`() {
        assertThat(D8Runner.run(apiLevel = 28).warnings).isEmpty()
    }
}