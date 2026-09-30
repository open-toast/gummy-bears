/*
 * Copyright (c) 2026. Toast Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
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
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.doesNotContain
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import java.io.File
import java.io.ObjectInputStream
import java.util.zip.GZIPInputStream

class Api21SignaturesTest {
    companion object {
        private fun signatures(name: String) =
            ObjectInputStream(GZIPInputStream(File(System.getProperty(name)).inputStream())).use {
                generateSequence { it.readObject() as Clazz? }.toList()
            }

        private val coreLibSignatures2Minimal by lazy {
            signatures("coreLibSignatures2Minimal")
        }

        private val coreLibSignatures2 by lazy {
            signatures("coreLibSignatures2")
        }

        private val coreLibSignatures2Nio by lazy {
            signatures("coreLibSignatures2Nio")
        }
    }

    @Test
    fun `core lib v2 minimal signatures include Function`() {
        val function = coreLibSignatures2Minimal.find { it.name == "java/util/function/Function" }

        expectThat(function).isNotNull()
    }

    @Test
    fun `core lib v2 minimal signatures exclude LocalDate`() {
        val localDate = coreLibSignatures2Minimal.find { it.name == "java/time/LocalDate" }

        expectThat(localDate).isEqualTo(null)
    }

    @Test
    fun `core lib v2 signatures include LocalDate`() {
        val localDate = coreLibSignatures2.find { it.name == "java/time/LocalDate" }

        expectThat(localDate).isNotNull()
    }

    @Test
    fun `core lib v2 signatures exclude File#toPath()`() {
        val file = coreLibSignatures2.find { it.name == "java/io/File" }

        expectThat(file).isNotNull().and {
            get { signatures }.doesNotContain("toPath()Ljava/nio/file/Path;")
        }
    }

    @Test
    fun `core lib v2 NIO signatures include File#toPath()`() {
        val file = coreLibSignatures2Nio.find { it.name == "java/io/File" }

        expectThat(file).isNotNull().and {
            get { signatures }.contains("toPath()Ljava/nio/file/Path;")
        }
    }

    @Test
    fun `core lib v2 NIO signatures include OpenOption`() {
        val openOption = coreLibSignatures2Nio.find { it.name == "java/nio/file/OpenOption" }

        expectThat(openOption).isNotNull()
    }
}
