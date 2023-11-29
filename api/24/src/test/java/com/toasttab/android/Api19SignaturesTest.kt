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
import strikt.assertions.contains
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
    }

    @Test
    fun `signatures include Unsafe#getInt`() {
        val integer = signatures.find { it.name == "sun/misc/Unsafe" }

        expectThat(integer).isNotNull().and {
            get { signatures }.contains("getInt(Ljava/lang/Object;J)I")
        }
    }

    @Test
    fun `signatures include Unsafe#storeFence`() {
        val integer = signatures.find { it.name == "sun/misc/Unsafe" }

        expectThat(integer).isNotNull().and {
            get { signatures }.contains("storeFence()V")
        }
    }
}
