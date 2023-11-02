package com.toasttab.android

import com.toasttab.android.signature.test.D8Runner
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEmpty

class Api28DexTest {
    @Test
    fun `API28 desugaring should succeed`() {
        expectThat(D8Runner.run(apiLevel = 28).warnings).isEmpty()
    }
}
