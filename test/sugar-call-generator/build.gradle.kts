plugins {
    kotlin("jvm")
}

dependencies {
    implementation(libraries.javapoet)
    implementation(libraries.javassist)
    implementation(kotlin("stdlib-jdk8"))
}