plugins {
    java
    `maven-publish`
    id("ru.vyarus.animalsniffer") version "1.5.0"
}

dependencies {
    implementation(project(":sugar"))
}

configure<ru.vyarus.gradle.plugin.animalsniffer.signature.AnimalSnifferSignatureExtension> {
    val api = project.name.substringAfter("api-")
    val sdk = project.file(System.getenv("ANDROID_HOME") + "/platforms/android-$api/android.jar")

    if (!sdk.exists()) {
        throw GradleException("$sdk does not exist")
    }

    files(configurations.compileClasspath)
    files(sdk)
}

afterEvaluate {
    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("signature") {
                groupId = "${project.group}"
                version = "${project.version}"
                artifactId = "gummy-bears-${project.name}"
                artifact("${project.buildDir}/animalsniffer/signature/${project.name}.sig") {
                    extension = "signature"
                    builtBy(tasks.named("animalsnifferSignature"))
                }
            }
        }
    }
}