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

plugins {
    id("kotlin-conventions")
}

repositories {
    androidSdk()
}

configurations {
    create(Configurations.SDK)
    create(Configurations.GENERATOR)
    create(Configurations.STANDARD_SUGAR)
    create(Configurations.EXERCISE_STANDARD_SUGAR)
}

dependencies {
    extractSdk()

    add(Configurations.GENERATOR, project(":signature-builder"))

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.launcher)
}

tasks.register<TypeDescriptorsTask>(Tasks.signatures) {
    classpath = configurations.getByName(Configurations.GENERATOR)
    sdk = configurations.getByName(Configurations.SDK)
    desugar = configurations.getByName(Configurations.STANDARD_SUGAR)
    animalSnifferOutput = project.layout.buildDirectory.file(Outputs.signatures)
    expediterOutput = project.layout.buildDirectory.file(Outputs.expediter)
    outputDescription = "Android API ${project.name}"
}

tasks {
    test {
        useJUnitPlatform()

        filesProperty("sdk", configurations.named(Configurations.SDK))
        filesProperty("jar", configurations.named(Configurations.EXERCISE_STANDARD_SUGAR))

        dependsOn(Tasks.signatures)
    }
}
