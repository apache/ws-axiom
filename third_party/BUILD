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

genrule(
    name = "jacocoagent",
    srcs = ["@jacoco_agent//jar"],
    outs = ["jacocoagent.jar"],
    cmd = """
        dir=$$(pwd)
        cd $(@D)
        jar xf $$dir/$< jacocoagent.jar""",
)

java_library(
    name = "junit",
    exports = [
        "@junit//jar",
    ],
    runtime_deps = [
        "@hamcrest//jar",
    ],
)

java_library(
    name = "mockito",
    exports = [
        "@mockito_core//jar",
    ],
    runtime_deps = [
        "@byte_buddy//jar",
        "@objenesis//jar",
    ],
)

java_library(
    name = "pax-exam",
    exports = [
        "@pax_exam//jar",
        "@pax_exam_junit4//jar",
        "@pax_exam_spi//jar",
    ],
    runtime_deps = [
        "@felix//jar",
        "@ops4j_io//jar",
        "@ops4j_lang//jar",
        "@ops4j_store//jar",
        "@ops4j_util_property//jar",
        "@pax_exam_container_native//jar",
        "@pax_exam_link_assembly//jar",
        "@pax_swissbox_tracker//jar",
        "@pax_tinybundles//jar",
        "@pax_url_classpath//jar",
        "@pax_url_commons//jar",
        "@pax_url_link//jar",
        "@slf4j_api//jar",
        "@slf4j_jdk14//jar",
    ],
)

java_library(
    name = "xerces",
    exports = [
        "@xerces//jar",
    ],
    runtime_deps = [
        "@xml_apis//jar",
    ],
)
