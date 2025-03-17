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
    `generate-signatures-conventions`
}

sdk("platform-24:r02")

dependencies {
    add(Configurations.STANDARD_DESUGARED, project(":test:invalid-desugared-signatures"))
    add(Configurations.GENERATED_CALLERS, project(":test:generated-callers:invalid"))

    implementation(platform(libs.junit.bom))
    implementation(libs.junit.jupiter.api)
    implementation(project(":test:d8-runner"))
    implementation(libs.strikt.core)
    implementation(libs.expediter.core)
}
