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

package com.toasttab.android.signature.transform

sealed interface ShouldTransform {
    object No : ShouldTransform
    class Yes(val newName: String) : ShouldTransform
}

object DesugarClassNameTransformer {
    fun shouldTransform(name: String, delimiter: Char = '.'): ShouldTransform {
        return if (name.substringAfterLast(delimiter).startsWith("Desugar")) {
            ShouldTransform.Yes(name.substringBeforeLast(delimiter) + delimiter + name.substringAfterLast("${delimiter}Desugar"))
        } else {
            ShouldTransform.No
        }
    }

    fun transform(name: String) = when (val shouldTransform = shouldTransform(name)) {
        ShouldTransform.No -> name
        is ShouldTransform.Yes -> shouldTransform.newName
    }
}
