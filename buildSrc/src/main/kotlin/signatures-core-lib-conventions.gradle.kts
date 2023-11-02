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

tasks.register<SignaturesTask>(Tasks.signaturesCoreLib) {
    classpath = configurations.getByName(Configurations.GENERATOR)
    sdk = configurations.getByName(Configurations.SDK)
    desugar = configurations.getByName(Configurations.STANDARD_SUGAR) + configurations.getByName(Configurations.CORE_LIB_SUGAR)
    output = project.layout.buildDirectory.file(Outputs.signaturesCoreLib)
    expediterOutput = project.layout.buildDirectory.file(Outputs.expediterCoreLib)
    outputDescription = "Android API ${project.name} with Core Library Desugaring"
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

tasks {
    test {
        environment("platformCoreLib", "$buildDir/${Outputs.expediterCoreLib}")
        inputs.file("$buildDir/${Outputs.expediterCoreLib}").withPropertyName(Outputs.expediterCoreLib)

        dependsOn(Tasks.signaturesCoreLib)
    }
}
