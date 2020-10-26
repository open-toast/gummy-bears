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
import org.gradle.api.tasks.JavaExec
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

private object Tasks {
    const val signatures = "buildSignatures"
    const val signaturesCoreLib = "buildSignaturesCoreLib"
}

private object Outputs {
    const val signatures = "signatures.sig"
    const val signaturesCoreLib = "signaturesCoreLib.sig"
}

fun Project.buildSignatures(
    apiLevel: String,
    sdk: String,
    coreLibDesugaring: Boolean = false
) {
    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    configurations {
        create(Scopes.sdk)
        create(Scopes.generator)
        create(Scopes.standardSugar)
        create(Scopes.exerciseStandardSugar)
        create(Scopes.coreLibSugar).isTransitive = false
    }

    dependencies {
        extractSdk()

        add("testImplementation", kotlin("stdlib-jdk8"))

        add(Scopes.generator, project(":signature-builder"))

        add(Scopes.sdk, "$SDK_GROUP:$sdk@zip")
        add(Scopes.standardSugar, project(":basic-sugar"))
        add(Scopes.exerciseStandardSugar, project(":test:basic-sugar-treadmill"))
        add(Scopes.coreLibSugar, libraries.desugarJdkLibs)

        add("testImplementation", project(":test:d8-runner"))
        add("testImplementation", libraries.junit)
        add("testImplementation", libraries.truth)
    }

    tasks.withType<Test> {
        dependsOn(":basic-sugar:build")
        dependsOn(":test:basic-sugar-treadmill:build")

        systemProperty("sdk", configurations.getByName(Scopes.sdk).asPath)
        systemProperty("jar", configurations.getByName(Scopes.exerciseStandardSugar).asPath)
        systemProperty("dexout", project.buildDir)
    }

    tasks.register<JavaExec>(Tasks.signatures) {
        classpath = configurations.getByName("generator").asFileTree
        main = "com.toasttab.animalsniffer.AndroidSignatureBuilderKt"
        args = listOf(
            "--sdk",
            configurations.getByName(Scopes.sdk).asPath,
            "--desugared",
            configurations.getByName(Scopes.standardSugar).asPath,
            "--output",
            "$buildDir/${Outputs.signatures}"
        )
    }

    if (coreLibDesugaring) {
        tasks.register<JavaExec>(Tasks.signaturesCoreLib) {
            classpath = configurations.getByName("generator").asFileTree
            main = "com.toasttab.animalsniffer.AndroidSignatureBuilderKt"
            args = listOf(
                "--sdk",
                configurations.getByName(Scopes.sdk).asPath,
                "--desugared",
                configurations.getByName(Scopes.coreLibSugar).asPath,
                "--desugared",
                configurations.getByName(Scopes.standardSugar).asPath,
                "--output",
                "$buildDir/${Outputs.signaturesCoreLib}"
            )
        }
    }

    afterEvaluate {
        configure<PublishingExtension> {
            publications {
                sign(create<MavenPublication>(Tasks.signatures) {
                    groupId = "${project.group}"
                    version = "${project.version}"
                    artifactId = "gummy-bears-api-$apiLevel"
                    artifact("$buildDir/${Outputs.signatures}") {
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
                        artifact("$buildDir/${Outputs.signaturesCoreLib}") {
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
