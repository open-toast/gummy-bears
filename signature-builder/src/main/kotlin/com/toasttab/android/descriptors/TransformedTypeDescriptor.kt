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

package com.toasttab.android.descriptors

import com.toasttab.android.signature.transform.DesugarClassNameTransformer
import protokt.v1.toasttab.expediter.v1.TypeDescriptor

class TransformedTypeDescriptor private constructor(
    private val type: TypeDescriptor,
    private val newName: String
) {
    constructor(type: TypeDescriptor) : this(type, transform(type.name))

    val priority = if (type.name == newName) {
        0
    } else {
        1
    }

    fun toType() = if (type.name == newName) {
        type
    } else {
        type.copy {
            name = newName
            superName = superName?.let(::transform)
            interfaces = type.interfaces.map(::transform)
        }
    }

    companion object {
        private fun transform(name: String) = DesugarClassNameTransformer.transform(name, '/')
    }
}
