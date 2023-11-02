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
import org.gradle.kotlin.dsl.register

private object Tasks {
    const val signaturesCoreLib = "buildSignaturesCoreLib"
}

private object Outputs {
    const val signaturesCoreLib = "signaturesCoreLib.sig"
    const val expediterCoreLib = "platformCoreLib.expediter"
}

plugins {
    id("signatures-conventions")
}

tasks.register<JavaExec>(Tasks.signaturesCoreLib) {
    dependsOn(":basic-sugar:jar")
    classpath = configurations.getByName(Configurations.GENERATOR).asFileTree
    mainClass.set("com.toasttab.android.signature.animalsniffer.AndroidSignatureBuilderKt")
    args = listOf(
        "--sdk",
        configurations.getByName(Configurations.SDK).asPath,
        "--desugared",
        configurations.getByName(Configurations.CORE_LIB_SUGAR).asPath,
        "--desugared",
        configurations.getByName(Configurations.STANDARD_SUGAR).asPath,
        "--output",
        "$buildDir/${Outputs.signaturesCoreLib}",
        "--expediter-output",
        "$buildDir/${Outputs.expediterCoreLib}",
        "--name",
        "Android API ${project.name} with Core Library Desugaring"
    )
}

publishing.publications.named<MavenPublication>(Publications.MAIN) {
    artifact("$buildDir/${Outputs.signaturesCoreLib}") {
        extension = "signature"
        classifier = "coreLib"
        builtBy(tasks.named(Tasks.signaturesCoreLib))
    }

    artifact("$buildDir/${Outputs.expediterCoreLib}") {
        extension = "expediter"
        classifier = "coreLib"
        builtBy(tasks.named(Tasks.signaturesCoreLib))
    }
}
