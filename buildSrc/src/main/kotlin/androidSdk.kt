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
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.transform.InputArtifact
import org.gradle.api.artifacts.transform.TransformAction
import org.gradle.api.artifacts.transform.TransformOutputs
import org.gradle.api.artifacts.transform.TransformParameters
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.registerTransform
import java.util.zip.ZipFile

const val SDK_GROUP = "com.google.android.sdk"

private const val ANDROID_JAR = "android.jar"

/**
 * This [artifact transform](https://docs.gradle.org/current/userguide/artifact_transforms.html)
 * extracts android.jar from the Android SDK Platform zip archive.
 */
abstract class ExtractSdkTransform : TransformAction<TransformParameters.None> {
    @get:InputArtifact
    abstract val inputArtifact: Provider<FileSystemLocation>

    override fun transform(outputs: TransformOutputs) {
        val sdkArchive = ZipFile(inputArtifact.get().asFile)

        val androidJarEntry = sdkArchive.entries().asSequence().find { it.name.endsWith(ANDROID_JAR) }

        sdkArchive.getInputStream(androidJarEntry).use { i ->
            outputs.file(ANDROID_JAR).outputStream().use { o ->
                i.copyTo(o)
            }
        }
    }
}

fun Project.extractSdk() {
    configurations.named("sdk") {
        attributes.attribute(Attributes.unpackedSdk, true)
    }

    dependencies {
        attributesSchema {
            attribute(Attributes.unpackedSdk)
        }

        artifactTypes.create("zip") {
            attributes.attribute(Attributes.unpackedSdk, false)
        }

        registerTransform(ExtractSdkTransform::class) {
            from.attribute(Attributes.unpackedSdk, false).attribute(Attributes.artifactType, "zip")
            to.attribute(Attributes.unpackedSdk, true).attribute(Attributes.artifactType, "jar")
        }
    }
}


/**
 * Enables Gradle to download artifacts from the Android SDK (non-Maven) repository.
 */
fun RepositoryHandler.androidSdk() {
    ivy {
        setUrl("https://dl.google.com/android/repository")

        content {
            includeGroup(SDK_GROUP)
        }

        patternLayout {
            artifact("/[module]_[revision].[ext]")
        }

        metadataSources {
            artifact()
        }
    }
}
