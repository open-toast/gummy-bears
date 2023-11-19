# Gummy Bears

[![CircleCI](https://circleci.com/gh/open-toast/gummy-bears.svg?style=svg)](https://circleci.com/gh/open-toast/gummy-bears)
[![Maven Central](https://img.shields.io/maven-central/v/com.toasttab.android/gummy-bears-api-24)](https://search.maven.org/artifact/com.toasttab.android/gummy-bears-api-24)

_D8 sugar for all of your animal sniffing needs_.

This project provides _Animal Sniffer_ signatures that account for desugared APIs for Android 4.4 and above (API level 19+). Read the [Background](#background) section below if none of this makes sense.

## Background

### Animal Sniffer

[Animal Sniffer](https://www.mojohaus.org/animal-sniffer/) checks your project's binary compatibility with a specific version of a java library. At Toast, we use Animal Sniffer to ensure that libraries consumed by our Android apps do not use any APIs unavailable on our oldest supported devices.

Animal Sniffer works by introspecting your project's bytecode and matching it against a set of field and method signatures. Conveniently, standard sets of signatures for all JDKs and Android SDKs are available on Maven Central. Standard signatures are generated by introspecting the classfiles (i.e. android.jar for Android SDK) and recording all public binary interfaces they expose.

### Android

The above, however, does not produce an accurate set of signatures for Android. Let's look at [Integer.hashCode(int)](https://developer.android.com/reference/java/lang/Integer#hashCode(int)). Per documentation, it was `Added in API level 24`. Yet it works perfectly fine on `API 19`. Moreover, when targeting Java 8+, the Kotlin compiler _will_ generate calls to this method to compute the hash code of `Int` fields in a `data class`.

When the APK is assembled, D8 (the Android Dexer) transforms java bytecode into Android (Dalvik/Art) bytecode. As part of that transformation, it rewrites (or _desugars_) some instructions and API calls. For example, lambdas are desuraged into anonymous classes, `try-with-resources` is desugared into a Dalvik-compatible set of instructions that matches the semantics of the _regular_ Java `try-with-resources`, and `Integer.hashCode(int)` is rewritten into a call to a synthetic class which is then added to the APK by D8.

[This article](https://jakewharton.com/d8-library-desugaring/) explains desugaring in detail.

### Gummy Bears

This project provides a safe and more accurate set of signatures for Android 4.4-13 (API 19-34). The additional _sugary_ signatures are generated from hand-written stubs. The reference for the stubs is the [D8 source code](https://r8.googlesource.com/r8/+/master/src/main/java/com/android/tools/r8/ir/desugar/BackportedMethodRewriter.java).

### Core library desugaring

This project also provides _experimental_ sets of signatures for APIs available via [core library desugaring](https://developer.android.com/studio/write/java8-support), including `java.time`, `ConcurrentHashMap`, etc.

Two versions of core library desugaring signatures are provided: v1, which requires `desugar_jdk_libs:1.2.3` or above and is published under the `coreLib`
classifier, and v2, which requires `desugar_jdk_libs:2.0.4` and is published under the `coreLib2` classifier. Note that `desugar_jdk_libs` version `2`
comes in three flavors: minimal, nio, and full. Currently, only the full flavor is supported.

Using signatures with core library desugaring to validate a library effectively implies that all Android projects consuming the library
must have core library desugaring enabled at build time and bring in the appropriate version of `desugar_jdk_libs`.

## How to use

Specify the latest version `com.toasttab.android:gummy-bears-api-${api}` as the set of signatures for Animal Sniffer.

### Gradle

```kotlin
plugins {
    id 'ru.vyarus.animalsniffer' version '1.6.0'
}

dependencies {
    signature('com.toasttab.android:gummy-bears-api-24:0.7.0@signature')
}
```

With core library desugaring:

```groovy
dependencies {
    signature('com.toasttab.android:gummy-bears-api-24:0.7.0:coreLib@signature')
}
```

With core library desugaring v2:

```groovy
dependencies {
    signature('com.toasttab.android:gummy-bears-api-24:0.7.0:coreLib2@signature')
}
```

### Gradle Kotlin DSL

```kotlin
plugins {
    id("ru.vyarus.animalsniffer") version "1.6.0"
}

dependencies {
    add("signature", "com.toasttab.android:gummy-bears-api-24:0.7.0@signature")
}
```

### Maven

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>animal-sniffer-maven-plugin</artifactId>
    <version>1.22</version>
    <configuration>
        <signature>
            <groupId>com.toasttab.android</groupId>
            <artifactId>gummy-bears-api-21</artifactId>
            <version>0.7.0</version>
        </signature>
    </configuration>
</plugin>
```

## Expediter

As of version 0.6.0, this project also publishes native [Expediter](https://github.com/open-toast/expediter) type descriptors. Expediter provides a superset
of the Animal Sniffer binary compatibility checks and comes with its own Gradle plugin.

## License

This project is licensed under the Apache 2 License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

:+1: to the authors of [Animal Sniffer](https://www.mojohaus.org/animal-sniffer/index.html), [gradle-animalsniffer-plugin](https://github.com/xvik/gradle-animalsniffer-plugin), [R8 and D8](https://r8.googlesource.com/r8).
