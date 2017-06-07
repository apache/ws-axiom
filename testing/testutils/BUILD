java_library(
    name = "testutils",
    srcs = glob(["src/main/java/**/*.java"]),
    deps = [
        "@commons_io//jar",
        "@guava//jar",
        "@junit//jar",
        "@osgi_core//jar",
        "@truth//jar",
    ],
)

java_test(
    name = "data_source_registry_test",
    srcs = [
        "src/test/java/org/apache/axiom/testutils/net/protocol/mem/DataSourceRegistryTest.java",
    ],
    deps = [
        ":testutils",
    ],
    test_class = "org.apache.axiom.testutils.net.protocol.mem.DataSourceRegistryTest",
)
