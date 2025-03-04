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

buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("gradle.plugin.net.vivin:gradle-semantic-build-versioning:4.0.0")
    }
}

apply(plugin = "net.vivin.gradle-semantic-build-versioning")

rootProject.name = "gummybears"

include(
    "signature-builder",
    "signature-transformer",
    "sugar:basic",
    "sugar:unsafe",
    "sugar:unsafe24",
    "test:d8-runner",
    "test:api-treadmill",
    "test:bad-sugar",
    "test:bad-sugar-treadmill",
    "test:basic-sugar-treadmill",
    "test:base-api-tests"
)

(19..35).forEach {
    include("api:$it")
}