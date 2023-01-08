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
    const val GENERATOR = "generator"
    const val SDK = "sdk"
    const val STANDARD_SUGAR = "sugar"
    const val EXERCISE_STANDARD_SUGAR = "exerciseStandardSugar"
    const val CORE_LIB_SUGAR = "coreLibSugar"
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
