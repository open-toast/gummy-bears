package com.toasttab.android

import com.google.common.truth.Truth.assertThat
import com.toasttab.android.test.D8Runner
import org.junit.Test

class Api32DexTest {
    @Test
    fun `API31 desugaring should succeed`() {
        assertThat(D8Runner.run(apiLevel = 32).warnings).isEmpty()
    }
}