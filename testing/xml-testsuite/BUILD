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

genrule(
    name = "filelist",
    srcs = glob(["src/main/resources/org/apache/axiom/ts/xml/bulk/*.xml"]),
    # src/*/resources is magic; see the documentation of java_library.
    outs = ["src/generated/resources/org/apache/axiom/ts/xml/bulk/filelist"],
    cmd = "for f in $(SRCS); do echo $$(basename $$f) >> $@; done",
)

java_library(
    name = "xml-testsuite",
    srcs = glob(["src/main/java/**/*.java"]),
    resources = glob(["src/main/resources/**/*"]) + ["filelist"],
    visibility = ["//visibility:public"],
    testonly = 1,
    deps = [
        "@commons_codec//jar",
        "@commons_io//jar",
        "@javamail//jar",
        "@stax2_api//jar",
        "@woodstox//jar",
        "//testing/multiton",
        "//testing/testutils",
    ],
)
