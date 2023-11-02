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

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.toasttab.expediter.ClasspathScanner
import com.toasttab.expediter.TypeParsers
import org.codehaus.mojo.animal_sniffer.Clazz
import protokt.v1.toasttab.expediter.v1.TypeDescriptors
import java.io.File
import java.util.zip.GZIPOutputStream

class AndroidSignatureBuilder : CliktCommand() {
    private val sdk: String by option(help = "SDK jar").required()
    private val desugared: List<String> by option(help = "desugared API jar(s)").multiple()

    private val name: String? by option(help = "name")
    private val output: String? by option(help = "output")
    private val expediterOutput: String? by option(help = "expediter-output")

    override fun run() {
        ClasspathScanner(listOf(File(sdk))).scan { stream, _ -> TypeParsers.typeDescriptor(stream) }

        val signatures = MutableTypeDescriptors(ClasspathScanner(listOf(File(sdk))).scan { stream, _ -> TypeParsers.typeDescriptor(stream) })

        for (more in desugared) {
            ClasspathScanner(listOf(File(more))).scan { stream, _ -> TypeParsers.typeDescriptor(stream) }.forEach {
                signatures.add(DesugarTypeTransformer.transform(it))
            }
        }

        output?.let {
            File(it).absoluteFile.run {
                parentFile.mkdirs()

                outputStream().use { out ->
                    AnimalSnifferSerializer.serialize(signatures.classes().map(AnimalSnifferConverter::convert), out)
                }
            }
        }

        expediterOutput?.let {
            val file = File(it).absoluteFile
            val descriptorsName = name ?: file.name

            file.run {
                parentFile.mkdirs()

                GZIPOutputStream(outputStream()).use { stream ->
                    TypeDescriptors {
                        description = descriptorsName
                        types = signatures.classes().toList()
                    }.serialize(stream)
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    AndroidSignatureBuilder().main(args)
}
