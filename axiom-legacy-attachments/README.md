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

# axiom-legacy-attachments

This module contains legacy MIME support for Apache Axiom. `axiom-legacy-attachments` provides compatibility support for older MIME-related integration paths and preserves behavior needed by existing users while newer MIME APIs (notably in the `org.apache.axiom.mime` package in `axiom-api`) and implementations are used elsewhere in the codebase. It is also used by Axis2 through its public API, which means behavior and API surface in this module can't be changed easily without risking downstream compatibility breaks; for new code, prefer the newer MIME APIs.

## Known issues

*   The code uses `OMException` even though that exception was intended for `org.apache.axiom.om`
    and `org.apache.axiom.soap` APIs.

*   There is some overlap between the `LifecycleManager` and `AttachmentCacheMonitor` classes. There
    should be a single API to manage the lifecycle of buffered attachments.

*   `LifecycleManager` is an abstract API (interface), but refers to `FileAccessor` which
    is placed in an `impl` package.
