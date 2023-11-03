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

import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.the

fun Project.isRelease() = !version.toString().endsWith("-SNAPSHOT")

val Project.libs get() = the<org.gradle.accessors.dm.LibrariesForLibs>()

val DirectoryProperty.path get() = asFile.get().path

val Provider<RegularFile>.path get() = get().asFile.path

fun Test.fileProperty(name: String, file: Provider<RegularFile>) {
    systemProperty(name, file.path)

    inputs.file(file).withPathSensitivity(PathSensitivity.RELATIVE).withPropertyName("system-property-$name")
}
fun Test.filesProperty(name: String, files: NamedDomainObjectProvider<Configuration>) {
    systemProperty(name, files.get().asPath)

    inputs.files(files).withPathSensitivity(PathSensitivity.RELATIVE).withPropertyName("system-property-$name")

}