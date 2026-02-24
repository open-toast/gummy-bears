/*
 * Copyright (c) 2023. Toast Inc.
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

class MutableTypeDescriptors(
    initial: Collection<TypeDescriptor>,
) {
    private val types =
        hashMapOf<String, TypeDescriptor>().apply {
            initial.associateByTo(this) { it.name }
        }

    fun add(type: TypeDescriptor) {
        types.merge(type.name, type) { oldType, newType ->
            TypeDescriptor {
                name = type.name
                superName = oldType.superName
                interfaces = oldType.interfaces.merge(newType.interfaces)

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
