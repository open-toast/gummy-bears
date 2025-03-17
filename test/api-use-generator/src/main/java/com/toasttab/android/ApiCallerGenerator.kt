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

package com.toasttab.android

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.palantir.javapoet.ArrayTypeName
import com.palantir.javapoet.ClassName
import com.palantir.javapoet.FieldSpec
import com.palantir.javapoet.JavaFile
import com.palantir.javapoet.MethodSpec
import com.palantir.javapoet.TypeName
import com.palantir.javapoet.TypeSpec
import com.toasttab.android.signature.transform.DesugarClassNameTransformer
import javassist.ClassPool
import javassist.CtClass
import javassist.CtPrimitiveType
import javassist.bytecode.ClassFile
import javassist.bytecode.Descriptor
import javassist.bytecode.MethodInfo
import java.io.DataInputStream
import java.io.File
import java.lang.Exception
import java.util.jar.JarFile
import javax.lang.model.element.Modifier

class ApiCallerGenerator : CliktCommand() {
    private val jar: List<String> by option(help = "jar with APIs").multiple()
    private val output: String by option(help = "output directory for generated classes").required()

    private fun listClasses(jar: File): Sequence<ClassFile> {
        val jf = JarFile(jar)
        return jf.entries().asSequence().filter { it.name.endsWith(".class") }.map {
            jf.getInputStream(it).use { s ->
                ClassFile(DataInputStream(s))
            }
        }
    }

    private fun CtClass.typeName(): TypeName {
        if (this is CtPrimitiveType) {
            return fqnToClassName(wrapperName).unbox()
        } else if (this.isArray) {
            return ArrayTypeName.of(this.componentType.typeName())
        } else {
            return fqnToClassName(name)
        }
    }

    private fun fqnToClassName(fqn: String) = ClassName.get(fqn.substringBeforeLast("."), fqn.substringAfterLast(".").replace('$', '.'))

    private fun generateMethodStubCaller(method: MethodInfo, calleeType: TypeName): MethodSpec {
        val paramTypes = Descriptor.getParameterTypes(method.descriptor, ClassPool.getDefault())
        val returnType = Descriptor.getReturnType(method.descriptor, ClassPool.getDefault())

        val params = paramTypes.indices.joinToString { "arg$it" }
        val instruction = if (returnType == CtClass.voidType) "" else "return "

        return MethodSpec.methodBuilder(method.name)
            .addModifiers(Modifier.PUBLIC)
            .addException(Exception::class.java)
            .returns(returnType.typeName())
            .apply {
                paramTypes.forEachIndexed { i, p ->
                    addParameter(p.typeName(), "arg$i")
                }

                if (javassist.Modifier.isStatic(method.accessFlags)) {
                    addCode(
                        "$instruction \$T.${method.name}($params);",
                        calleeType
                    )
                } else {
                    addCode(
                        "$instruction callee.${method.name}($params);"
                    )
                }
            }
            .build()
    }

    private fun generateStubCallers(name: String, classes: Collection<ClassFile>): JavaFile {
        val className = name.replace(".", "_")

        val calleeType = fqnToClassName(DesugarClassNameTransformer.transform(name))

        val builder = TypeSpec.classBuilder(className).addModifiers(Modifier.PUBLIC, Modifier.FINAL)

        val methods = classes.flatMap { it.methods.filter { it.isMethod } }

        for (method in methods) {
            builder.addMethod(generateMethodStubCaller(method, calleeType))
        }

        if (methods.any { !javassist.Modifier.isStatic(it.accessFlags) }) {
            builder.addField(
                FieldSpec.builder(calleeType, "callee").addModifiers(Modifier.FINAL, Modifier.PRIVATE).build()
            ).addMethod(
                MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
                    .addParameter(calleeType, "callee")
                    .addCode("this.callee = callee;")
                    .build()
            )
        }

        return JavaFile.builder("com.toasttab.android.stub", builder.build()).build()
    }

    private fun write(name: String, classes: Collection<ClassFile>, output: File) {
        generateStubCallers(name, classes).writeTo(output)
    }

    override fun run() {
        jar.flatMap { listClasses(File(it)) }.groupBy { it.name }.forEach { (name, classes) ->
            write(name, classes, File(output))
        }
    }
}

fun main(args: Array<String>) {
    ApiCallerGenerator().main(args)
}
