plugins {
    id("com.android.application")
}

android {
    compileSdkVersion(28)
    defaultConfig {
        applicationId = "com.toasttab.android"
        minSdkVersion(19)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"
    }

    lintOptions {
        isCheckReleaseBuilds = false
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(project(":test:sugar-calls"))
}