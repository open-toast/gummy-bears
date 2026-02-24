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

import com.toasttab.android.signature.test.D8Result
import com.toasttab.android.signature.test.D8Runner
import com.toasttab.expediter.Expediter
import com.toasttab.expediter.ignore.Ignore
import com.toasttab.expediter.issue.Issue
import com.toasttab.expediter.parser.TypeParsers
import com.toasttab.expediter.provider.ClasspathApplicationTypesProvider
import com.toasttab.expediter.provider.InMemoryPlatformTypeProvider
import com.toasttab.expediter.roots.RootSelector
import com.toasttab.expediter.scanner.ClasspathScanner
import com.toasttab.expediter.types.ClassfileSource
import com.toasttab.expediter.types.ClassfileSourceType
import org.junit.jupiter.api.io.TempDir
import strikt.api.expectThat
import strikt.assertions.isEmpty
import java.nio.file.Path
import java.nio.file.Paths

open class BaseApiTest {
    @TempDir
    lateinit var output: Path

    /**
     * Run D8 and capture desugaring warnings
     */
    fun runD8(apiLevel: Int): D8Result {
        val sdk = Paths.get(System.getProperty("sdk"))
        val jar = Paths.get(System.getProperty("jar"))

        return D8Runner.run(apiLevel, sdk, jar, output)
    }

    /**
     * Validate the output of d8 against the Android SDK using Expediter.
     * This should return calls to APIs not present in the SDK that have not been desugared by d8.
     * Note that we are explicitly excluding Unsafe because it's not explicitly present in the Android SDKs.
     */
    fun runExpediter(result: D8Result): Set<Issue> =
        Expediter(
            ignore = Ignore.TargetStartsWith("sun/misc/Unsafe"),
            appTypes =
                ClasspathApplicationTypesProvider(
                    listOf(
                        ClassfileSource(result.output.toFile(), ClassfileSourceType.UNKNOWN),
                    ),
                ),
            platformTypeProvider =
                InMemoryPlatformTypeProvider(
                    ClasspathScanner(listOf(ClassfileSource(result.sdk.toFile(), ClassfileSourceType.UNKNOWN)))
                        .scan { stream, s -> TypeParsers.typeDescriptor(stream) },
                ),
            rootSelector = RootSelector.All,
        ).findIssues()

    fun testDesugaring(apiLevel: Int) {
        val result = runD8(apiLevel)

        expectThat(result.desugaringWarnings).isEmpty()
        expectThat(runExpediter(result)).isEmpty()
    }
}
