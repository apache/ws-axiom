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
package org.apache.axiom.testing.multiton;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Adapters {
    private final Map<Class<?>, Object> adapters = new HashMap<>();
    private boolean initialized;

    public <T> void add(Class<T> type, T adapter) {
        adapters.put(type, adapter);
    }

    public void add(Object adapter) {
        add(adapter, adapter.getClass(), new HashSet<Class<?>>());
    }

    private void add(Object adapter, Class<?> asType, Set<Class<?>> seen) {
        if (seen.add(asType)) {
            if (asType.getAnnotation(AdapterType.class) != null) {
                adapters.put(asType, adapter);
            }
            Class<?> superClass = asType.getSuperclass();
            if (superClass != null) {
                add(adapter, superClass, seen);
            }
            for (Class<?> iface : asType.getInterfaces()) {
                add(adapter, iface, seen);
            }
        }
    }

    <T> T get(Class<T> type) {
        return type.cast(adapters.get(type));
    }

    boolean initialized() {
        boolean result = initialized;
        initialized = true;
        return result;
    }
}
