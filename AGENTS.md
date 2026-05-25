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

## Code formatting

This project uses the [spotless-maven-plugin](https://github.com/diffplug/spotless) with `palantirJavaFormat` to enforce consistent Java formatting. **After modifying any Java source files, you MUST run spotless to reformat the code:**

```
./mvnw -pl <module> spotless:apply
```

Replace `<module>` with the Maven module path(s) you modified (e.g. `axiom-api`, `implementations/axiom-impl`). To apply across all modules at once, omit `-pl`:

```
./mvnw spotless:apply
```

**CI will fail if spotless has not been applied.**
