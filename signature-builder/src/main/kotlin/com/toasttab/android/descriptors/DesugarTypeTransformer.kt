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
import com.toasttab.android.signature.transform.ShouldTransform
import protokt.v1.toasttab.expediter.v1.TypeDescriptor

object DesugarTypeTransformer {
    private fun shouldTransform(type: TypeDescriptor) = DesugarClassNameTransformer.shouldTransform(type.name, '/')

    fun transform(type: TypeDescriptor) =
        when (val shouldTransform = shouldTransform(type)) {
            is ShouldTransform.No -> type
            is ShouldTransform.Yes -> type.copy {
                name = shouldTransform.newName
            }
        }
}
