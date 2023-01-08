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

import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.project
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.repositories

private object Tasks {
    const val signatures = "buildSignatures"
}

private object Outputs {
    const val signatures = "signatures.sig"
}

plugins {
    id("kotlin-conventions")
    id("publishing-conventions")
}

repositories {
    androidSdk()
}

group = "com.toasttab.android"
version = rootProject.version

configurations {
    create(Configurations.SDK)
    create(Configurations.GENERATOR)
    create(Configurations.STANDARD_SUGAR)
    create(Configurations.EXERCISE_STANDARD_SUGAR)
    create(Configurations.CORE_LIB_SUGAR).isTransitive = false
}

dependencies {
    extractSdk()

    add(Configurations.GENERATOR, project(":signature-builder"))

    add(Configurations.STANDARD_SUGAR, project(":basic-sugar"))
    add(Configurations.EXERCISE_STANDARD_SUGAR, project(":test:basic-sugar-treadmill"))
    add(Configurations.CORE_LIB_SUGAR, libs.desugarJdkLibs)

    add("testImplementation", project(":test:d8-runner"))
    add("testImplementation", libs.junit)
    add("testImplementation", libs.truth)
}

tasks.named<Test>("test") {
    dependsOn(":basic-sugar:build")
    dependsOn(":test:basic-sugar-treadmill:build")

    systemProperty("sdk", configurations.getByName(Configurations.SDK).asPath)
    systemProperty("jar", configurations.getByName(Configurations.EXERCISE_STANDARD_SUGAR).asPath)
    systemProperty("dexout", project.buildDir)
}

tasks.register<JavaExec>(Tasks.signatures) {
    dependsOn(":basic-sugar:jar")
    classpath = configurations.getByName(Configurations.GENERATOR).asFileTree
    mainClass.set("com.toasttab.android.signature.animalsniffer.AndroidSignatureBuilderKt")
    args = listOf(
        "--sdk",
        configurations.getByName(Configurations.SDK).asPath,
        "--desugared",
        configurations.getByName(Configurations.STANDARD_SUGAR).asPath,
        "--output",
        "$buildDir/${Outputs.signatures}"
    )
}

publishing.publications.named<MavenPublication>(Publications.MAIN) {
    artifact("$buildDir/${Outputs.signatures}") {
        extension = "signature"
        builtBy(tasks.named(Tasks.signatures))
    }
}
