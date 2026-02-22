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

Prefer AssertJ over Google Truth
=================================

## Decision

New test code must use [AssertJ](https://assertj.github.io/doc/) as the assertion library.
Existing usages of Google Truth and plain JUnit assertions (`assertTrue`, `assertEquals`,
`assertSame`, etc.) should be migrated to AssertJ when the opportunity arises.

## Rationale

*   AssertJ has a richer and more discoverable fluent API, providing a wider range of
    built-in assertions out of the box (e.g. for collections, exceptions, optional, etc.).

*   AssertJ is more actively maintained and has broader adoption in the Java ecosystem.

*   Standardizing on a single assertion library reduces the number of dependencies and
    makes the test code more consistent across modules.

*   AssertJ integrates well with IDEs and provides better error messages, making test
    failures easier to diagnose.

*   AssertJ's fluent `assertThat` style is more expressive than JUnit's traditional
    `assertEquals(expected, actual)` or `assertTrue(condition)` forms, which improves
    readability and reduces the risk of swapping expected/actual arguments.
