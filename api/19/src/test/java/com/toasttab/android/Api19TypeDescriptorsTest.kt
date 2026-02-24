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

import org.junit.jupiter.api.Test
import protokt.v1.toasttab.expediter.v1.AccessDeclaration
import protokt.v1.toasttab.expediter.v1.AccessProtection
import protokt.v1.toasttab.expediter.v1.MemberDescriptor
import protokt.v1.toasttab.expediter.v1.SymbolicReference
import protokt.v1.toasttab.expediter.v1.TypeDescriptors
import protokt.v1.toasttab.expediter.v1.TypeExtensibility
import protokt.v1.toasttab.expediter.v1.TypeFlavor
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.doesNotContain
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import java.io.File
import java.util.zip.GZIPInputStream

class Api19TypeDescriptorsTest {
    companion object {
        private fun descriptors(name: String) =
            GZIPInputStream(File(System.getProperty(name)).inputStream()).use {
                TypeDescriptors.deserialize(it)
            }

        private val descriptors by lazy {
            descriptors("platformDescriptors")
        }

        private val coreLibDescriptors by lazy {
            descriptors("platformCoreLibDescriptors")
        }

        private val coreLibDescriptors2 by lazy {
            descriptors("platformCoreLibDescriptors2")
        }
    }

    @Test
    fun `type descriptors include Integer#hashCode(int)`() {
        val integer = descriptors.types.find { it.name == "java/lang/Integer" }

        expectThat(integer).isNotNull().and {
            get { methods }.contains(
                MemberDescriptor {
                    ref =
                        SymbolicReference {
                            name = "hashCode"
                            signature = "(I)I"
                        }
                    protection = AccessProtection.PUBLIC
                    declaration = AccessDeclaration.STATIC
                },
            )
        }
    }

    @Test
    fun `core lib type descriptors include Stream#count()`() {
        val stream = coreLibDescriptors.types.find { it.name == "java/util/stream/Stream" }

        expectThat(stream).isNotNull().and {
            get { methods }.contains(
                MemberDescriptor {
                    ref =
                        SymbolicReference {
                            name = "count"
                            signature = "()J"
                        }
                    protection = AccessProtection.PUBLIC
                    declaration = AccessDeclaration.INSTANCE
                },
            )
        }
    }

    /**
     * java.util.Base64 is included in desugar_jdk_libs starting with version 2.0.4
     */
    @Test
    fun `core lib v2 type descriptors include Base64$Decoder#decode`() {
        val decoder = coreLibDescriptors2.types.find { it.name == "java/util/Base64\$Decoder" }

        expectThat(decoder).isNotNull().and {
            get { methods }.contains(
                MemberDescriptor {
                    ref =
                        SymbolicReference {
                            name = "decode"
                            signature = "([B)[B"
                        }
                    protection = AccessProtection.PUBLIC
                    declaration = AccessDeclaration.INSTANCE
                },
            )
        }
    }

    /**
     * Tests an edge case in merging type descriptors, where multiple versions of `LinuxFileSystemProvider` are provided,
     * one extending `UnixFileSystemProvider` and another extending `FileSystem`
     */
    @Test
    fun `core lib v2 LinuxFileSystemProvider extends UnixFileSystemProvider`() {
        val provider = coreLibDescriptors2.types.find { it.name == "sun/nio/fs/LinuxFileSystemProvider" }

        expectThat(provider).isNotNull().and {
            get { superName }.isEqualTo("sun/nio/fs/UnixFileSystemProvider")
        }
    }

    /**
     * Tests an edge case in merging type descriptors, where multiple versions of `MimeTypesFileTypeDetector` are
     * provided, one with the transformed `desugar/sun/nio/fs/DesugarAbstractFileTypeDetector` name
     */
    @Test
    fun `core lib v2 MimeTypesFileTypeDetector extends AbstractFileTypeDetector`() {
        val detector = coreLibDescriptors2.types.find { it.name == "sun/nio/fs/MimeTypesFileTypeDetector" }

        expectThat(detector).isNotNull().and {
            get { superName }.isEqualTo("sun/nio/fs/AbstractFileTypeDetector")
        }
    }

    /**
     * Tests an edge case in merging type descriptors, where multiple versions of `IntStream`
     * are provided; one is an interface, and another one is a class
     */
    @Test
    fun `core lib v2 IntStream is an interface`() {
        val stream = coreLibDescriptors2.types.find { it.name == "java/util/stream/IntStream" }

        expectThat(stream).isNotNull().and {
            get { flavor }.isEqualTo(TypeFlavor.INTERFACE)
            get { extensibility }.isEqualTo(TypeExtensibility.NOT_FINAL)
        }
    }

    /**
     * Tests an edge case in merging type descriptors, where multiple versions of `Character`
     * are provided; one is final, and another one is not
     */
    @Test
    fun `core lib v2 Character is final`() {
        val character = coreLibDescriptors2.types.find { it.name == "java/lang/Character" }

        expectThat(character).isNotNull().and {
            get { extensibility }.isEqualTo(TypeExtensibility.FINAL)
        }
    }

    @Test
    fun `descriptors include Unsafe#getInt`() {
        val integer = descriptors.types.find { it.name == "sun/misc/Unsafe" }

        expectThat(integer).isNotNull().and {
            get { methods }.contains(
                MemberDescriptor {
                    ref =
                        SymbolicReference {
                            name = "getInt"
                            signature = "(Ljava/lang/Object;J)I"
                        }
                    declaration = AccessDeclaration.INSTANCE
                    protection = AccessProtection.PUBLIC
                },
            )
        }
    }

    @Test
    fun `descriptors do not include Unsafe#storeFence`() {
        val integer = descriptors.types.find { it.name == "sun/misc/Unsafe" }

        expectThat(integer).isNotNull().and {
            get { methods }.doesNotContain(
                MemberDescriptor {
                    ref =
                        SymbolicReference {
                            name = "storeFence"
                            signature = "()V"
                        }
                    declaration = AccessDeclaration.INSTANCE
                    protection = AccessProtection.PUBLIC
                },
            )
        }
    }
}
