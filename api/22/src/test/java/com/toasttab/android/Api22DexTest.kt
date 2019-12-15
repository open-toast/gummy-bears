package com.toasttab.android

import com.google.common.truth.Truth.assertThat
import com.toasttab.android.test.D8Runner
import org.junit.Test

class Api22DexTest {
    @Test
    fun `API22 desugaring should succeed`() {
        assertThat(D8Runner.run(apiLevel = 22).warnings).isEmpty()
    }
}