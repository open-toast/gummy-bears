repositories {
    jcenter()
    gradlePluginPortal()
}

plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("de.undercouch:gradle-download-task:4.0.2")
    implementation("ru.vyarus:gradle-animalsniffer-plugin:1.5.0")
}