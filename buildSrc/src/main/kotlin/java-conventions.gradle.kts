repositories {
    mavenCentral()
    mavenLocal()
    google()
}

plugins {
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    jar {
        isPreserveFileTimestamps = false
    }
}
