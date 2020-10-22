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
import java.lang.IllegalArgumentException

class MutableSignatures {
    private val classes: MutableMap<String, Clazz> = hashMapOf()

    constructor(initial: Sequence<Clazz>) {
        initial.associateByTo(classes) {
            it.name
        }
    }

    fun add(clz: Clazz) {
        classes.merge(clz.name, clz) { oldValue, newValue ->
            val superClass = when (newValue.superClass) {
                "java/lang/Object" -> oldValue.superClass
                oldValue.superClass -> oldValue.superClass
                else -> throw IllegalArgumentException("conflicting superclasses ${oldValue.superClass} and ${newValue.superClass} for ${clz.name}")
            }

            Clazz(
                clz.name, oldValue.signatures + newValue.signatures, superClass, (
                        (oldValue.superInterfaces.toSet() + newValue.superInterfaces).toTypedArray()
                        )
            )
        }
    }

    fun classes(): Sequence<Clazz> = classes.values.asSequence()
}