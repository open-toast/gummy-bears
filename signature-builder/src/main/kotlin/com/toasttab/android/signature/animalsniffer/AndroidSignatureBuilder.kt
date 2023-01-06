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
import java.io.File

class AndroidSignatureBuilder : CliktCommand() {
    private val sdk: String by option(help = "SDK jar").required()
    private val desugared: List<String> by option(help = "desugared API jar(s)").multiple()
    private val output: String by option(help = "output").required()

    override fun run() {
        val signatures = MutableSignatures(SignatureIo.create(File(sdk)))

        for (more in desugared) {
            SignatureIo.create(File(more)).forEach {
                signatures.add(DesugarSignatureTransformer.transform(it))
            }
        }

        File(output).absoluteFile.apply {
            parentFile.mkdirs()

            outputStream().use {
                SignatureIo.serialize(signatures.classes(), it)
            }
        }
    }
}

fun main(args: Array<String>) {
    AndroidSignatureBuilder().main(args)
}