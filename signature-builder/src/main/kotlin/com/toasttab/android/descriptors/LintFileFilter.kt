/*
 * Copyright (c) 2026. Toast Inc.
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

package com.toasttab.android.descriptors

import protokt.v1.toasttab.expediter.v1.TypeDescriptor
import java.io.File

/**
 * Filters [TypeDescriptor] entries based on a desugared APIs lint file from the
 * `desugar_jdk_libs_configuration` artifact. The lint file lists exactly which APIs are made
 * available through Android core library desugaring for a given minSdk/compileSdk combination.
 *
 * Classes not listed in the lint file (e.g. `CompletableFuture`, `ForkJoinPool`) are internal
 * dependencies of the desugaring runtime and crash at runtime on older API levels — they must
 * be excluded from the signature.
 */
class LintFileFilter(
    lintFile: File,
) {
    /**
     * Classes listed without any member qualifiers (e.g. `java/util/Optional`). The entire
     * class is desugared, so all its methods and fields should be included.
     */
    private val classOnlyEntries: Set<String>

    /**
     * Classes that appear only with individual method entries (e.g. `java/lang/String#isBlank()Z`).
     * The values are the set of allowed method signatures in JVM descriptor format
     * (`name(params)returnType`). Only these methods should be included; all others must be
     * stripped.
     */
    private val methodEntries: Map<String, Set<String>>

    init {
        val classOnly = mutableSetOf<String>()
        val methods = mutableMapOf<String, MutableSet<String>>()
        val classesWithMembers = mutableSetOf<String>()

        for (line in lintFile.readLines()) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            val hashIndex = trimmed.indexOf('#')
            if (hashIndex < 0) {
                classOnly.add(trimmed)
            } else {
                val className = trimmed.substring(0, hashIndex)
                val memberSignature = trimmed.substring(hashIndex + 1)
                classesWithMembers.add(className)

                // Only track method signatures (contain parentheses).
                // Field entries lack type descriptors, so all fields are preserved during filtering.
                if (memberSignature.contains("(")) {
                    methods.getOrPut(className) { mutableSetOf() }.add(memberSignature)
                }
            }
        }

        // Classes with member entries but no class-only entry need method-level filtering.
        for (cls in classesWithMembers) {
            if (cls !in classOnly) {
                methods.getOrPut(cls) { mutableSetOf() }
            }
        }

        // Class-only entries take precedence — no filtering needed for those.
        classOnly.forEach { methods.remove(it) }

        classOnlyEntries = classOnly
        methodEntries = methods
    }

    /**
     * Filters a [TypeDescriptor] based on the lint file entries. Returns `null` if the class
     * should be excluded entirely.
     *
     * For class-only entries, the descriptor is returned unchanged. For method-entry classes,
     * methods not listed in the lint file are stripped (constructors and fields are preserved).
     */
    fun filter(type: TypeDescriptor): TypeDescriptor? {
        val topLevel = topLevelClass(type.name)

        if (topLevel in classOnlyEntries) {
            return type
        }

        val allowedMethods = methodEntries[topLevel] ?: return null

        // Inner classes of method-entry classes are kept as-is.
        if (type.name != topLevel) {
            return type
        }

        val filteredMethods =
            type.methods.filter { method ->
                val ref = method.ref
                val sig = ref.name + ref.signature
                sig in allowedMethods || ref.name == "<init>" || ref.name == "<clinit>"
            }

        return type.copy {
            methods = filteredMethods
        }
    }

    private fun topLevelClass(name: String): String {
        val dollarIndex = name.indexOf('$')
        return if (dollarIndex >= 0) name.substring(0, dollarIndex) else name
    }
}
