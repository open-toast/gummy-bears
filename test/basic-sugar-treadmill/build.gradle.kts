/*
 * Copyright (c) 2020. Toast Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    `java-library`
}

configurations {
    create(Scopes.standardSugar)
    create(Scopes.generator)
}

dependencies {
    add(Scopes.standardSugar, project(":basic-sugar"))
    add(Scopes.generator, project(":test:api-treadmill"))
}

tasks.register<JavaExec>("generateClasses") {
    classpath = configurations.getByName(Scopes.generator).asFileTree
    mainClass.set("com.toasttab.android.ApiUseGeneratorKt")
    args = listOf(
        "--jar",
        configurations.getByName(Scopes.standardSugar).asPath,
        "--output",
        "${project.buildDir}/generated-sources/java/main"
    )
}

tasks.named<JavaCompile>("compileJava") {
    dependsOn("generateClasses")
}

sourceSets.main {
    java.srcDir("${project.buildDir}/generated-sources/java/main")
}