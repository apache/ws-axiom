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

The Axiom API should not assume that StAX is used as the XML parser
====================================================================

## Decision

The Axiom API must not be designed around the assumption that a StAX
implementation is used as the underlying XML parser. Although existing Axiom
implementations (LLOM and DOOM) use StAX, this is not a strict requirement.
The only strict requirements for an Axiom implementation are:

*   It is able to construct a tree from an `XMLStreamReader` supplied by
    application code.

*   It is able to serialize a tree to an `XMLStreamWriter`.

*   It is able to construct an `XMLStreamReader` from a tree.

In particular:

*   Methods defined by the Axiom API should only declare `XMLStreamException` if
    they interact directly with a StAX object supplied by application code. Other
    error conditions (such as I/O errors or parser errors during deferred parsing)
    should use Axiom's own exception types.

*   API types such as `StAXParserConfiguration` that inherently assume StAX should
    be replaced by more generic abstractions that are independent of the parser
    technology.

## Rationale

*   An Axiom implementation could use a parser API other than StAX, or provide its
    own XML parser. Coupling the public API to StAX would unnecessarily prevent
    such implementations.

*   Keeping StAX concerns out of the API results in a cleaner separation of
    concerns: the public API defines the object model contract, while the choice of
    parser is an implementation detail.

*   Avoiding `XMLStreamException` on methods that don't directly interact with a
    caller-supplied StAX object gives application code a more meaningful exception
    hierarchy, making it possible to distinguish I/O errors from parser errors
    during deferred parsing.
