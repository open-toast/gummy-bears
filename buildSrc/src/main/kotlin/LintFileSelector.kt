/*
 * Copyright (c) 2026. Toast Inc.
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

import java.io.File
import java.util.zip.ZipFile

object LintFileSelector {
    private val LINT_FILE_PATTERN = Regex("""desugared_apis_(\d+)_(\d+)\.txt""")

    /**
     * Represents a parsed lint file with its compileSdk and minSdk thresholds.
     */
    private data class LintFileCandidate(
        val file: File,
        val compileSdk: Int,
        val minSdk: Int,
    )

    /**
     * Extracts lint files from a `desugar_jdk_libs_configuration` JAR and selects the best one
     * for the given API level. Mirrors Android Gradle Plugin's selection logic:
     * 1. Pick the highest compileSdk directory available.
     * 2. Within that, pick the lint file with the highest minSdk threshold that does not exceed
     *    the target API level.
     *
     * @param configJar the `desugar_jdk_libs_configuration` JAR file
     * @param apiLevel the target Android API level (minSdk)
     * @param outputDir directory to extract lint files into
     * @return the selected lint file, or null if no matching lint file was found
     */
    fun selectLintFile(configJar: File, apiLevel: Int, outputDir: File): File? {
        outputDir.mkdirs()

        val candidates = mutableListOf<LintFileCandidate>()

        ZipFile(configJar).use { zip ->
            for (entry in zip.entries()) {
                if (entry.isDirectory) continue

                val match = LINT_FILE_PATTERN.find(entry.name) ?: continue
                val compileSdk = match.groupValues[1].toInt()
                val minSdk = match.groupValues[2].toInt()

                val outFile = File(outputDir, entry.name)
                outFile.parentFile.mkdirs()
                zip.getInputStream(entry).use { input ->
                    outFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                candidates.add(LintFileCandidate(outFile, compileSdk, minSdk))
            }
        }

        if (candidates.isEmpty()) return null

        val highestCompileSdk = candidates.maxOf { it.compileSdk }

        return candidates
            .filter { it.compileSdk == highestCompileSdk && it.minSdk <= apiLevel }
            .maxByOrNull { it.minSdk }
            ?.file
    }
}