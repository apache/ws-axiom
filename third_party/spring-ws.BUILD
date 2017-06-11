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
    name = "xml",
    srcs = glob(["spring-xml/src/main/java/**/*.java"]),
    deps = [
        "@commons_logging//jar",
        "@jaxen//jar",
        "@spring_beans//jar",
        "@spring_context//jar",
        "@spring_core//jar",
        "@xmlschema//jar",
    ],
)

java_library(
    name = "core",
    srcs = glob(["spring-ws-core/src/main/java/**/*.java"]),
    resources = glob(["spring-ws-core/src/main/resources/**"]),
    deps = [
        "@axiom//axiom-api",
        "@axiom//axiom-api:stax",
        "@axiom//axiom-compat",
        "@commons_logging//jar",
        "@dom4j//jar",
        "@httpclient//jar",
        "@httpclient3//jar",
        "@httpcore//jar",
        "@jdom2//jar",
        "@servlet//jar",
        "@spring_aop//jar",
        "@spring_beans//jar",
        "@spring_context//jar",
        "@spring_core//jar",
        "@spring_oxm//jar",
        "@spring_web//jar",
        "@spring_webmvc//jar",
        "@wsdl4j//jar",
        "@xom//jar",
        ":xml",
    ],
)

java_library(
    name = "core-tests",
    srcs = glob(["spring-ws-core/src/test/java/**/*.java"]),
    resources = glob(["spring-ws-core/src/test/resources/**"]),
    deps = [
        "@aspectj_runtime//jar",
        "@axiom//axiom-api",
        "@axiom//axiom-compat",
        "@commons_logging//jar",
        "@dom4j//jar",
        "@easymock//jar",
        "@hamcrest//jar",
        "@httpclient//jar",
        "@httpclient3//jar",
        "@httpcore//jar",
        "@javamail//jar",
        "@jdom2//jar",
        "@jetty//jar",
        "@jetty_util//jar",
        "@junit//jar",
        "@log4j//jar",
        "@servlet//jar",
        "@spring_beans//jar",
        "@spring_context//jar",
        "@spring_core//jar",
        "@spring_oxm//jar",
        "@spring_test//jar",
        "@spring_web//jar",
        "@wsdl4j//jar",
        "@xmlunit//jar",
        "@xom//jar",
        ":core",
        ":xml",
    ],
)
