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