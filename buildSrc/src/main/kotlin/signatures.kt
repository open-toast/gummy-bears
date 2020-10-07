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
import ru.vyarus.gradle.plugin.animalsniffer.signature.BuildSignatureTask

private object Scopes {
    const val sugar = "sugar"
    const val sugarCalls = "sugarCalls"
    const val coreLibDesugaring = "coreLibDesugaring"
}

private object Tasks {
    const val download = "downloadSdk"
    const val unpack = "unpackSdk"

    const val signatures = "buildSignatures"
    const val signaturesCoreLib = "buildSignaturesCoreLib"
}

private object Outputs {
    const val signatures = "signatures"
    const val signaturesCoreLib = "signaturesCoreLib"
}

fun Project.buildSignatures(
    apiLevel: String,
    sdkFile: String,
    sdkDir: String,
    coreLibDesugaring: Boolean = false
) {
    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")
    apply(plugin = "ru.vyarus.animalsniffer")
    apply(plugin = "de.undercouch.download")
    apply(plugin = "signing")

    configurations {
        create(Scopes.sugar)
        create(Scopes.sugarCalls)
        create(Scopes.coreLibDesugaring)
    }

    dependencies {
        add("implementation", kotlin("stdlib-jdk8"))

        add(Scopes.sugar, project(":sugar"))
        add(Scopes.sugarCalls, project(":test:sugar-calls"))
        add(Scopes.coreLibDesugaring, libraries.desugarJdkLibs)

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
        systemProperty("jar", configurations.getByName(Scopes.sugarCalls).asPath)
        systemProperty("dexout", project.buildDir)
    }

    tasks.register<de.undercouch.gradle.tasks.download.Download>(Tasks.download)  {
        tempAndMove(true)
        onlyIfModified(true)
        src("https://dl.google.com/android/repository/$sdkFile")
        dest("${rootProject.buildDir}/sdk-archives/$sdkFile")
    }

    tasks.register<Copy>(Tasks.unpack) {
        dependsOn(Tasks.download)
        from(zipTree("${rootProject.buildDir}/sdk-archives/$sdkFile"))
        into("$buildDir/sdk")
    }

    tasks.register<BuildSignatureTask>(Tasks.signatures) {
        dependsOn(Tasks.unpack)

        files(configurations.getByName(Scopes.sugar).asPath)
        files(sdk)

        outputName = Outputs.signatures
    }

    if (coreLibDesugaring) {
        tasks.register<BuildSignatureTask>(Tasks.signaturesCoreLib) {
            dependsOn(Tasks.unpack)

            files(configurations.getByName(Scopes.sugar).asPath)
            files(configurations.getByName(Scopes.coreLibDesugaring).asPath)
            files(sdk)
            exclude("java.lang.*8")

            outputName = Outputs.signaturesCoreLib
        }
    }

    afterEvaluate {
        configure<PublishingExtension> {
            publications {
                sign(create<MavenPublication>(Tasks.signatures) {
                    groupId = "${project.group}"
                    version = "${project.version}"
                    artifactId = "gummy-bears-api-$apiLevel"
                    artifact("${project.buildDir}/animalsniffer/${Tasks.signatures}/${Outputs.signatures}.sig") {
                        extension = "signature"
                        builtBy(tasks.named(Tasks.signatures))
                    }

                    standardPom()
                })

                if (coreLibDesugaring) {
                    sign(create<MavenPublication>(Tasks.signaturesCoreLib) {
                        groupId = "${project.group}"
                        version = "${project.version}"
                        artifactId = "gummy-bears-api-$apiLevel"
                        artifact("${project.buildDir}/animalsniffer/${Tasks.signaturesCoreLib}/${Outputs.signaturesCoreLib}.sig") {
                            extension = "signature"
                            classifier = "coreLib"
                            builtBy(tasks.named(Tasks.signaturesCoreLib))
                        }

                        standardPom()
                    })
                }
            }

            publishReleasesToRemote(project)
        }
    }
}
