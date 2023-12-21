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

package com.toasttab.android.descriptors

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.toasttab.android.descriptors.sniffer.AnimalSnifferConverter
import com.toasttab.android.descriptors.sniffer.AnimalSnifferSerializer
import com.toasttab.expediter.parser.TypeParsers
import com.toasttab.expediter.scanner.ClasspathScanner
import protokt.v1.toasttab.expediter.v1.TypeDescriptors
import java.io.File
import java.util.zip.GZIPOutputStream

class AndroidTypeDescriptorBuilder : CliktCommand() {
    private val sdk: String by option(help = "SDK jar").required()
    private val desugared: List<String> by option(help = "desugared API jar(s)").multiple()

    private val description: String by option(help = "description").required()
    private val animalSnifferOutput: String by option(help = "animal-sniffer-output").required()
    private val expediterOutput: String by option(help = "expediter-output").required()

    override fun run() {
        ClasspathScanner(listOf(File(sdk))).scan { stream, _ -> TypeParsers.typeDescriptor(stream) }

        val signatures = MutableTypeDescriptors(ClasspathScanner(listOf(File(sdk))).scan { stream, _ -> TypeParsers.typeDescriptor(stream) })

        for (more in desugared) {
            ClasspathScanner(listOf(File(more))).scan { stream, _ -> TransformedTypeDescriptor(TypeParsers.typeDescriptor(stream)) }
                .sortedBy { it.priority }
                .forEach {
                    signatures.add(it.toType())
                }
        }

        File(animalSnifferOutput).absoluteFile.run {
            parentFile.mkdirs()

            outputStream().use { out ->
                AnimalSnifferSerializer.serialize(signatures.classes().map(AnimalSnifferConverter::convert), out)
            }
        }

        File(expediterOutput).absoluteFile.run {
            parentFile.mkdirs()

            GZIPOutputStream(outputStream()).use { stream ->
                TypeDescriptors {
                    description = this@AndroidTypeDescriptorBuilder.description
                    types = signatures.classes().toList()
                }.serialize(stream)
            }
        }
    }
}

fun main(args: Array<String>) {
    AndroidTypeDescriptorBuilder().main(args)
}
