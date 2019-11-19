plugins {
    java
}

configurations {
    create("sugar")
    create("generator")
}

dependencies {
    add("sugar", project(":sugar"))
    add("generator", project(":test:sugar-call-generator"))
}

tasks.register<JavaExec>("generateStubCalls") {
    classpath = configurations.getByName("generator").asFileTree
    main = "com.toasttab.android.StubCallGeneratorKt"
    args = listOf(configurations.getByName("sugar").asPath, "${project.buildDir}/generated-sources/java/main")
}

tasks.named<org.gradle.api.tasks.compile.JavaCompile>("compileJava") {
    dependsOn("generateStubCalls")
}

sourceSets.main {
    java.srcDir("${project.buildDir}/generated-sources/java/main")
}