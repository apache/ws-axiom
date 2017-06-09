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

load('//buildutils:aspectj.bzl', 'aspectj_library')

java_library(
    name = "factory",
    srcs = glob(["src/main/java/org/apache/axiom/om/impl/dom/factory/*.java"]),
    resources = glob(["src/main/resources/**/*"]),
    deps = [
        "//aspects/dom-aspects",
        "//aspects/om-aspects",
        "//axiom-api:locator-loader",
        "//axiom-api:stax",
    ],
)

aspectj_library(
    name = "axiom-dom",
    srcs = glob([
        "src/main/java/**/*.java",
        "src/main/java/**/*.aj",
    ]),
    aspects = [
        "//aspects/core-aspects",
        "//aspects/dom-aspects",
        "//aspects/om-aspects",
        "//aspects/shared-aspects",
    ],
    deps = [
        ":factory",
        "//axiom-api:locator-loader",
    ],
)
java_test(
    name = "om_implementation_test",
    srcs = ["src/test/java/org/apache/axiom/om/impl/dom/OMImplementationTest.java"],
    test_class = "org.apache.axiom.om.impl.dom.OMImplementationTest",
    deps = [
        ":axiom-dom",
        "//testing/axiom-testsuite",
    ],
)

java_test(
    name = "soap_implementation_test",
    srcs = ["src/test/java/org/apache/axiom/soap/impl/dom/SOAPImplementationTest.java"],
    test_class = "org.apache.axiom.soap.impl.dom.SOAPImplementationTest",
    deps = [
        ":axiom-dom",
        "//testing/axiom-testsuite",
    ],
)
