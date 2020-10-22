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

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javassist.ClassPool
import javassist.CtClass
import javassist.CtPrimitiveType
import javassist.bytecode.ClassFile
import javassist.bytecode.Descriptor
import javassist.bytecode.MethodInfo
import java.io.DataInputStream
import java.io.File
import java.util.jar.JarFile
import javax.lang.model.element.Modifier

class ApiUseGenerator : CliktCommand() {
    private val jar: String by option(help = "jar with APIs").required()
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
            return fqnToClassName(name)
        } else {
            return fqnToClassName(name)
        }
    }

    private fun fqnToClassName(fqn: String) = ClassName.get(fqn.substringBeforeLast("."), fqn.substringAfterLast("."))

    private fun generateMethodStubCaller(method: MethodInfo): MethodSpec {
        val paramTypes = Descriptor.getParameterTypes(method.descriptor, ClassPool.getDefault())
        val returnType = Descriptor.getReturnType(method.descriptor, ClassPool.getDefault())

        val params = paramTypes.indices.joinToString { "arg$it" };
        val instruction = if (returnType == CtClass.voidType) "" else "return "

        return MethodSpec.methodBuilder(method.name)
            .addModifiers(Modifier.PUBLIC)
            .returns(returnType.typeName())
            .apply {
                paramTypes.forEachIndexed { i, p ->
                    addParameter(p.typeName(), "arg$i")
                }
            }
            .addCode(
                "$instruction callee.${method.name}($params);\n"
            )
            .build()
    }

    private fun generateStubCallers(cls: ClassFile): JavaFile {
        val className = cls.name.replace(".", "_");
        val builder = TypeSpec.classBuilder(className)
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            .addField(
                FieldSpec.builder(
                    fqnToClassName(cls.name), "callee"
                ).build()
            )

        cls.methods.filter { it.isMethod }.forEach { method ->
            builder.addMethod(generateMethodStubCaller(method))
        }

        return JavaFile.builder("com.toasttab.android.stub", builder.build()).build()
    }

    private fun write(cls: ClassFile, output: File) {
        generateStubCallers(cls).writeTo(output)
    }


}

fun main(args: Array<String>) {
    val jarLocation = args[0]
    val output = File(args[1])

    listClasses(File(jarLocation)).forEach {
        write(it, output)
    }
}
