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

java_library(
    name = "lifecycle",
    srcs = glob(["pax-swissbox-lifecycle/src/main/java/**/*.java"]),
)

java_library(
    name = "property",
    srcs = glob(["pax-swissbox-property/src/main/java/**/*.java"]),
    deps = [
        "@ops4j_base//:lang",
        "@ops4j_base//:util-property",
        "@osgi_core//jar",
    ],
)

java_library(
    name = "tracker",
    srcs = glob(["pax-swissbox-tracker/src/main/java/**/*.java"]),
    deps = [
        "@ops4j_base//:lang",
        "@osgi_core//jar",
        "@slf4j_api//jar",
        ":lifecycle",
    ],
)
