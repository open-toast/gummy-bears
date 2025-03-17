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

object Configurations {
    const val GENERATOR = "_generator_"
    const val ANDROID_SDK = "_android_sdk_"
    const val STANDARD_DESUGARED = "_standard_desugared_signatures_"
    const val GENERATED_CALLERS = "_generated_callers_"
    const val CORE_LIB = "_core_lib_"
    const val CORE_LIB_2 = "_core_lib_2_"
}

object Publications {
    const val MAIN = "main"
}

object Pgp {
    val KEY by lazy {
        System.getenv("PGP_KEY")?.replace('$', '\n')
    }

    val PASSWORD by lazy {
        System.getenv("PGP_PASSWORD")
    }
}

object Remote {
    val USERNAME by lazy {
        System.getenv("OSSRH_USERNAME")
    }

    val PASSWORD by lazy {
        System.getenv("OSSRH_PASSWORD")
    }

    val url = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
}

object Tasks {
    const val signatures = "buildSignatures"
}

object Outputs {
    const val signatures = "signatures.sig"
    const val expediter = "platform.expediter"
}
