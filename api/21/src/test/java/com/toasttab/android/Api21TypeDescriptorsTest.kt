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
import protokt.v1.toasttab.expediter.v1.AccessDeclaration
import protokt.v1.toasttab.expediter.v1.AccessProtection
import protokt.v1.toasttab.expediter.v1.MemberDescriptor
import protokt.v1.toasttab.expediter.v1.SymbolicReference
import protokt.v1.toasttab.expediter.v1.TypeDescriptors
import strikt.api.expectThat
import strikt.assertions.contains
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

        private val coreLibDescriptors2Minimal by lazy {
            descriptors("platformCoreLibDescriptors2Minimal")
        }

        private val coreLibDescriptors2 by lazy {
            descriptors("platformCoreLibDescriptors2")
        }

        private val coreLibDescriptors2Nio by lazy {
            descriptors("platformCoreLibDescriptors2Nio")
        }
    }

    @Test
    fun `core lib v2 minimal includes Function`() {
        val function = coreLibDescriptors2Minimal.types.find { it.name == "java/util/function/Function" }

        expectThat(function).isNotNull()
    }

    @Test
    fun `core lib v2 minimal excludes LocalDate`() {
        val localDate = coreLibDescriptors2Minimal.types.find { it.name == "java/time/LocalDate" }

        expectThat(localDate).isEqualTo(null)
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

    @Test
    fun `core lib v2 includes LocalDate`() {
        val localDate = coreLibDescriptors2.types.find { it.name == "java/time/LocalDate" }

        expectThat(localDate).isNotNull()
    }

    @Test
    fun `core lib v2 excludes Path`() {
        val path = coreLibDescriptors2.types.find { it.name == "java/nio/file/Path" }

        expectThat(path).isEqualTo(null)
    }

    @Test
    fun `core lib v2 NIO includes Path`() {
        val path = coreLibDescriptors2Nio.types.find { it.name == "java/nio/file/Path" }

        expectThat(path).isNotNull()
    }

    @Test
    fun `core lib v2 NIO includes OpenOption`() {
        val openOption = coreLibDescriptors2Nio.types.find { it.name == "java/nio/file/OpenOption" }

        expectThat(openOption).isNotNull()
    }

    /**
     * desugar_jdk_libs represents File.toPath() as a static toPath(File) method.
     * The generated descriptor remaps it to the instance method seen in application bytecode.
     */
    @Test
    fun `core lib v2 NIO includes remapped File#toPath()`() {
        val file = coreLibDescriptors2Nio.types.find { it.name == "java/io/File" }

        expectThat(file).isNotNull().and {
            get { methods }.contains(
                MemberDescriptor {
                    ref =
                        SymbolicReference {
                            name = "toPath"
                            signature = "()Ljava/nio/file/Path;"
                        }
                    protection = AccessProtection.PUBLIC
                    declaration = AccessDeclaration.INSTANCE
                },
            )
        }
    }
}
