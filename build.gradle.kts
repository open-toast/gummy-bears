buildscript {
    repositories {
        google()
        gradlePluginPortal()
        jcenter()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:3.5.2")
    }
}

subprojects {
    group = "com.toasttab.android"
    version = "0.0.2-SNAPSHOT"

    repositories {
        google()
        jcenter()
    }
}