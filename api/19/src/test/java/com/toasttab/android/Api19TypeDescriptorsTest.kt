/*
 * Copyright (c) 2023. Toast Inc.
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

import org.junit.Test
import protokt.v1.toasttab.expediter.v1.AccessDeclaration
import protokt.v1.toasttab.expediter.v1.AccessProtection
import protokt.v1.toasttab.expediter.v1.MemberDescriptor
import protokt.v1.toasttab.expediter.v1.SymbolicReference
import protokt.v1.toasttab.expediter.v1.TypeDescriptors
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.isNotNull
import java.io.File
import java.util.zip.GZIPInputStream

class Api19TypeDescriptorsTest {
    @Test
    fun `type descriptors include Integer#hashCode(int)`() {
        val desc = GZIPInputStream(File(System.getProperty("platformDescriptors")).inputStream()).use {
            TypeDescriptors.deserialize(it)
        }

        val integer = desc.types.find { it.name == "java/lang/Integer" }

        expectThat(integer).isNotNull().and {
            get { methods }.contains(
                MemberDescriptor {
                    ref = SymbolicReference {
                        name = "hashCode"
                        signature = "(I)I"
                    }
                    protection = AccessProtection.PUBLIC
                    declaration = AccessDeclaration.STATIC
                }
            )
        }
    }

    @Test
    fun `core lib type descriptors include Stream#count()`() {
        val desc = GZIPInputStream(File(System.getProperty("platformCoreLibDescriptors")).inputStream()).use {
            TypeDescriptors.deserialize(it)
        }

        val stream = desc.types.find { it.name == "java/util/stream/Stream" }

        expectThat(stream).isNotNull().and {
            get { methods }.contains(
                MemberDescriptor {
                    ref = SymbolicReference {
                        name = "count"
                        signature = "()J"
                    }
                    protection = AccessProtection.PUBLIC
                    declaration = AccessDeclaration.INSTANCE
                }
            )
        }
    }

    @Test
    fun `core lib v2 type descriptors include Base64$Decoder#decode`() {
        val desc = GZIPInputStream(File(System.getProperty("platformCoreLibDescriptors2")).inputStream()).use {
            TypeDescriptors.deserialize(it)
        }

        val stream = desc.types.find { it.name == "java/util/Base64\$Decoder" }

        expectThat(stream).isNotNull().and {
            get { methods }.contains(
                MemberDescriptor {
                    ref = SymbolicReference {
                        name = "decode"
                        signature = "([B)[B"
                    }
                    protection = AccessProtection.PUBLIC
                    declaration = AccessDeclaration.INSTANCE
                }
            )
        }
    }
}
