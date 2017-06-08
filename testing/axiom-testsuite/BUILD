#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements. See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership. The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License. You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied. See the License for the
# specific language governing permissions and limitations
# under the License.
#

# TODO: not all rules should be visible
package(default_visibility = ["//visibility:public"])

genrule(
    name = "identity",
    srcs = ["src/main/schemas/identity.xsd"],
    outs = ["identity.srcjar"],
    tools = [
        "@local_jdk//:xjc",
        "@local_jdk//:jar",
    ],
    cmd = """
        mkdir $(@D)/_srcs;
        $(location @local_jdk//:xjc) -d $(@D)/_srcs $< > /dev/null;
        $(location @local_jdk//:jar) cf $@ -C $(@D)/_srcs . > /dev/null;
        rm -rf $(@D)/_srcs""",
)

java_binary(
    name = "scomp",
    runtime_deps = ["@xmlbeans//jar"],
    main_class = "org.apache.xmlbeans.impl.tool.SchemaCompiler",
)

genrule(
    name = "order",
    srcs = ["src/main/xsd/order.xsd"],
    outs = ["order.jar"],
    tools = [":scomp"],
    cmd = "$(location :scomp) -out $@ $< > /dev/null",
)

java_library(
    name = "axiom-testsuite",
    srcs = glob(["src/main/java/**/*.java"]) + [
        ":identity",
    ],
    resources = glob(["src/main/resources/**/*"]),
    deps = [
        "@commons_codec//jar",
        "@commons_io//jar",
        "@javamail//jar",
        "@jaxen//jar",
        "@junit//jar",
        "@mockito_core//jar",
        "@truth//jar",
        "@xalan_serializer//jar",
        "@xmlbeans//jar",
        ":order",
        "//axiom-api",
        "//axiom-api:blob",
        "//axiom-api:ext-stax",
        "//axiom-api:mime",
        "//axiom-api:stax",
        "//axiom-api:util",
        "//axiom-api:util-sax",
        "//axiom-api:util-stax",
        "//axiom-api:util-stax-debug",
        "//axiom-api:util-stax-wrapper",
        "//components/base64-utils",
        "//components/core-streams",
        "//components/xml-utils",
        "//testing/axiom-truth",
        "//testing/jaxen-testsuite",
        "//testing/jaxp-testsuite",
        "//testing/multiton",
        "//testing/soap-testsuite",
        "//testing/testutils",
        "//testing/xml-testsuite",
        "//testing/xml-truth",
    ],
)
