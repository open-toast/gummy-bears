import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class SignaturesTask : DefaultTask() {
    @Classpath
    lateinit var classpath: FileCollection

    @InputFiles
    @PathSensitive(PathSensitivity.ABSOLUTE)
    lateinit var sdk: FileCollection

    @InputFiles
    @PathSensitive(PathSensitivity.ABSOLUTE)
    lateinit var desugar: FileCollection

    @OutputFile
    lateinit var output: Any

    @OutputFile
    lateinit var expediterOutput: Any

    @Input
    lateinit var outputDescription: String

    @TaskAction
    fun exec() {
        project.javaexec {
            mainClass.set("com.toasttab.android.signature.animalsniffer.AndroidSignatureBuilderKt")
            classpath = this@SignaturesTask.classpath

            args = listOf(
                "--sdk",
                sdk.singleFile.path,
                "--output",
                project.file(output).path,
                "--expediter-output",
                project.file(expediterOutput).path,
                "--description",
                outputDescription
            ) + desugar.flatMap {
                listOf("--desugared", it.path)
            }
        }
    }
}