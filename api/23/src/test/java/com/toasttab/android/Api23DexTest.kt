package com.toasttab.android

import com.google.common.truth.Truth.assertThat
import com.toasttab.android.test.D8Runner
import org.junit.Test

class Api23DexTest {
    @Test
    fun `API23 desugaring should succeed`() {
        assertThat(D8Runner.run(apiLevel = 23).warnings).isEmpty()
    }
}