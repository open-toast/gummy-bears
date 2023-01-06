package com.toasttab.android

import com.google.common.truth.Truth.assertThat
import com.toasttab.android.signature.test.D8Runner
import org.junit.Test

class Api21DexTest {
    @Test
    fun `API21 desugaring should succeed`() {
        assertThat(D8Runner.run(apiLevel = 21).warnings).isEmpty()
    }
}