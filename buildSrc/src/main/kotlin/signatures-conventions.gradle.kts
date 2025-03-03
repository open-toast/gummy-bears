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

import gradle.kotlin.dsl.accessors._e054d9723d982fdb55b1e388b8ab0cbf.test
import gradle.kotlin.dsl.accessors._e054d9723d982fdb55b1e388b8ab0cbf.testRuntimeOnly
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.project
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.repositories

plugins {
    id("publishing-conventions")
    id("generate-signatures-conventions")
}

group = "com.toasttab.android"
version = rootProject.version

configurations {
    create(Configurations.CORE_LIB_SUGAR).isTransitive = false
    create(Configurations.CORE_LIB_SUGAR_2).isTransitive = false
}

dependencies {
    add(Configurations.STANDARD_SUGAR, project(":sugar:basic"))
    add(Configurations.STANDARD_SUGAR, project(":sugar:unsafe"))
    if (project.name.toInt() >= 24) {
        add(Configurations.STANDARD_SUGAR, project(":sugar:unsafe24"))
    }
    add(Configurations.EXERCISE_STANDARD_SUGAR, project(":test:basic-sugar-treadmill"))
    add(Configurations.CORE_LIB_SUGAR, libs.desugarJdkLibs)
    add(Configurations.CORE_LIB_SUGAR_2, libs.desugarJdkLibs2)

    testImplementation(project(":test:d8-runner"))
    testImplementation(project(":test:base-api-tests"))

    testImplementation(libs.strikt.core)
    testImplementation(libs.expediter.core)
    testImplementation(libs.protobuf.java)
    testImplementation(libs.animalSniffer)
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

        dependsOn(":sugar:basic:build")
        dependsOn(":test:basic-sugar-treadmill:build")
    }
}
