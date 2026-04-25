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

import LintFileSelector.selectLintFile
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

@CacheableTask
abstract class TypeDescriptorsTask @Inject constructor(
    private val exec: ExecOperations
) : DefaultTask() {
    @Classpath
    lateinit var classpath: FileCollection

    @InputFiles
    @PathSensitive(PathSensitivity.ABSOLUTE)
    lateinit var sdk: FileCollection

    @InputFiles
    @PathSensitive(PathSensitivity.ABSOLUTE)
    lateinit var desugar: FileCollection

    /**
     * CoreLib desugared JAR(s) to be filtered by the lint file. Passed to the builder
     * via `--desugared-corelib` so that the lint filter applies only to these JARs.
     */
    @get:InputFiles
    @get:Optional
    @get:PathSensitive(PathSensitivity.ABSOLUTE)
    abstract val desugaredCorelib: Property<FileCollection>

    /**
     * The `desugar_jdk_libs_configuration` JAR containing desugared API lint files.
     * When set together with [apiLevel], the best lint file is extracted and passed
     * to the builder to filter coreLib classes to only truly-desugared APIs.
     */
    @get:InputFiles
    @get:Optional
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val coreLibConfigJar: Property<FileCollection>

    @get:Input
    @get:Optional
    abstract val apiLevel: Property<Int>

    @OutputFile
    lateinit var animalSnifferOutput: Any

    @OutputFile
    lateinit var expediterOutput: Any

    @Input
    lateinit var outputDescription: String

    @TaskAction
    fun exec() {
        val lintFileUri = if (coreLibConfigJar.isPresent && apiLevel.isPresent) {
            selectLintFile(
                coreLibConfigJar.get().singleFile,
                apiLevel.get()
            )
        } else {
            null
        }

        exec.javaexec {
            mainClass.set("com.toasttab.android.descriptors.AndroidTypeDescriptorBuilderKt")
            classpath = this@TypeDescriptorsTask.classpath

            args = buildList {
                addAll(
                    listOf(
                        "--sdk",
                        sdk.singleFile.path,
                        "--animal-sniffer-output",
                        project.file(animalSnifferOutput).path,
                        "--expediter-output",
                        project.file(expediterOutput).path,
                        "--description",
                        outputDescription
                    )
                )
                addAll(
                    desugar.flatMap {
                        listOf("--desugared", it.path)
                    }
                )
                if (desugaredCorelib.isPresent) {
                    addAll(
                        desugaredCorelib.get().flatMap {
                            listOf("--desugared-corelib", it.path)
                        }
                    )
                }
                if (lintFileUri != null) {
                    addAll(listOf("--lint-file", lintFileUri.toString()))
                }
            }
        }
    }
}