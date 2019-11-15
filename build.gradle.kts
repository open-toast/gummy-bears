import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import ru.vyarus.gradle.plugin.animalsniffer.signature.AnimalSnifferSignatureExtension

plugins {
    kotlin("jvm") version "1.3.60"
    id("ru.vyarus.animalsniffer") version "1.5.0"
}

group = "com.toasttab.android"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

configure<AnimalSnifferSignatureExtension> {
    files(sourceSets.main.get().output)
}
