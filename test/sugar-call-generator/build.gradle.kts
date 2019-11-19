plugins {
    java
    kotlin("jvm") version "1.3.60"
}

dependencies {
    implementation("com.squareup:javapoet:1.11.1")
    implementation("org.javassist:javassist:3.26.0-GA")
    implementation(kotlin("stdlib-jdk8"))
}