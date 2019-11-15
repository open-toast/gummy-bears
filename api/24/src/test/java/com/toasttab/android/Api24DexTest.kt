package com.toasttab.android

import com.google.common.truth.Truth.assertThat
import com.toasttab.android.test.D8Runner
import org.junit.Test

class Api24DexTest {
    @Test
    fun `API24 desugaring should succeed`() {
        assertThat(D8Runner.run(apiLevel = 24).warnings).isEmpty()
    }
}