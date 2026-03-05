/*
 * Copyright (c) 2026. Toast Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.toasttab.android

import org.junit.jupiter.api.Test
import protokt.v1.toasttab.expediter.v1.TypeDescriptors
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import java.io.File
import java.util.zip.GZIPInputStream

class Api21TypeDescriptorsTest {
    companion object {
        private fun descriptors(name: String) =
            GZIPInputStream(File(System.getProperty(name)).inputStream()).use {
                TypeDescriptors.deserialize(it)
            }

        private val coreLibDescriptors2 by lazy {
            descriptors("platformCoreLibDescriptors2")
        }
    }

    /**
     * CompletableFuture is present in the desugar_jdk_libs JAR but is not a desugared API —
     * it is not listed in the lint file and crashes at runtime on API 21.
     * The lint file filter must exclude it from the signature.
     */
    @Test
    fun `core lib v2 excludes CompletableFuture`() {
        val completableFuture = coreLibDescriptors2.types.find { it.name == "java/util/concurrent/CompletableFuture" }

        expectThat(completableFuture).isEqualTo(null)
    }

    /**
     * Optional is a fully desugared class listed in the lint file and should be present.
     */
    @Test
    fun `core lib v2 includes Optional`() {
        val optional = coreLibDescriptors2.types.find { it.name == "java/util/Optional" }

        expectThat(optional).isNotNull()
    }
}
