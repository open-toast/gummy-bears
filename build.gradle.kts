buildscript {
    repositories {
        google()
        gradlePluginPortal()
        jcenter()
    }
}

plugins {
    kotlin("jvm") version versions.kotlin apply false
}

subprojects {
    group = "com.toasttab.android"
    version = "0.0.2-SNAPSHOT"

    repositories {
        google()
        jcenter()
    }
}