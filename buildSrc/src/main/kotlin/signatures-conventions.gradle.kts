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
    const val expediter = "platform.expediter"
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
    create(Configurations.CORE_LIB_SUGAR_2).isTransitive = false
}

dependencies {
    extractSdk()

    add(Configurations.GENERATOR, project(":signature-builder"))

    add(Configurations.STANDARD_SUGAR, project(":basic-sugar"))
    add(Configurations.EXERCISE_STANDARD_SUGAR, project(":test:basic-sugar-treadmill"))
    add(Configurations.CORE_LIB_SUGAR, libs.desugarJdkLibs)
    add(Configurations.CORE_LIB_SUGAR_2, libs.desugarJdkLibs2)

    testImplementation(project(":test:d8-runner"))
    testImplementation(libs.junit)
    testImplementation(libs.strikt.core)
    testImplementation(libs.expediter.core)
    testImplementation(libs.protobuf.java)
    testImplementation(libs.animalSniffer)
}

tasks.register<TypeDescriptorsTask>(Tasks.signatures) {
    classpath = configurations.getByName(Configurations.GENERATOR)
    sdk = configurations.getByName(Configurations.SDK)
    desugar = configurations.getByName(Configurations.STANDARD_SUGAR)
    animalSnifferOutput = project.layout.buildDirectory.file(Outputs.signatures)
    expediterOutput = project.layout.buildDirectory.file(Outputs.expediter)
    outputDescription = "Android API ${project.name}"
}

publishing.publications.named<MavenPublication>(Publications.MAIN) {
    artifact(layout.buildDirectory.file(Outputs.signatures)) {
        extension = "signature"
        builtBy(tasks.named(Tasks.signatures))
    }

    artifact(layout.buildDirectory.file(Outputs.expediter)) {
        extension = "expediter"
        builtBy(tasks.named(Tasks.signatures))
    }
}

tasks {
    test {
        fileProperty("platformDescriptors", layout.buildDirectory.file(Outputs.expediter))
        fileProperty("signatures", layout.buildDirectory.file(Outputs.signatures))
        filesProperty("sdk", configurations.named(Configurations.SDK))
        filesProperty("jar", configurations.named(Configurations.EXERCISE_STANDARD_SUGAR))

        systemProperty("dexout", layout.buildDirectory.path)

        dependsOn(":basic-sugar:build")
        dependsOn(":test:basic-sugar-treadmill:build")

        dependsOn(Tasks.signatures)
    }
}
