load('//buildutils:aspectj.bzl', 'aspectj_library')

aspectj_library(
    name = "core-aspects",
    srcs = glob([
        "src/main/java/**/*.java",
        "src/main/java/**/*.aj",
    ]),
    deps = [
        "//components/core-streams",
    ],
    visibility = ["//visibility:public"],
)
