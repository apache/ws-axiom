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

package(default_visibility = ["//visibility:public"])

load("//buildutils:bnd.bzl", "bundle")

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
    name = "util-stax-debug",
    srcs = glob(["src/main/java/org/apache/axiom/util/stax/debug/*.java"]),
    deps = [
        "@commons_logging//jar",
        ":util-stax-wrapper",
    ],
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
        "src/main/java/org/apache/axiom/om/util/AXIOMUtil.java",
        "src/main/java/org/apache/axiom/om/util/DigestGenerator.java",
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

java_library(
    name = "locator",
    srcs = glob(["src/main/java/org/apache/axiom/locator/*.java"]),
    deps = [
        "@commons_logging//jar",
        "@osgi_core//jar",
        ":axiom-api",
        ":locator-loader",
    ],
)

java_library(
    name = "locator-loader",
    srcs = glob(["src/main/java/org/apache/axiom/locator/loader/*.java"]),
    deps = [
        ":axiom-api",
    ],
)

bundle(
    name = "bundle",
    symbolic_name = "org.apache.ws.commons.axiom.axiom-api",
    private_packages = [],
    export_packages = [
        "org.apache.axiom.*;-split-package:=merge-first;-noimport:=true",
    ],
    import_packages = [
        "org.jaxen.*;resolution:=optional",
        "org.codehaus.stax2;resolution:=optional",
        "org.apache.james.mime4j.*;version=\"${range;[==,=+)}\"",
        "*"
    ],
    activator = "org.apache.axiom.locator.Activator",
    activation_policy = "lazy",
    deps = [
        ":locator",
        ":util-sax",
    ],
)
