import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.plugins.signing.SigningExtension

private object Gpg {
    val key by lazy {
        System.getenv("GPG_KEY")
    }

    val password by lazy {
        System.getenv("GPG_PASSWORD")
    }
}

private object Remote {
    val username by lazy {
        System.getenv("OSSRH_USERNAME")
    }

    val password by lazy {
        System.getenv("OSSRH_PASSWORD")
    }

    val url = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
}

fun PublishingExtension.withRemote(version: Any) {
    if (!version.toString().endsWith("-SNAPSHOT")) {
        repositories {
            maven {
                name = "remote"
                setUrl(Remote.url)
                credentials {
                    username = Remote.username
                    password = Remote.password
                }
            }
        }
    }
}

fun MavenPublication.standardPom() {
    pom {
        scm {
            url.set("https://github.com/open-toast/gummy-bears")
        }
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
    }
}

fun Project.sign(publication: MavenPublication) {
    configure<SigningExtension> {
        useInMemoryPgpKeys(Gpg.key, Gpg.password)
        sign(publication)
    }
}