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

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.project
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType

private object scopes {
    const val sugar = "sugar"
    const val sugarCalls = "sugarCalls"
}

private object downloadTasks {
    const val download = "downloadSdk"
    const val unpack = "unpackSdk"
}

fun Project.buildSignatures(
    apiLevel: String,
    sdkFile: String,
    sdkDir: String
) {
    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")
    apply(plugin = "ru.vyarus.animalsniffer")
    apply(plugin = "de.undercouch.download")
    apply(plugin = "signing")

    configurations {
        create(scopes.sugar)
        create(scopes.sugarCalls)
    }

    dependencies {
        add("implementation", kotlin("stdlib-jdk8"))

        add(scopes.sugar, project(":sugar"))
        add(scopes.sugarCalls, project(":test:sugar-calls"))

        add("testImplementation", project(":test:d8-common"))
        add("testImplementation", libraries.junit)
        add("testImplementation", libraries.truth)
    }

    val sdk = project.file("$buildDir/sdk/$sdkDir/android.jar")

    tasks.withType<Test> {
        dependsOn("unpackSdk")
        dependsOn(":sugar:build")
        dependsOn(":test:sugar-calls:build")

        systemProperty("sdk", sdk)
        systemProperty("jar", configurations.getByName(scopes.sugarCalls).asPath)
        systemProperty("dexout", project.buildDir)
    }

    configure<ru.vyarus.gradle.plugin.animalsniffer.signature.AnimalSnifferSignatureExtension> {
        files(configurations.getByName("compileClasspath").asPath)
        files(sdk)
    }

    tasks.register<de.undercouch.gradle.tasks.download.Download>(downloadTasks.download)  {
        tempAndMove(true)
        onlyIfModified(true)
        src("https://dl.google.com/android/repository/$sdkFile")
        dest("${rootProject.buildDir}/sdk-archives/$sdkFile")
    }

    tasks.register<Copy>(downloadTasks.unpack) {
        dependsOn(downloadTasks.download)
        from(zipTree("${rootProject.buildDir}/sdk-archives/$sdkFile"))
        into("$buildDir/sdk")
    }

    afterEvaluate {
        tasks.named("animalsnifferSignature") {
            dependsOn(downloadTasks.unpack)
        }

        configure<PublishingExtension> {
            publications {
                sign(create<MavenPublication>("signature") {
                    groupId = "${project.group}"
                    version = "${project.version}"
                    artifactId = "gummy-bears-api-$apiLevel"
                    artifact("${project.buildDir}/animalsniffer/signature/${project.name}.sig") {
                        extension = "signature"
                        builtBy(tasks.named("animalsnifferSignature"))
                    }

                    standardPom()
                })
            }

            publishReleasesToRemote(project)
        }
    }
}