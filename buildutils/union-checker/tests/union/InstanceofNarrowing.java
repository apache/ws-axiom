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

/** Tests for {@code instanceof}-based narrowing ({@code UnionTransfer}). */
class InstanceofNarrowing {

    void acceptStringOrInteger(@Union(types = {String.class, Integer.class}) Object o) {}

    void acceptIntegerOnly(@Union(types = {Integer.class}) Object o) {}

    // x instanceof String || x instanceof Integer narrows x to @Union({String, Integer}).
    void or(Object x) {
        if (x instanceof String || x instanceof Integer) {
            acceptStringOrInteger(x);
        }
    }

    // The equivalent if/else if chain narrows x the same way in each branch.
    void elseIf(Object x) {
        if (x instanceof String) {
            acceptStringOrInteger(x);
        } else if (x instanceof Integer) {
            acceptStringOrInteger(x);
        } else {
            // :: error: (argument)
            acceptStringOrInteger(x);
        }
    }

    // x instanceof String narrows x to @Union({String}), which is not a subtype of
    // @Union({Integer}).
    void mismatch(Object x) {
        if (x instanceof String) {
            // :: error: (argument)
            acceptIntegerOnly(x);
        }
    }
}
