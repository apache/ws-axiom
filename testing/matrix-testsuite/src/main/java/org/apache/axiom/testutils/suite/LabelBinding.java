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
package org.apache.axiom.testutils.suite;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import com.google.inject.Injector;

public interface LabelBinding<T> {
    LabelBinding<Dimension> DIMENSION = (injector, value, labels) -> value.addLabels(labels);

    void addLabels(Injector injector, T value, LabelTarget labels);

    static <T> LabelBinding<T> simpleString(String label, Function<T, String> fn) {
        return (injector, value, labels) -> labels.addLabel(label, fn.apply(value));
    }

    static <T> LabelBinding<T> simpleBoolean(String label, Predicate<T> fn) {
        return (injector, value, labels) -> labels.addLabel(label, fn.test(value));
    }

    static <T> LabelBinding<T> simpleInt(String label, ToIntFunction<T> fn) {
        return (injector, value, labels) -> labels.addLabel(label, fn.applyAsInt(value));
    }

    static LabelBinding<String> simpleString(String label) {
        return simpleString(label, v -> v);
    }

    static LabelBinding<Boolean> simpleBoolean(String label) {
        return simpleBoolean(label, v -> v);
    }

    static LabelBinding<Integer> simpleInt(String label) {
        return simpleInt(label, v -> v);
    }
}
