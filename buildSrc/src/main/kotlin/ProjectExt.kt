import org.gradle.api.Project
import org.gradle.kotlin.dsl.the

fun Project.isRelease() = !version.toString().endsWith("-SNAPSHOT")

val Project.libs get() = the<org.gradle.accessors.dm.LibrariesForLibs>()