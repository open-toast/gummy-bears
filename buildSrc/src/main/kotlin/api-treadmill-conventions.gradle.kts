/*
 * Copyright (c) 2025. Toast Inc.
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

import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.project
import org.gradle.kotlin.dsl.register

plugins {
    id("java-conventions")
}

configurations {
    create(Configurations.STANDARD_SUGAR)
    create(Configurations.GENERATOR)
}

dependencies {
    add(Configurations.GENERATOR, project(":test:api-treadmill"))
}

tasks.register<JavaExec>("generateClasses") {
    classpath = configurations.getByName(Configurations.GENERATOR).asFileTree
    mainClass.set("com.toasttab.android.ApiUseGeneratorKt")
    args = listOf(
        "--output",
        layout.buildDirectory.file("generated-sources/java/main").path
    ) + configurations.getByName(Configurations.STANDARD_SUGAR).flatMap {
        listOf("--jar", it.path)
    }
}

tasks.named<JavaCompile>("compileJava") {
    dependsOn("generateClasses")
}

sourceSets.main {
    java.srcDir(layout.buildDirectory.file("generated-sources/java/main"))
}
