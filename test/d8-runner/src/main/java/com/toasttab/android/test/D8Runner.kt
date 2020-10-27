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

package com.toasttab.android.test

import com.android.tools.r8.BaseCompilerCommand
import com.android.tools.r8.D8
import com.android.tools.r8.D8Command
import com.android.tools.r8.Diagnostic
import com.android.tools.r8.DiagnosticsHandler
import com.android.tools.r8.OutputMode
import com.android.tools.r8.errors.DesugarDiagnostic
import com.android.tools.r8.utils.Reporter
import java.nio.file.Path
import java.nio.file.Paths

class DesugaringResult(
    val warnings: List<String>
)

object D8Runner {
    fun run(apiLevel: Int,
            sdk: Path = Paths.get(System.getProperty("sdk")),
            jar: Path = Paths.get(System.getProperty("jar")),
            output: Path = Paths.get(System.getProperty("dexout"))): DesugaringResult {
        val cmd = D8Command.builder()
            .addLibraryFiles(sdk)
            .addProgramFiles(jar)
            .setOutput(output, OutputMode.DexIndexed)
            .setMinApiLevel(apiLevel)
            .build()

        val warnings = mutableListOf<String>()

        BaseCompilerCommand::class.java.declaredFields.first { it.type == Reporter::class.java }.apply {
            isAccessible = true
        }.set(cmd, Reporter(object : DiagnosticsHandler {
            override fun warning(diagnostic: Diagnostic) {
                if (diagnostic is DesugarDiagnostic) {
                    warnings.add(diagnostic.diagnosticMessage)
                }
            }
        }))

        D8.run(cmd)

        return DesugaringResult(warnings.toList())
    }
}