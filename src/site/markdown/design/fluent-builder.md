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

Fluent builder conventions
==========================

Builders are mutable objects which create other (often immutable) objects.
They are used as an alternative to public constructors in order to avoid
constructors with long argument lists and classes with many different constructors.
Builders generally use the fluent pattern so that method calls to the setters
of the builder can be chained.

Axiom uses the following coding conventions for builders:

*   A builder type is implemented as a public static final inner class of the class
    to be built. The name of the class is `Builder` and it's constructor must not be public.

*   A builder instance is created using a static method named `builder` defined on the
    type being built.

*   The builder has a method named `build` that builds the object. This method takes no
    arguments.

*   The setter methods on the builder have names that follow the JavaBeans conventions.
    They return `this` so that they can be chained. As a general rule the builder type should not
    have getter methods.

*   The built type may also have an instance method called `toBuilder` returning a new
    builder initialized with the property values from the existing instance. This can then
    be used to create a new instance similar to an existing one, and the expectation is that
    `someInstance.toBuilder().build()` would produce an instance that is equal (in the
    relevant sense) to `someInstance`.
    
    If the build type has a `toBuilder` method, then the builder type may need additional
    methods allowing the caller to unset properties. In certain cases it may also be pertinent
    to add getter methods to support complex state transitions.
