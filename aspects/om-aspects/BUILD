load('//buildutils:aspectj.bzl', 'aspectj_library')

aspectj_library(
    name = "om-aspects",
    srcs = glob([
        "src/main/java/**/*.java",
        "src/main/java/**/*.aj",
    ]),
    deps = [
        "//aspects/core-aspects",
        "//aspects/shared-aspects",
        "//axiom-api",
        "//axiom-api:util-base64",
        "//axiom-api:util-sax",
        "//components/base64-utils",
    ],
)
