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

load("//buildutils:aspectj.bzl", "aj_library")
load("//buildutils:bnd.bzl", "bundle")
load("//buildutils:test.bzl", "test")

java_library(
    name = "factory",
    srcs = glob(["src/main/java/org/apache/axiom/om/impl/llom/factory/*.java"]),
    resources = glob(["src/main/resources/**/*"]),
    deps = [
        "//aspects/om-aspects",
        "//axiom-api:locator-loader",
    ],
)

aj_library(
    name = "axiom-impl",
    srcs = glob([
        "src/main/java/org/apache/axiom/om/impl/llom/*.java",
        "src/main/java/org/apache/axiom/soap/impl/llom/**/*.java",
    ]),
    aspects = [
        "//aspects/core-aspects",
        "//aspects/om-aspects",
        "//aspects/shared-aspects",
    ],
    deps = [
        ":factory",
        "@woodstox//jar",
    ],
)

bundle(
    name = "bundle",
    symbolic_name = "org.apache.ws.commons.axiom.axiom-impl",
    private_packages = [
        "org.apache.axiom.core.*",
        "!org.apache.axiom.om.impl",
        "org.apache.axiom.om.impl.llom.*;-split-package:=merge-first",
        "org.apache.axiom.om.impl.*",
        "org.apache.axiom.soap.impl.*;-split-package:=merge-first",
        "org.apache.axiom.shared.*",
        "org.apache.axiom.util.xml.*",
        # TODO: remove this; see r1796490
        "org.aspectj.lang",
        "org.aspectj.lang.annotation",
    ],
    import_packages = [
        "!org.aspectj.*",
        "*",
    ],
    activation_policy = "lazy",
    visibility = ["//visibility:public"],
    deps = [
        "axiom-impl",
    ],
    resources = ["src/main/resources/META-INF/axiom.xml"],
)

test(
    name = "om-implementation-test",
    srcs = ["src/test/java/org/apache/axiom/om/impl/llom/OMImplementationTest.java"],
    test_class = "org.apache.axiom.om.impl.llom.OMImplementationTest",
    deps = [
        ":axiom-impl",
        "//testing/axiom-testsuite",
    ],
)

test(
    name = "soap-implementation-test",
    srcs = ["src/test/java/org/apache/axiom/soap/impl/llom/SOAPImplementationTest.java"],
    test_class = "org.apache.axiom.soap.impl.llom.SOAPImplementationTest",
    deps = [
        ":axiom-impl",
        "//testing/axiom-testsuite",
    ],
)

