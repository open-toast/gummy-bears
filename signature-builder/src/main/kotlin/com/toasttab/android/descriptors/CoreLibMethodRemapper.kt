/*
 * Copyright (c) 2026. Toast Inc.
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

import com.toasttab.expediter.parser.MethodSignature
import com.toasttab.expediter.parser.SignatureParser
import protokt.v1.toasttab.expediter.v1.AccessDeclaration
import protokt.v1.toasttab.expediter.v1.SymbolicReference
import protokt.v1.toasttab.expediter.v1.TypeDescriptor

/**
 * Remaps static methods in core library desugaring back to their original instance method
 * signatures. During desugaring, some instance methods are transformed into static methods
 * where the receiver type is prepended as the first parameter (e.g.
 * `BufferedReader.lines()` becomes `static lines(BufferedReader)`). This class reverses
 * that transformation.
 */
object CoreLibMethodRemapper {
    /**
     * Map from class name to the set of static method signatures that need to be remapped.
     * Each entry is in the format `methodName(staticDescriptor)returnType`.
     */
    private val remappedMethods: Map<String, Set<String>> =
        mapOf(
            "java/io/BufferedReader" to
                setOf(
                    "lines(Ljava/io/BufferedReader;)Ljava/util/stream/Stream;",
                ),
            "java/io/File" to
                setOf(
                    "toPath(Ljava/io/File;)Ljava/nio/file/Path;",
                ),
            "java/io/InputStream" to
                setOf(
                    "transferTo(Ljava/io/InputStream;Ljava/io/OutputStream;)J",
                ),
            "java/time/LocalTime" to
                setOf(
                    "toEpochSecond(Ljava/time/LocalTime;Ljava/time/LocalDate;Ljava/time/ZoneOffset;)J",
                ),
            "java/time/chrono/IsoChronology" to
                setOf(
                    "epochSecond(Ljava/time/chrono/IsoChronology;IIIIIILjava/time/ZoneOffset;)J",
                ),
            "java/time/LocalDate" to
                setOf(
                    "datesUntil(Ljava/time/LocalDate;Ljava/time/LocalDate;)Ljava/util/stream/Stream;",
                    "datesUntil(Ljava/time/LocalDate;Ljava/time/LocalDate;Ljava/time/Period;)Ljava/util/stream/Stream;",
                    "toEpochSecond(Ljava/time/LocalDate;Ljava/time/LocalTime;Ljava/time/ZoneOffset;)J",
                ),
            "java/time/Duration" to
                setOf(
                    "dividedBy(Ljava/time/Duration;Ljava/time/Duration;)J",
                    "toSeconds(Ljava/time/Duration;)J",
                    "toDaysPart(Ljava/time/Duration;)J",
                    "toHoursPart(Ljava/time/Duration;)I",
                    "toMinutesPart(Ljava/time/Duration;)I",
                    "toSecondsPart(Ljava/time/Duration;)I",
                    "toMillisPart(Ljava/time/Duration;)I",
                    "toNanosPart(Ljava/time/Duration;)I",
                    "truncatedTo(Ljava/time/Duration;Ljava/time/temporal/TemporalUnit;)Ljava/time/Duration;",
                ),
            "java/time/OffsetTime" to
                setOf(
                    "toEpochSecond(Ljava/time/OffsetTime;Ljava/time/LocalDate;)J",
                ),
            "java/util/TimeZone" to
                setOf(
                    "toZoneId(Ljava/util/TimeZone;)Ljava/time/ZoneId;",
                ),
            "java/util/LinkedHashSet" to
                setOf(
                    "spliterator(Ljava/util/LinkedHashSet;)Ljava/util/Spliterator;",
                ),
            "java/util/Date" to
                setOf(
                    "toInstant(Ljava/util/Date;)Ljava/time/Instant;",
                ),
            "java/util/GregorianCalendar" to
                setOf(
                    "toZonedDateTime(Ljava/util/GregorianCalendar;)Ljava/time/ZonedDateTime;",
                ),
            "java/util/Calendar" to
                setOf(
                    "toInstant(Ljava/util/Calendar;)Ljava/time/Instant;",
                ),
            "java/util/concurrent/atomic/AtomicLong" to
                setOf(
                    "getAndUpdate(Ljava/util/concurrent/atomic/AtomicLong;Ljava/util/function/LongUnaryOperator;)J",
                    "updateAndGet(Ljava/util/concurrent/atomic/AtomicLong;Ljava/util/function/LongUnaryOperator;)J",
                    "getAndAccumulate(Ljava/util/concurrent/atomic/AtomicLong;JLjava/util/function/LongBinaryOperator;)J",
                    "accumulateAndGet(Ljava/util/concurrent/atomic/AtomicLong;JLjava/util/function/LongBinaryOperator;)J",
                ),
            "java/util/concurrent/atomic/AtomicInteger" to
                setOf(
                    "getAndUpdate(Ljava/util/concurrent/atomic/AtomicInteger;Ljava/util/function/IntUnaryOperator;)I",
                    "updateAndGet(Ljava/util/concurrent/atomic/AtomicInteger;Ljava/util/function/IntUnaryOperator;)I",
                    "getAndAccumulate(Ljava/util/concurrent/atomic/AtomicInteger;ILjava/util/function/IntBinaryOperator;)I",
                    "accumulateAndGet(Ljava/util/concurrent/atomic/AtomicInteger;ILjava/util/function/IntBinaryOperator;)I",
                ),
            "java/util/concurrent/atomic/AtomicReference" to
                setOf(
                    "getAndUpdate(Ljava/util/concurrent/atomic/AtomicReference;Ljava/util/function/UnaryOperator;)Ljava/lang/Object;",
                    "updateAndGet(Ljava/util/concurrent/atomic/AtomicReference;Ljava/util/function/UnaryOperator;)Ljava/lang/Object;",
                    "getAndAccumulate(Ljava/util/concurrent/atomic/AtomicReference;Ljava/lang/Object;Ljava/util/function/BinaryOperator;)Ljava/lang/Object;",
                    "accumulateAndGet(Ljava/util/concurrent/atomic/AtomicReference;Ljava/lang/Object;Ljava/util/function/BinaryOperator;)Ljava/lang/Object;",
                ),
            "java/util/concurrent/TimeUnit" to
                setOf(
                    "toChronoUnit(Ljava/util/concurrent/TimeUnit;)Ljava/time/temporal/ChronoUnit;",
                    "convert(Ljava/util/concurrent/TimeUnit;Ljava/time/Duration;)J",
                ),
        )

    fun remap(type: TypeDescriptor): TypeDescriptor {
        val entries = remappedMethods[type.name] ?: return type
        val methods =
            type.methods.map { method ->
                val ref = method.requireRef
                val key = ref.name + ref.signature
                if (method.declaration == AccessDeclaration.STATIC && key in entries) {
                    method.copy {
                        this.ref =
                            SymbolicReference {
                                name = ref.name
                                signature = stripFirstParameter(ref.signature)
                            }
                        declaration = AccessDeclaration.INSTANCE
                    }
                } else {
                    method
                }
            }
        return type.copy { this.methods = methods }
    }

    private fun stripFirstParameter(signature: String): String {
        val parsed = SignatureParser.parseMethod(signature)
        return MethodSignature(parsed.returnType, parsed.argumentTypes.drop(1)).toDescriptor()
    }
}
