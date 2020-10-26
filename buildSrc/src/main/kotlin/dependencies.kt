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

object versions {
    const val animalSniffer = "1.16"
    const val clikt = "3.0.1"
    const val desugarJdkLibs = "1.0.10"
    const val r8 = "1.5.68"
    const val kotlin = "1.4.10"
    const val javapoet = "1.11.1"
    const val javassist = "3.26.0-GA"
    const val junit = "4.12"
    const val truth = "1.0"
}

object libraries {
    val animalSniffer = "org.codehaus.mojo:animal-sniffer:${versions.animalSniffer}"
    val clikt = "com.github.ajalt.clikt:clikt:${versions.clikt}"
    val desugarJdkLibs = "com.android.tools:desugar_jdk_libs:${versions.desugarJdkLibs}"
    val r8 = "com.android.tools:r8:${versions.r8}"
    val javapoet = "com.squareup:javapoet:${versions.javapoet}"
    val javassist = "org.javassist:javassist:${versions.javassist}"
    val junit = "junit:junit:${versions.junit}"
    val truth = "com.google.truth:truth:${versions.truth}"
}
