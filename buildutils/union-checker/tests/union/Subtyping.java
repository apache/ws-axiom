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

/** Tests for {@code @Union} subset-based subtyping ({@code UnionQualifierHierarchy}). */
class Subtyping {

    void acceptNarrow(@Union(types = {String.class}) Object o) {}

    void acceptWide(@Union(types = {String.class, Integer.class}) Object o) {}

    // @Union({String}) is a subtype of @Union({String, Integer}).
    void narrowToWide(@Union(types = {String.class}) Object narrow) {
        acceptWide(narrow);
    }

    // @Union({String, Integer}) is not a subtype of @Union({String}).
    void wideToNarrow(@Union(types = {String.class, Integer.class}) Object wide) {
        // :: error: (argument)
        acceptNarrow(wide);
    }

    // Equal sets are subtypes of each other.
    void sameSet(@Union(types = {Integer.class, String.class}) Object wide) {
        acceptWide(wide);
    }
}
