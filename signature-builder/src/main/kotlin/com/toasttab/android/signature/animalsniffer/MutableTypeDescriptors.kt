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

package com.toasttab.android.signature.animalsniffer

import protokt.v1.toasttab.expediter.v1.TypeDescriptor

class MutableTypeDescriptors(initial: Collection<TypeDescriptor>) {
    private val types = hashMapOf<String, TypeDescriptor>().apply {
        initial.associateByTo(this) { it.name }
    }

    fun add(type: TypeDescriptor) {
        types.merge(type.name, type) { oldType, newType ->
            val superClass = when (newType.superName) {
                null -> oldType.superName
                "java/lang/Object" -> oldType.superName
                oldType.superName -> oldType.superName
                else -> throw IllegalArgumentException("conflicting superclasses ${oldType.superName} != ${newType.superName} for ${type.name}")
            }

            if (oldType.flavor != newType.flavor) {
                throw IllegalArgumentException("conflicting flavor ${oldType.flavor} != ${newType.flavor} for ${type.name}")
            }

            if (oldType.protection != newType.protection) {
                throw IllegalArgumentException("conflicting protection ${oldType.flavor} != ${newType.flavor} for ${type.name}")
            }

            TypeDescriptor {
                name = type.name
                superName = superClass
                interfaces = (oldType.interfaces.toSet() + newType.interfaces).toList()

                fields = oldType.fields.merge(newType.fields)
                methods = oldType.methods.merge(newType.methods)

                flavor = oldType.flavor
                protection = oldType.protection
                extensibility = oldType.extensibility
            }
        }
    }

    fun classes(): Collection<TypeDescriptor> = types.values

    private fun <T> List<T>.merge(other: List<T>): List<T> {
        val set = LinkedHashSet(this)
        set.addAll(other)
        return set.toList()
    }
}