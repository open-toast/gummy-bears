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

package com.toasttab.animalsniffer

import org.codehaus.mojo.animal_sniffer.Clazz

sealed class ShouldTransform {
    object No: ShouldTransform()
    class Yes(val newName: String): ShouldTransform()
}

object DesugarSignatureTransformer {
    fun shouldTransform(name: String): ShouldTransform {
        if (name.endsWith("8")) {
            return ShouldTransform.Yes(name.removeSuffix("8"))
        } else if (name.substringAfterLast("/").startsWith("Desugar")) {
            return ShouldTransform.Yes(name.substringBeforeLast("/") + "/" + name.substringAfterLast("/Desugar"))
        } else {
            return ShouldTransform.No
        }
    }

    fun shouldTransform(clz: Clazz) = shouldTransform(clz.name)

    fun transform(clz: Clazz) =
        when (val shouldTransform = shouldTransform(clz)) {
            is ShouldTransform.No -> clz
            is ShouldTransform.Yes -> Clazz(
                shouldTransform.newName,
                clz.signatures,
                clz.superClass,
                clz.superInterfaces
            )
        }
}