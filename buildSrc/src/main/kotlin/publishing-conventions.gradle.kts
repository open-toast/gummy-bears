import org.gradle.api.publish.maven.MavenPublication

import org.gradle.kotlin.dsl.create

plugins {
    id("maven-publish")
    id("signing")
}

group = rootProject.group
version = rootProject.version

publishing {
    publications {
        create<MavenPublication>("main") {
            groupId = "${project.group}"
            version = "${project.version}"
            artifactId = "gummy-bears-api-${project.name}"

            pom {
                name.set("Gummy Bears")
                description.set("Animalsniffer signatures for Android")
                url.set("https://github.com/open-toast/gummy-bears")
                scm {
                    url.set("https://github.com/open-toast/gummy-bears")
                }
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("Toast")
                        name.set("Toast Open Source")
                        email.set("opensource@toasttab.com")
                    }
                }
            }
        }
    }
}

if (isRelease() && Pgp.KEY != null) {
    signing {
        useInMemoryPgpKeys(Pgp.KEY, Pgp.PASSWORD)

        project.publishing.publications.withType<MavenPublication> {
            sign(this)
        }
    }
}
