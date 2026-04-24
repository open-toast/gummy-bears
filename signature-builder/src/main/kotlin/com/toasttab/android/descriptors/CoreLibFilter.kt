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

import protokt.v1.toasttab.expediter.v1.AccessProtection
import protokt.v1.toasttab.expediter.v1.TypeDescriptor
import java.io.File

/**
 * Filters and transforms core library desugaring types. Applies the following steps in order:
 *
 * 1. Excludes classes outside `java/` and `javax/` packages.
 * 2. Excludes non-public/protected classes and strips non-public/protected members.
 * 3. Remaps static companion methods back to instance methods.
 * 4. Applies lint file filtering (if a lint file was provided).
 *
 * Returns `null` if the type should be excluded entirely.
 */
class CoreLibFilter(lintFile: File?) {
    private val lintFileFilter = lintFile?.let { LintFileFilter(it) }

    /**
     * Methods present in the core library desugaring jar that do not exist
     * on JVM 17 and must be excluded from the signature.
     */
    private val excludedMethods: Map<String, Set<String>> = mapOf(
        "java/util/concurrent/ThreadLocalRandom" to setOf("nextGaussian()D"),
        "java/time/chrono/IsoChronology" to setOf("<init>()V"),
        "java/time/Duration" to setOf("<init>()V"),
    )

    fun filter(type: TypeDescriptor): TypeDescriptor? {
        if (!type.name.startsWith("java/") && !type.name.startsWith("javax/")) {
            return null
        }

        if (!type.protection.isPublicOrProtected()) {
            return null
        }

        val publicMembers = type.copy {
            methods = type.methods.filter { it.protection.isPublicOrProtected() }
            fields = type.fields.filter { it.protection.isPublicOrProtected() }
        }

        val remapped = CoreLibMethodRemapper.remap(publicMembers)

        val excluded = excludedMethods[remapped.name]
        val cleaned = if (excluded != null) {
            remapped.copy {
                methods = remapped.methods.filter { m ->
                    val ref = m.requireRef
                    ref.name + ref.signature !in excluded
                }
            }
        } else {
            remapped
        }

        return if (lintFileFilter != null) lintFileFilter.filter(cleaned) else cleaned
    }
}

private fun AccessProtection.isPublicOrProtected() =
    this == AccessProtection.PUBLIC || this == AccessProtection.PROTECTED
