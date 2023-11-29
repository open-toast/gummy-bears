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

import org.codehaus.mojo.animal_sniffer.Clazz
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.all
import strikt.assertions.contains
import strikt.assertions.doesNotContain
import strikt.assertions.isA
import strikt.assertions.isNotNull
import java.io.File
import java.io.ObjectInputStream
import java.util.zip.GZIPInputStream

class Api19SignaturesTest {
    companion object {
        private fun signatures(name: String) =
            ObjectInputStream(GZIPInputStream(File(System.getProperty(name)).inputStream())).use {
                generateSequence { it.readObject() as Clazz? }.toList()
            }

        private val signatures by lazy {
            signatures("signatures")
        }

        private val coreLibSignatures by lazy {
            signatures("coreLibSignatures")
        }

        private val coreLibSignatures2 by lazy {
            signatures("coreLibSignatures2")
        }
    }

    @Test
    fun `signatures include Integer#hashCode(int)`() {
        val integer = signatures.find { it.name == "java/lang/Integer" }

        expectThat(integer).isNotNull().and {
            get { signatures }.contains("hashCode(I)I")
        }
    }

    @Test
    fun `signatures include Unsafe#getInt`() {
        val integer = signatures.find { it.name == "sun/misc/Unsafe" }

        expectThat(integer).isNotNull().and {
            get { signatures }.contains("getInt(Ljava/lang/Object;J)I")
        }
    }

    @Test
    fun `signatures do not include Unsafe#storeFence`() {
        val integer = signatures.find { it.name == "sun/misc/Unsafe" }

        expectThat(integer).isNotNull().and {
            get { signatures }.doesNotContain("storeFence()V")
        }
    }

    @Test
    fun `core lib signatures include Stream#count()`() {
        val stream = coreLibSignatures.find { it.name == "java/util/stream/Stream" }

        expectThat(stream).isNotNull().and {
            get { signatures }.contains("count()J")
        }
    }

    @Test
    fun `core lib v2 signatures include Base64$Decoder#decode`() {
        val stream = coreLibSignatures2.find { it.name == "java/util/Base64\$Decoder" }

        expectThat(stream).isNotNull().and {
            get { signatures }.contains("decode([B)[B")
        }
    }

    @Test
    fun `signatures use HashSet`() {
        expectThat(signatures).all {
            get { signatures }.isA<HashSet<*>>()
        }
    }

    @Test
    fun `core lib signatures use HashSet`() {
        expectThat(coreLibSignatures).all {
            get { signatures }.isA<HashSet<*>>()
        }
    }
}
