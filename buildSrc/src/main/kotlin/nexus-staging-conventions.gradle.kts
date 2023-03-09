plugins {
    id("io.github.gradle-nexus.publish-plugin")
}

if (isRelease()) {
    nexusPublishing {
        repositories {
            sonatype {
                username.set(Remote.username)
                password.set(Remote.password)
            }
        }
    }
}
