load('//buildutils:aspectj.bzl', 'aspectj_library')

aspectj_library(
    name = "axiom-impl",
    srcs = glob([
        "src/main/java/**/*.java",
        "src/main/java/**/*.aj",
    ]),
    aspects = [
        "//aspects/core-aspects",
        "//aspects/om-aspects",
        "//aspects/shared-aspects",
    ],
    deps = [
        "//axiom-api:locator-loader",
    ],
)
