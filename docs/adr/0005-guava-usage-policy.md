<!--
  ~ Licensed to the Apache Software Foundation (ASF) under one
  ~ or more contributor license agreements. See the NOTICE file
  ~ distributed with this work for additional information
  ~ regarding copyright ownership. The ASF licenses this file
  ~ to you under the Apache License, Version 2.0 (the
  ~ "License"); you may not use this file except in compliance
  ~ with the License. You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing,
  ~ software distributed under the License is distributed on an
  ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  ~ KIND, either express or implied. See the License for the
  ~ specific language governing permissions and limitations
  ~ under the License.
  -->

Guava usage policy
==================

## Decision

Guava must not be added as a dependency of the core artifacts of the project (i.e. the
artifacts that end up on the classpath of applications consuming Axiom). Its use is allowed
and even encouraged in tests and in code that is only used during the build process (e.g.
Maven plugins, build utilities, code generators).

## Rationale

*   Guava is a large library with a significant footprint. Adding it as a transitive
    dependency of Axiom's core artifacts would increase the dependency burden on
    downstream consumers, many of whom may not use Guava themselves or may depend on
    a different version.

*   Guava has a history of breaking changes between major versions, which can lead to
    classpath conflicts when multiple libraries in an application depend on different
    versions of Guava. Keeping Guava out of the core artifacts avoids exposing Axiom
    users to these compatibility issues.

*   In tests and build-time code, the dependency scope is limited (`test` or build plugin
    classpath) and does not leak to consumers. In this context, Guava's rich collection
    utilities, preconditions, and other helpers improve code readability and reduce
    boilerplate, so their use is encouraged.

*   The Java standard library has progressively closed the gap with Guava (e.g.
    `java.util.Optional`, `java.util.stream`, `java.util.Objects`, `List.of`/`Map.of`).
    For core artifacts, these standard alternatives should be preferred.
