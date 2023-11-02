package com.toasttab.android

import com.toasttab.android.signature.test.D8Runner
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEmpty

class Api30DexTest {
    @Test
    fun `API30 desugaring should succeed`() {
        expectThat(D8Runner.run(apiLevel = 30).warnings).isEmpty()
    }
}
