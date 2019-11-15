plugins {
    java
    `maven-publish`
    id("ru.vyarus.animalsniffer") version "1.5.0"
    id("de.undercouch.download") version "4.0.1"
}

dependencies {
    implementation(project(":sugar"))
}

configure<ru.vyarus.gradle.plugin.animalsniffer.signature.AnimalSnifferSignatureExtension> {
    files(configurations.compileClasspath)
    files(project.file("$buildDir/sdk/android-4.4.2/android.jar"))
}

val sdkFile = "android-19_r04.zip"

tasks.register<de.undercouch.gradle.tasks.download.Download>("downloadSdk")  {
    tempAndMove(true)
    src("https://dl-ssl.google.com/Android/repository/$sdkFile")
    dest("$buildDir/$sdkFile")
}

tasks.register<Copy>("unpackSdk") {
    dependsOn("downloadSdk")
    from(zipTree("$buildDir/$sdkFile"))
    into("$buildDir/sdk")
}

afterEvaluate {
    tasks.named("animalsnifferSignature") {
        dependsOn("unpackSdk")
    }

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