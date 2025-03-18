plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.toasttab.desugartestapp"
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
