rootProject.name = "gummybears"

include(
    "sugar",
    ":api:19",
    ":api:21",
    "test:d8-common",
    "test:sugar-call-generator",
    "test:sugar-calls"
)