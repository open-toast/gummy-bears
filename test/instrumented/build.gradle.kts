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
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.toasttab.android.test"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        managedDevices {
            localDevices {
                create("api27") {
                    device = "Small Phone"
                    apiLevel = 27
                    systemImageSource = "aosp"
                }
            }
        }
    }
}

dependencies {
    implementation(project(":test:generated-callers:basic"))
    implementation(project(":test:generated-callers:unsafe"))
    androidTestImplementation(project(":test:generated-callers:basic"))
    androidTestImplementation(project(":test:generated-callers:unsafe"))
    androidTestImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.test)
}
