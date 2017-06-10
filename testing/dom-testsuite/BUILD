genrule(
    name = "org-w3c-domts",
    srcs = ["src/w3c/dom1-core-tests-20040405.jar"],
    outs = ["liborg-w3c-domts.jar"],
    cmd = """
        tmpdir=$$(mktemp -d)
        dir=$$(pwd)
        cd $$tmpdir
        jar xf $$dir/$<
        jar cf $$dir/$@ org/w3c/domts/*.class
        rm -rf $$tmpdir""",
)

java_library(
    name = "common",
    srcs = [
        "src/main/java/org/apache/axiom/ts/dom/DocumentBuilderFactoryFactory.java",
    ],
    visibility = ["//visibility:public"],
)

java_library(
    name = "dom-testsuite",
    srcs = glob([
        "src/main/java/org/apache/axiom/ts/dom/DOMTestCase.java",
        "src/main/java/org/apache/axiom/ts/dom/DOMTestSuiteBuilder.java",
        "src/main/java/org/apache/axiom/ts/dom/DOMUtils.java",
        "src/main/java/org/apache/axiom/ts/dom/attr/**/*.java",
        "src/main/java/org/apache/axiom/ts/dom/builder/**/*.java",
        "src/main/java/org/apache/axiom/ts/dom/document/**/*.java",
        "src/main/java/org/apache/axiom/ts/dom/documentfragment/**/*.java",
        "src/main/java/org/apache/axiom/ts/dom/documenttype/**/*.java",
        "src/main/java/org/apache/axiom/ts/dom/element/**/*.java",
        "src/main/java/org/apache/axiom/ts/dom/text/**/*.java",
    ]),
    resources = glob(["src/main/resources/**/*"]),
    visibility = ["//visibility:public"],
    deps = [
        "@truth//jar",
        ":common",
        "//testing/multiton",
        "//testing/testutils",
        "//testing/jaxp-testsuite",
        "//testing/xml-testsuite",
        "//testing/xml-truth",
        "//third_party:mockito",
        "//third_party:xerces",
    ],
)

java_library(
    name = "w3c",
    srcs = glob(["src/main/java/org/apache/axiom/ts/dom/w3c/*.java"]),
    visibility = ["//visibility:public"],
    deps = [
        "@asm//jar",
        ":common",
        ":org-w3c-domts",
        "//testing/testutils",
        "//third_party:junit",
    ],
)

[java_library(
    name = "w3c-level%s" % level,
    srcs = [
        "src/main/java/org/apache/axiom/ts/dom/w3c/level%s/W3CDOMLevel%sTestSuiteBuilder.java" % (level, level),
    ],
    visibility = ["//visibility:public"],
    deps = [
        "src/w3c/dom%s-core-tests-20040405.jar" % level,
        ":common",
        ":w3c",
    ],
) for level in [1, 2, 3]]

java_test(
    name = "xerces-test",
    srcs = [
        "src/test/java/org/apache/axiom/ts/dom/XercesTest.java",
    ],
    deps = [
        ":common",
        ":dom-testsuite",
        "//third_party:xerces",
    ],
    test_class = "org.apache.axiom.ts.dom.XercesTest",
)

[java_test(
    name = "w3c-level%s-xerces-test" % level,
    srcs = [
        "src/test/java/org/apache/axiom/ts/dom/w3c/level%s/XercesTest.java" % level,
    ],
    deps = [
        ":common",
        ":w3c",
        ":w3c-level%s" % level,
        "//third_party:xerces",
    ],
    test_class = "org.apache.axiom.ts.dom.w3c.level%s.XercesTest" % level,
) for level in [1, 2, 3]]

