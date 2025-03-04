import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    id("java-conventions")
    kotlin("jvm")
    id("com.diffplug.spotless")
}

spotless {
    kotlin {
        ktlint()
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
        languageVersion = KotlinVersion.KOTLIN_2_0
    }
}
