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
import java.net.URI
import java.util.zip.ZipFile

object LintFileSelector {
    private val LINT_FILE_PATTERN = Regex("""desugared_apis_(\d+)_(\d+)\.txt""")

    private data class LintFileCandidate(
        val entryName: String,
        val compileSdk: Int,
        val minSdk: Int,
    )

    /**
     * Selects the best lint file from a `desugar_jdk_libs_configuration` JAR for the given API
     * level. Mirrors Android Gradle Plugin's selection logic:
     * 1. Pick the highest compileSdk directory available.
     * 2. Within that, pick the lint file with the highest minSdk threshold that does not exceed
     *    the target API level.
     *
     * @param configJar the `desugar_jdk_libs_configuration` JAR file
     * @param apiLevel the target Android API level (minSdk)
     * @return a `jar:` URI pointing to the selected lint file inside the JAR, or null if no
     *         matching lint file was found
     */
    fun selectLintFile(configJar: File, apiLevel: Int): URI? {
        val candidates = mutableListOf<LintFileCandidate>()

        ZipFile(configJar).use { zip ->
            for (entry in zip.entries()) {
                if (entry.isDirectory) continue

                val match = LINT_FILE_PATTERN.find(entry.name) ?: continue
                val compileSdk = match.groupValues[1].toInt()
                val minSdk = match.groupValues[2].toInt()

                candidates.add(LintFileCandidate(entry.name, compileSdk, minSdk))
            }
        }

        if (candidates.isEmpty()) return null

        val highestCompileSdk = candidates.maxOf { it.compileSdk }

        val selected = candidates
            .filter { it.compileSdk == highestCompileSdk && it.minSdk <= apiLevel }
            .maxByOrNull { it.minSdk }
            ?: return null

        return URI("jar:${configJar.toURI()}!/${selected.entryName}")
    }
}