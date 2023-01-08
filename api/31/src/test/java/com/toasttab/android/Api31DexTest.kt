package com.toasttab.android

import com.google.common.truth.Truth.assertThat
import com.toasttab.android.signature.test.D8Runner
import org.junit.Test

class Api31DexTest {
    @Test
    fun `API31 desugaring should succeed`() {
        assertThat(D8Runner.run(apiLevel = 31).warnings).isEmpty()
    }
}
