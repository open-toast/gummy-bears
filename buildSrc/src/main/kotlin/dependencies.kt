object versions {
    const val r8 = "1.5.68"
    const val kotlin = "1.3.60"
    const val javapoet = "1.11.1"
    const val javassist = "3.26.0-GA"
    const val junit = "4.12"
    const val truth = "1.0"
}

object libraries {
    val r8 = "com.android.tools:r8:${versions.r8}"
    val javapoet = "com.squareup:javapoet:${versions.javapoet}"
    val javassist = "org.javassist:javassist:${versions.javassist}"
    val junit = "junit:junit:${versions.junit}"
    val truth = "com.google.truth:truth:${versions.truth}"
}
