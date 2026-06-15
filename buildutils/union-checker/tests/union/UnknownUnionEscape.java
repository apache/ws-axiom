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
 * Tests for the {@code @UnknownUnion} escape rule, which allows an unconstrained value to be used
 * where {@code @Union(types = {...})} is expected if its erased type is a subtype of one of the
 * union members ({@code UnionTypeHierarchy}).
 */
class UnknownUnionEscape {

    void acceptStringOrCharSequence(@Union(types = {String.class, CharSequence.class}) Object o) {}

    void acceptIntegerOnly(@Union(types = {Integer.class}) Object o) {}

    // String is itself one of the union members.
    void exactMember(String s) {
        acceptStringOrCharSequence(s);
    }

    // StringBuilder is a subtype of CharSequence, a union member.
    void subtypeOfMember(StringBuilder sb) {
        acceptStringOrCharSequence(sb);
    }

    // StringBuilder is not a subtype of Integer, the only union member.
    void notAMember(StringBuilder sb) {
        // :: error: (argument)
        acceptIntegerOnly(sb);
    }
}
