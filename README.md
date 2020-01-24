# Gummy Bears

_D8 sugar for all of your animal sniffing needs_.

This project provides _Animal Sniffer_ signatures for Android API levels 19 and above that account for additional APIs available via desugaring. Read the [Background](#background) section below if none of this makes sense.

## Background

### Animal Sniffer

[Animal Sniffer](https://www.mojohaus.org/animal-sniffer/) provides tools to assert your project's binary compatibility with a specific version of a library. At Toast, we use Animal Sniffer to ensure that libraries consumed by our Android apps do not use any APIs unavailable on our oldest supported devices.

Animal Sniffer works by introspecting your project's bytecode and matching it against a set of signatures. Conveniently, standard sets of signatures for all JDKs and Android SDKs are available on Maven Central. Standard signatures are generated by introspecting the classfiles (i.e. android.jar for Android SDK) and recording all public binary interfaces they expose.

### Android

Let's look at [Integer.hashCode(int)](https://developer.android.com/reference/java/lang/Integer#hashCode(int)). Per documentation, it was `Added in API level 24`. Yet it works perfectly fine on `API 19`. Moreover, when targeting `1.8` bytecode level, the Kotlin compiler will generate calls to this method to compute the hash code of `Int` fields in a `data class`.

When the APK is assembled, D8 (the Android Dexer) transforms java bytecode into Android (Dalvik/Art) bytecode. As part of that transformation, it rewrites (or _desugars_) some instructions and API calls. For example, lambdas are desuraged into anonymous classes, `try-with-resources` is desugared into a Dalvik-compatible set of instructions that matches the semantics of _regular_ Java's `try-with-resources`, and `Integer.hashCode(int)` is rewritten into a call to a synthetic class which is then added to the APK by D8.

### Gummy Bears

The set of desugared methods is defined by the version of D8, which itself is defined by the android gradle plugin, as well as the minimum SDK level.

This project provides a safe and more accurate set of signatures for Android 4.4-10 + Android Gradle 3.x. The additional _sugary_ signatures are generated from hand-written stubs. The reference point for the stubs is the [D8 source code](https://r8.googlesource.com/r8/+/master/src/main/java/com/android/tools/r8/ir/desugar/BackportedMethodRewriter.java). In the future, it will provide an expanded set of signatures for Android Gradle 4.x including `java.time`, `ConcurrentHashMap`, etc.

## How to use

Specify the latest version `com.toasttab.android:gummy-bears-api-${api}` as the set of signatures for Animal Sniffer.

Gradle:

```groovy
plugins {
    id 'ru.vyarus.animalsniffer' version '1.5.0'
}

dependencies {
    signature('com.toasttab.android:gummy-bears-api-24:0.0.1')
}
```

Gradle Kotlin DSL:

```kotlin
plugins {
    id("ru.vyarus.animalsniffer") version "1.5.0"
}

dependencies {
    add("signature", "com.toasttab.android:gummy-bears-api-24:0.0.1")
}
```

Maven:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>animal-sniffer-maven-plugin</artifactId>
    <version>1.16</version>
    <configuration>
        <signature>
            <groupId>com.toasttab</groupId>
            <artifactId>gummy-bears-api-21</artifactId>
            <version>0.0.1</version>
        </signature>
    </configuration>
</plugin>
```
## License

This project is licensed under the Apache 2 License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

:+1: to the authors of [Animal Sniffer](https://www.mojohaus.org/animal-sniffer/index.html), [gradle-animalsniffer-plugin](https://github.com/xvik/gradle-animalsniffer-plugin), [R8 and D8](https://r8.googlesource.com/r8).
