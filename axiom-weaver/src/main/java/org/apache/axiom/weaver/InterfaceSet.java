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
package org.apache.axiom.weaver;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

final class InterfaceSet implements Iterable<Class<?>> {
    private final Set<Class<?>> interfaces = new LinkedHashSet<>();

    @Override
    public Iterator<Class<?>> iterator() {
        return interfaces.iterator();
    }

    void add(Class<?> iface) {
        if (interfaces.contains(iface)) {
            return;
        }
        for (Class<?> i : interfaces) {
            if (iface.isAssignableFrom(i)) {
                return;
            }
        }
        for (Iterator<Class<?>> it = interfaces.iterator(); it.hasNext(); ) {
            Class<?> i = it.next();
            if (i.isAssignableFrom(iface)) {
                it.remove();
            }
        }
        interfaces.add(iface);
    }

    void addAll(Iterable<Class<?>> ifaces) {
        for (Class<?> iface : ifaces) {
            add(iface);
        }
    }

    void remove(Class<?> iface) {
        interfaces.remove(iface);
    }
}
