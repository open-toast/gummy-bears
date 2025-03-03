/*
 * Copyright (c) 2025. Toast Inc.
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

package com.toasttab.android

import com.toasttab.expediter.issue.Issue
import com.toasttab.expediter.types.MemberAccess
import com.toasttab.expediter.types.MemberSymbolicReference
import com.toasttab.expediter.types.MethodAccessType
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly

open class NegativeTest : BaseApiTest() {
    @Test
    fun `undesugared methods are detected by our tests`() {
        val issues = runExpediter(runD8(24))

        expectThat(issues).containsExactly(
            Issue.MissingMember(
                caller = "com/toasttab/android/stub/desugar_java_lang_DesugarClass",
                member = MemberAccess.MethodAccess(
                    targetType = "java/lang/Class",
                    ref = MemberSymbolicReference(
                        name = "isRecord",
                        signature = "()Z"
                    ),
                    accessType = MethodAccessType.VIRTUAL
                )
            )
        )
    }
}
