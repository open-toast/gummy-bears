rootProject.name = "gummybears"

include(
    "sugar",
    "test:d8-common",
    "test:sugar-call-generator",
    "test:sugar-calls"
)

(19..29).forEach {
    include("api:$it")
}