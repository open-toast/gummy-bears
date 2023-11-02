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
        val desc = GZIPInputStream(File(System.getenv("platform")).inputStream()).use {
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
    fun `type descriptors include Stream#count()`() {
        val desc = GZIPInputStream(File(System.getenv("platformCoreLib")).inputStream()).use {
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
}
