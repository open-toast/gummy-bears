package com.toasttab.android

import com.google.common.truth.Truth.assertThat
import com.toasttab.android.test.D8Runner
import org.junit.Test

class Api29DexTest {
    @Test
    fun `API29 desugaring should succeed`() {
        assertThat(D8Runner.run(apiLevel = 29).warnings).isEmpty()
    }
}