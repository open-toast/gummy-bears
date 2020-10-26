# Signature Builder

This is a special-purpose wrapper around Animal Sniffer that generates Android signatures from the standard SDK jar
and auxiliary jars with desugared APIs such as Google's desugar_jdk_libs.

The wrapper supports name retargeting in the auxiliary jars (e.g. `DesugarDate` to `Date` and `Long8` to `Long`) and
is less strict compared to vanilla Animal Sniffer when merging signatures (e.g. does not care that `Long8` extends
`Object`, not `Number`.)