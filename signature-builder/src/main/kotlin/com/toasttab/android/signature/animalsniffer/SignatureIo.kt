/*
 * Copyright (c) 2020. Toast Inc.
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

package com.toasttab.android.signature.animalsniffer

import org.codehaus.mojo.animal_sniffer.Clazz
import org.codehaus.mojo.animal_sniffer.SignatureBuilder
import org.codehaus.mojo.animal_sniffer.logging.PrintWriterLogger
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object SignatureIo {
    fun create(file: File): Sequence<Clazz> {
        val out = ByteArrayOutputStream()

        SignatureBuilder(out, PrintWriterLogger(System.err)).apply {
            process(file)
            close()
        }

        return deserialize(out.toByteArray().inputStream())
    }

    fun serialize(classes: Sequence<Clazz>, out: OutputStream) {
        ObjectOutputStream(GZIPOutputStream(out)).use {
            for (cls in classes) {
                it.writeObject(cls)
            }

            it.writeObject(null)
        }
    }

    fun deserialize(inputStream: InputStream) = sequence<Clazz> {
        inputStream.use {
            val objectInputStream = ObjectInputStream(GZIPInputStream(inputStream))
            do {
                val cls = objectInputStream.readObject() as Clazz?
                if (cls != null) {
                    yield(cls)
                }
            } while (cls != null)
        }
    }
}
