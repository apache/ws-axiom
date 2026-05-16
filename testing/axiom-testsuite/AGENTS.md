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

# AI Agent Instructions

## Validating changes to axiom-testsuite

`testing/axiom-testsuite` contains abstract test cases that are executed against the Axiom implementations. The module has no tests of its own; its tests only run as part of `implementations/axiom-impl` (LLOM) and `implementations/axiom-dom` (DOOM).

**After every change to `testing/axiom-testsuite`, you MUST run the tests for both implementations:**

```
./mvnw -pl testing/axiom-testsuite,implementations/axiom-impl,implementations/axiom-dom -am test
```

Do not consider a change to `axiom-testsuite` complete until this command passes without failures.
