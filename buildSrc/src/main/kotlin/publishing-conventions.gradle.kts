import gradle.kotlin.dsl.accessors._5d7aa1b90eb4f2d994f899ac68c12425.publishing
import org.gradle.api.publish.maven.MavenPublication

import org.gradle.kotlin.dsl.create

plugins {
    id("maven-publish")
    id("signing")
}

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

    if (isRelease()) {
        repositories {
            maven {
                name = "remote"
                setUrl(Remote.url)
                credentials {
                    username = Remote.USERNAME
                    password = Remote.PASSWORD
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
