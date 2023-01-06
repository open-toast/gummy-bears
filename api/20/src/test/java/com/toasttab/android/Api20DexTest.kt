package com.toasttab.android

import com.google.common.truth.Truth.assertThat
import com.toasttab.android.signature.test.D8Runner
import org.junit.Test

class Api20DexTest {
    @Test
    fun `API20 desugaring should succeed`() {
        assertThat(D8Runner.run(apiLevel = 20).warnings).isEmpty()
    }
}