package(default_visibility = ["//visibility:public"])

java_library(
    name = "ext-activation",
    srcs = glob(["src/main/java/org/apache/axiom/ext/activation/*.java"]),
)

java_library(
    name = "ext-io",
    srcs = glob(["src/main/java/org/apache/axiom/ext/io/*.java"]),
)

java_library(
    name = "ext-stax",
    srcs = glob(["src/main/java/org/apache/axiom/ext/stax/**/*.java"]),
)

java_library(
    name = "util",
    srcs = glob(["src/main/java/org/apache/axiom/util/*.java"]),
)

java_library(
    name = "util-activation",
    srcs = glob(["src/main/java/org/apache/axiom/util/activation/*.java"]),
    deps = [
        ":ext-activation",
    ],
)

java_library(
    name = "util-base64",
    srcs = glob(["src/main/java/org/apache/axiom/util/base64/*.java"]),
    deps = [
        ":util-activation",
        "//components/base64-utils",
    ],
)

java_library(
    name = "util-io",
    srcs = glob(["src/main/java/org/apache/axiom/util/io/*.java"]),
    deps = [
        ":ext-io",
    ],
)

java_library(
    name = "util-namespace",
    srcs = glob(["src/main/java/org/apache/axiom/util/namespace/*.java"]),
)

java_library(
    name = "util-sax",
    srcs = glob(["src/main/java/org/apache/axiom/util/sax/*.java"]),
)

java_library(
    name = "util-stax",
    srcs = glob(["src/main/java/org/apache/axiom/util/stax/*.java"]),
    deps = [
        "@commons_logging//jar",
        ":blob",
        ":ext-stax",
        ":util-activation",
        ":util-namespace",
        "//components/base64-utils",
    ],
)

java_library(
    name = "util-stax-wrapper",
    srcs = glob(["src/main/java/org/apache/axiom/util/stax/wrapper/*.java"]),
)

java_library(
    name = "blob",
    srcs = glob(["src/main/java/org/apache/axiom/blob/*.java"]),
    deps = [
        "@commons_logging//jar",
        ":ext-activation",
        ":ext-io",
        ":util-io",
    ],
)

java_library(
    name = "mime",
    srcs = glob(["src/main/java/org/apache/axiom/mime/*.java"]),
    deps = [
        "@commons_logging//jar",
        "@mime4j_core//jar",
        ":blob",
        ":ext-activation",
        ":ext-io",
        ":util",
        "//components/base64-utils",
    ],
)

java_library(
    name = "stax",
    srcs = glob(["src/main/java/org/apache/axiom/util/stax/dialect/*.java"]) + [
        "src/main/java/org/apache/axiom/om/util/StAXParserConfiguration.java",
        "src/main/java/org/apache/axiom/om/util/StAXUtils.java",
        "src/main/java/org/apache/axiom/om/util/StAXWriterConfiguration.java",
    ],
    deps = [
        "@commons_logging//jar",
        "@stax2_api//jar",
        ":ext-stax",
        "util-namespace",
        ":util-stax",
        ":util-stax-wrapper",
    ],
)

java_library(
    name = "axiom-api",
    srcs = glob([
        # TODO: disentangle attachments from the rest of axiom-api
        "src/main/java/org/apache/axiom/attachments/**/*.java",
        "src/main/java/org/apache/axiom/om/*.java",
        "src/main/java/org/apache/axiom/om/dom/*.java",
        "src/main/java/org/apache/axiom/om/ds/**/*.java",
        "src/main/java/org/apache/axiom/om/impl/*.java",
        # TODO: move this to a separate rule
        "src/main/java/org/apache/axiom/om/xpath/*.java",
        "src/main/java/org/apache/axiom/soap/*.java",
    ]) + [
        "src/main/java/org/apache/axiom/om/util/CommonUtils.java",
        "src/main/java/org/apache/axiom/om/util/LogOutputStream.java",
    ],
    deps = [
        "@commons_logging//jar",
        "@jaxen//jar",
        ":blob",
        ":ext-activation",
        ":ext-stax",
        ":mime",
        ":stax",
        ":util",
        ":util-namespace",
        ":util-stax",
    ],
    # TODO
    javacopts = ['-extra_checks:off'],
)
