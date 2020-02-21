package com.toasttab.android

import com.google.common.truth.Truth.assertThat
import com.toasttab.android.test.D8Runner
import org.junit.Test

class Api30DexTest {
    @Test
    fun `API30 desugaring should succeed`() {
        assertThat(D8Runner.run(apiLevel = 30).warnings).isEmpty()
    }
}