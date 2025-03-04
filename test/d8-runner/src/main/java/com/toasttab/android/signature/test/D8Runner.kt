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

package com.toasttab.android.signature.test

import com.android.tools.r8.D8
import com.android.tools.r8.D8Command
import com.android.tools.r8.Diagnostic
import com.android.tools.r8.DiagnosticsHandler
import com.android.tools.r8.OutputMode
import com.android.tools.r8.errors.DesugarDiagnostic
import java.nio.file.Path

class D8Result(
    val output: Path,
    val sdk: Path,
    val desugaringWarnings: List<String>
)

class DesugarWarningCollector : DiagnosticsHandler {
    private val warnings = mutableListOf<String>()

    fun warnings(): List<String> = warnings

    override fun warning(diagnostic: Diagnostic) {
        if (diagnostic is DesugarDiagnostic) {
            warnings.add(diagnostic.diagnosticMessage)
        }
    }
}

object D8Runner {
    fun run(
        apiLevel: Int,
        sdk: Path,
        jar: Path,
        output: Path
    ): D8Result {
        val collector = DesugarWarningCollector()

        D8.run(
            D8Command.builder(collector)
                .addLibraryFiles(sdk)
                .addProgramFiles(jar)
                .setOutput(output, OutputMode.ClassFile)
                .setMinApiLevel(apiLevel)
                .build()
        )

        return D8Result(output, sdk, collector.warnings())
    }
}
