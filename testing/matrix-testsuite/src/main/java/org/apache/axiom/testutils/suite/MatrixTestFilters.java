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

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.BiPredicate;

import com.google.common.collect.ImmutableList;

import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

/**
 * An immutable set of filters that match matrix test cases by class and/or LDAP filter expressions
 * on labels. Implements {@link BiPredicate} where {@link #test} returns {@code true} if the given
 * test class and labels match any of the configured filters.
 */
public final class MatrixTestFilters implements BiPredicate<Class<?>, Map<String, String>> {
    private static class Entry {
        private final Class<?> testClass;
        private final Filter filter;

        Entry(Class<?> testClass, Filter filter) {
            this.testClass = testClass;
            this.filter = filter;
        }

        boolean matches(Class<?> clazz, Dictionary<String, String> labels) {
            return (testClass == null || clazz.equals(testClass))
                    && (filter == null || filter.match(labels));
        }
    }

    public static final class Builder {
        private final ImmutableList.Builder<Entry> entries = ImmutableList.builder();

        private Builder() {}

        public Builder add(Class<?> testClass, String filter) {
            try {
                entries.add(
                        new Entry(
                                testClass,
                                filter == null ? null : FrameworkUtil.createFilter(filter)));
            } catch (InvalidSyntaxException ex) {
                throw new IllegalArgumentException("Invalid filter expression", ex);
            }
            return this;
        }

        public Builder add(Class<?> testClass) {
            return add(testClass, null);
        }

        public Builder add(String filter) {
            return add(null, filter);
        }

        public MatrixTestFilters build() {
            return new MatrixTestFilters(entries.build());
        }
    }

    private final ImmutableList<Entry> entries;

    private MatrixTestFilters(ImmutableList<Entry> entries) {
        this.entries = entries;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean test(Class<?> testClass, Map<String, String> labels) {
        Hashtable<String, String> hashtable = new Hashtable<>(labels);
        for (Entry entry : entries) {
            if (entry.matches(testClass, hashtable)) {
                return true;
            }
        }
        return false;
    }
}
