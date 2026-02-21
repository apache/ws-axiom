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

`axiom-api` should only contain public API
===========================================

## Decision

`axiom-api` should only contain Axiom's public API. All shared implementation
classes must be moved out of `axiom-api` (to `om-aspects` or another appropriate
module).

## Rationale

*   The more compact the public API is, the easier it is for users to understand the
    API and to locate the features they are looking for.

*   By definition, anything that is not part of the public API can be modified
    without the risk of breaking application code. Clarifying the distinction between
    the public API and internal implementation classes therefore gives the project
    more flexibility to implement changes.

*   Having a well defined abstract API allows to create alternative implementations
    of that API.
