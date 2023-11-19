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
import strikt.assertions.isA
import strikt.assertions.isNotNull
import java.io.File
import java.io.ObjectInputStream
import java.util.zip.GZIPInputStream

class Api19SignaturesTest {
    @Test
    fun `signatures include Integer#hashCode(int)`() {
        val desc = ObjectInputStream(GZIPInputStream(File(System.getProperty("signatures")).inputStream())).use {
            generateSequence { it.readObject() as Clazz? }.toList()
        }

        val integer = desc.find { it.name == "java/lang/Integer" }

        expectThat(integer).isNotNull().and {
            get { signatures }.contains("hashCode(I)I")
        }
    }

    @Test
    fun `core lib signatures include Stream#count()`() {
        val desc = ObjectInputStream(GZIPInputStream(File(System.getProperty("coreLibSignatures")).inputStream())).use {
            generateSequence { it.readObject() as Clazz? }.toList()
        }

        val stream = desc.find { it.name == "java/util/stream/Stream" }

        expectThat(stream).isNotNull().and {
            get { signatures }.contains("count()J")
        }
    }

    @Test
    fun `core lib v2 signatures include Base64$Decoder#decode`() {
        val desc = ObjectInputStream(GZIPInputStream(File(System.getProperty("coreLibSignatures2")).inputStream())).use {
            generateSequence { it.readObject() as Clazz? }.toList()
        }

        val stream = desc.find { it.name == "java/util/Base64\$Decoder" }

        expectThat(stream).isNotNull().and {
            get { signatures }.contains("decode([B)[B")
        }
    }

    @Test
    fun `signatures use HashSet`() {
        val desc = ObjectInputStream(GZIPInputStream(File(System.getProperty("signatures")).inputStream())).use {
            generateSequence { it.readObject() as Clazz? }.toList()
        }

        expectThat(desc).all {
            get { signatures }.isA<HashSet<*>>()
        }
    }

    @Test
    fun `core lib signatures use HashSet`() {
        val desc = ObjectInputStream(GZIPInputStream(File(System.getProperty("coreLibSignatures")).inputStream())).use {
            generateSequence { it.readObject() as Clazz? }.toList()
        }

        expectThat(desc).all {
            get { signatures }.isA<HashSet<*>>()
        }
    }
}
