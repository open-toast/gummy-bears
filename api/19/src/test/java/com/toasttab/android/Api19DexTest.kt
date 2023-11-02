package com.toasttab.android

import com.toasttab.android.signature.test.D8Runner
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEmpty

class Api19DexTest {
    @Test
    fun `API19 desugaring should succeed`() {
        expectThat(D8Runner.run(apiLevel = 19).warnings).isEmpty()
    }
}
