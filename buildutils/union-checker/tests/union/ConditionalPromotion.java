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
 * Tests for promoting the type of a conditional expression to {@code @Union(types = {...})} when
 * one branch is {@code @UnknownUnion} but its erased type is a subtype of a union member
 * ({@code UnionTreeAnnotator}).
 */
class ConditionalPromotion {

    // sb has erased type StringBuilder, a subtype of CharSequence, so the conditional
    // expression is promoted to @Union({String, CharSequence}), regardless of which branch it
    // appears in.
    @Union(types = {String.class, CharSequence.class}) Object unknownSecond(
            boolean flag,
            @Union(types = {String.class, CharSequence.class}) Object data, StringBuilder sb) {
        return flag ? sb : data;
    }

    @Union(types = {String.class, CharSequence.class}) Object unknownFirst(
            boolean flag,
            @Union(types = {String.class, CharSequence.class}) Object data, StringBuilder sb) {
        return flag ? data : sb;
    }

    // other has erased type Object, which is not a subtype of String or CharSequence, so no
    // promotion happens and the conditional expression's type remains @UnknownUnion, which is not
    // a subtype of the declared @Union return type.
    @Union(types = {String.class, CharSequence.class}) Object noPromotion(
            boolean flag, @Union(types = {String.class, CharSequence.class}) Object data, Object other) {
        // :: error: (return)
        return flag ? other : data;
    }
}
