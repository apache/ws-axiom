/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.axiom.checker.union.Union;

/**
 * Tests for the least upper bound of two {@code @Union} qualifiers at a conditional expression
 * ({@code UnionQualifierHierarchy#leastUpperBoundWithElements}).
 */
class ConditionalLub {

    static class A {}

    static class B {}

    void acceptAOrB(@Union(types = {A.class, B.class}) Object o) {}

    void acceptAOnly(@Union(types = {A.class}) Object o) {}

    // The lub of @Union({A}) and @Union({B}) is @Union({A, B}).
    void lub(
            boolean flag,
            @Union(types = {A.class}) Object a,
            @Union(types = {B.class}) Object b) {
        acceptAOrB(flag ? a : b);
    }

    // @Union({A, B}) is not a subtype of @Union({A}).
    void lubNotNarrower(
            boolean flag,
            @Union(types = {A.class}) Object a,
            @Union(types = {B.class}) Object b) {
        // :: error: (argument)
        acceptAOnly(flag ? a : b);
    }
}
