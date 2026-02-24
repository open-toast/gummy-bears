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
import org.gradle.kotlin.dsl.register

private object CoreLibTasks {
    const val signaturesCoreLib = "buildSignaturesCoreLib"
    const val signaturesCoreLib2 = "buildSignaturesCoreLib2"
}

private object CoreLibOutputs {
    const val signaturesCoreLib = "signaturesCoreLib.sig"
    const val expediterCoreLib = "platformCoreLib.expediter"
    const val signaturesCoreLib2 = "signaturesCoreLib-2.sig"
    const val expediterCoreLib2 = "platformCoreLib-2.expediter"
}

plugins {
    id("signatures-conventions")
}

tasks.register<TypeDescriptorsTask>(CoreLibTasks.signaturesCoreLib) {
    classpath = configurations.getByName(Configurations.GENERATOR)
    sdk = configurations.getByName(Configurations.ANDROID_SDK)
    desugar = configurations.getByName(Configurations.STANDARD_DESUGARED) + configurations.getByName(Configurations.CORE_LIB)
    animalSnifferOutput = project.layout.buildDirectory.file(CoreLibOutputs.signaturesCoreLib)
    expediterOutput = project.layout.buildDirectory.file(CoreLibOutputs.expediterCoreLib)
    outputDescription = "Android API ${project.name} with Core Library Desugaring 1.x"
}

tasks.register<TypeDescriptorsTask>(CoreLibTasks.signaturesCoreLib2) {
    classpath = configurations.getByName(Configurations.GENERATOR)
    sdk = configurations.getByName(Configurations.ANDROID_SDK)
    desugar = configurations.getByName(Configurations.STANDARD_DESUGARED) + configurations.getByName(Configurations.CORE_LIB_2)
    animalSnifferOutput = project.layout.buildDirectory.file(CoreLibOutputs.signaturesCoreLib2)
    expediterOutput = project.layout.buildDirectory.file(CoreLibOutputs.expediterCoreLib2)
    outputDescription = "Android API ${project.name} with Core Library Desugaring 2.x"
}


publishing.publications.named<MavenPublication>(Publications.MAIN) {
    artifact(layout.buildDirectory.file(CoreLibOutputs.signaturesCoreLib)) {
        extension = "signature"
        classifier = "coreLib"
        builtBy(tasks.named(CoreLibTasks.signaturesCoreLib))
    }

    artifact(layout.buildDirectory.file(CoreLibOutputs.expediterCoreLib)) {
        extension = "expediter"
        classifier = "coreLib"
        builtBy(tasks.named(CoreLibTasks.signaturesCoreLib))
    }

    artifact(layout.buildDirectory.file(CoreLibOutputs.signaturesCoreLib2)) {
        extension = "signature"
        classifier = "coreLib2"
        builtBy(tasks.named(CoreLibTasks.signaturesCoreLib2))
    }

    artifact(layout.buildDirectory.file(CoreLibOutputs.expediterCoreLib2)) {
        extension = "expediter"
        classifier = "coreLib2"
        builtBy(tasks.named(CoreLibTasks.signaturesCoreLib2))
    }
}

tasks {
    test {
        fileProperty("platformCoreLibDescriptors", layout.buildDirectory.file(CoreLibOutputs.expediterCoreLib))
        fileProperty("coreLibSignatures", layout.buildDirectory.file(CoreLibOutputs.signaturesCoreLib))

        fileProperty("platformCoreLibDescriptors2", layout.buildDirectory.file(CoreLibOutputs.expediterCoreLib2))
        fileProperty("coreLibSignatures2", layout.buildDirectory.file(CoreLibOutputs.signaturesCoreLib2))

        dependsOn(CoreLibTasks.signaturesCoreLib)
        dependsOn(CoreLibTasks.signaturesCoreLib2)
    }
}
