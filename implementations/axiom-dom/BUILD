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

load('//buildutils:aspectj.bzl', 'aj_library')

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

aj_library(
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
        "@woodstox//jar",
        ":factory",
    ],
)

java_test(
    name = "om-implementation-test",
    srcs = ["src/test/java/org/apache/axiom/om/impl/dom/OMImplementationTest.java"],
    test_class = "org.apache.axiom.om.impl.dom.OMImplementationTest",
    deps = [
        ":axiom-dom",
        "//testing/axiom-testsuite",
    ],
)

java_test(
    name = "soap-implementation-test",
    srcs = ["src/test/java/org/apache/axiom/soap/impl/dom/SOAPImplementationTest.java"],
    test_class = "org.apache.axiom.soap.impl.dom.SOAPImplementationTest",
    deps = [
        ":axiom-dom",
        "//testing/axiom-testsuite",
    ],
)

java_test(
    name = "dom-implementation-test",
    srcs = [
        "src/test/java/org/apache/axiom/om/impl/dom/DOMTests.java",
        "src/test/java/org/apache/axiom/om/impl/dom/DOMImplementationTest.java",
    ],
    test_class = "org.apache.axiom.om.impl.dom.DOMImplementationTest",
    deps = [
        ":axiom-dom",
        "//testing/dom-testsuite",
        # TODO: this shouldn't be here
        "//testing/dom-testsuite:common",
        "//testing/dom-testsuite:w3c",
    ],
)

[java_test(
    name = "w3c-dom-level%s-implementation-test" % level,
    srcs = [
        "src/test/java/org/apache/axiom/om/impl/dom/DOMTests.java",
        "src/test/java/org/apache/axiom/om/impl/dom/W3CDOMLevel%sImplementationTest.java" % level,
    ],
    test_class = "org.apache.axiom.om.impl.dom.W3CDOMLevel%sImplementationTest" % level,
    deps = [
        ":axiom-dom",
        "//testing/dom-testsuite:common",
        "//testing/dom-testsuite:w3c",
        "//testing/dom-testsuite:w3c-level%s" % level,
    ],
) for level in [1, 2, 3]]
