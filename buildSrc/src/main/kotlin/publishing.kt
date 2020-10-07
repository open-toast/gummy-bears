import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.plugins.signing.SigningExtension

import io.codearte.gradle.nexus.NexusStagingExtension

private object Pgp {
    val key by lazy {
        System.getenv("PGP_KEY")?.replace('$', '\n')
    }

    val password by lazy {
        System.getenv("PGP_PASSWORD")
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

fun Project.isRelease() = !version.toString().endsWith("-SNAPSHOT")

fun PublishingExtension.publishReleasesToRemote(project: Project) {
    if (project.isRelease()) {
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

fun Project.sign(publication: MavenPublication) {
    if (Pgp.key != null) {
        configure<SigningExtension> {
            useInMemoryPgpKeys(Pgp.key, Pgp.password)
            sign(publication)
        }
    }
}

fun Project.promoteStagingRepo() {
    if (project.isRelease()) {
        apply(plugin = "io.codearte.nexus-staging")

        configure<NexusStagingExtension> {
            username = Remote.username
            password = Remote.password
            packageGroup = "com.toasttab"
            numberOfRetries = 50
        }
    } else {
        tasks.register("closeAndReleaseRepository")
    }
}