load('//buildutils:aspectj.bzl', 'aspectj_library')

aspectj_library(
    name = "dom-aspects",
    srcs = glob([
        "src/main/java/**/*.java",
        "src/main/java/**/*.aj",
    ]),
    deps = [
        "//aspects/core-aspects",
        "//aspects/shared-aspects",
    ],
)
