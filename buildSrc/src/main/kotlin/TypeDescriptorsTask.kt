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

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
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

    @OutputFile
    lateinit var animalSnifferOutput: Any

    @OutputFile
    lateinit var expediterOutput: Any

    @Input
    lateinit var outputDescription: String

    @TaskAction
    fun exec() {
        exec.javaexec {
            mainClass.set("com.toasttab.android.descriptors.AndroidTypeDescriptorBuilderKt")
            classpath = this@TypeDescriptorsTask.classpath

            args = listOf(
                "--sdk",
                sdk.singleFile.path,
                "--animal-sniffer-output",
                project.file(animalSnifferOutput).path,
                "--expediter-output",
                project.file(expediterOutput).path,
                "--description",
                outputDescription
            ) + desugar.flatMap {
                listOf("--desugared", it.path)
            }
        }
    }
}