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

import org.gradle.api.attributes.Attribute

object Attributes {
    val artifactType = Attribute.of("artifactType", String::class.java)
    val unpackedSdk = Attribute.of("unpackedSdk", Boolean::class.javaObjectType)
    val usage = Attribute.of("org.gradle.usage", String::class.java)
}

const val JAVA_RUNTIME = "java-runtime"