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

package com.toasttab.android.descriptors.sniffer

import org.codehaus.mojo.animal_sniffer.Clazz
import protokt.v1.toasttab.expediter.v1.MemberDescriptor
import protokt.v1.toasttab.expediter.v1.TypeDescriptor

object AnimalSnifferConverter {
    fun convert(type: TypeDescriptor) =
        Clazz(
            type.name,
            type.signatures(),
            type.superName,
            type.interfaces.toTypedArray(),
        )

    private fun fieldSignature(descriptor: MemberDescriptor) = "${descriptor.requireRef.name}#${descriptor.requireRef.signature}"

    private fun methodSignature(descriptor: MemberDescriptor) = "${descriptor.requireRef.name}${descriptor.requireRef.signature}"

    private fun TypeDescriptor.fieldSignatures() = fields.map(AnimalSnifferConverter::fieldSignature)

    private fun TypeDescriptor.methodSignatures() = methods.map(AnimalSnifferConverter::methodSignature)

    private fun TypeDescriptor.signatures() = (fieldSignatures() + methodSignatures()).toHashSet()
}
