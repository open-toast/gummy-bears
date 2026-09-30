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

/** Defines the build and publication names for one core-library flavor. */
private data class CoreLibFlavor(
    val classifier: String,
    val artifactSuffix: String,
    val description: String,
) {
    private val configurationSuffix = artifactSuffix.replace('-', '_')
    val libraryConfiguration = "_core_lib_${configurationSuffix}_"
    val configConfiguration = "_core_lib_config_${configurationSuffix}_"
    val taskName = "buildSignatures${classifier.replaceFirstChar { it.uppercase() }}"
    val propertySuffix = classifier.removePrefix("coreLib")
    val signaturesOutput = "signaturesCoreLib-$artifactSuffix.sig"
    val expediterOutput = "platformCoreLib-$artifactSuffix.expediter"
}

private object CoreLibFlavors {
    val default = CoreLibFlavor("coreLib2", "2", "2.x")
    val minimal = CoreLibFlavor("coreLib2Minimal", "2-minimal", "2.x Minimal")
    val nio = CoreLibFlavor("coreLib2Nio", "2-nio", "2.x NIO")
    val all = listOf(default, minimal, nio)
}

plugins {
    id("signatures-conventions")
}

configurations {
    CoreLibFlavors.all.forEach { flavor ->
        create(flavor.libraryConfiguration).isTransitive = false
        create(flavor.configConfiguration).isTransitive = false
    }
}

dependencies {
    add(CoreLibFlavors.default.libraryConfiguration, libs.desugarJdkLibs2)
    add(CoreLibFlavors.default.configConfiguration, libs.desugarJdkLibsConfig2)
    add(CoreLibFlavors.minimal.libraryConfiguration, libs.desugarJdkLibs2Minimal)
    add(CoreLibFlavors.minimal.configConfiguration, libs.desugarJdkLibsConfig2Minimal)
    add(CoreLibFlavors.nio.libraryConfiguration, libs.desugarJdkLibs2Nio)
    add(CoreLibFlavors.nio.configConfiguration, libs.desugarJdkLibsConfig2Nio)
}

private val coreLibTasks =
    CoreLibFlavors.all.associateWith { flavor ->
        tasks.register<TypeDescriptorsTask>(flavor.taskName) {
            classpath = configurations.getByName(Configurations.GENERATOR)
            sdk = configurations.getByName(Configurations.ANDROID_SDK)
            desugar = configurations.getByName(Configurations.STANDARD_DESUGARED)
            desugaredCorelib.set(configurations.getByName(flavor.libraryConfiguration))
            coreLibConfigJar.set(configurations.getByName(flavor.configConfiguration))
            apiLevel.set(project.name.toInt())
            animalSnifferOutput = project.layout.buildDirectory.file(flavor.signaturesOutput)
            expediterOutput = project.layout.buildDirectory.file(flavor.expediterOutput)
            outputDescription = "Android API ${project.name} with Core Library Desugaring ${flavor.description}"
        }
    }

publishing.publications.named<MavenPublication>(Publications.MAIN) {
    CoreLibFlavors.all.forEach { flavor ->
        artifact(layout.buildDirectory.file(flavor.signaturesOutput)) {
            extension = "signature"
            classifier = flavor.classifier
            builtBy(coreLibTasks.getValue(flavor))
        }

        artifact(layout.buildDirectory.file(flavor.expediterOutput)) {
            extension = "expediter"
            classifier = flavor.classifier
            builtBy(coreLibTasks.getValue(flavor))
        }
    }
}

tasks {
    test {
        CoreLibFlavors.all.forEach { flavor ->
            fileProperty("platformCoreLibDescriptors${flavor.propertySuffix}", layout.buildDirectory.file(flavor.expediterOutput))
            fileProperty("coreLibSignatures${flavor.propertySuffix}", layout.buildDirectory.file(flavor.signaturesOutput))
        }

        dependsOn(coreLibTasks.values)
    }
}
