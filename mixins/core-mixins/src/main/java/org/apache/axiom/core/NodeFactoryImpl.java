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
package org.apache.axiom.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.axiom.core.util.EdgeRelation;
import org.apache.axiom.core.util.TopologicalSort;

public abstract class NodeFactoryImpl implements NodeFactory {
    private final Map<Class<?>,Constructor<?>> constructorMap;
    
    public NodeFactoryImpl(ClassLoader cl, String... packages) {
        List<Class<?>> implementations = new ArrayList<Class<?>>();
        for (String pkg : packages) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(cl.getResourceAsStream(pkg.replace('.', '/') + "/nodetypes.index"), "UTF-8"));
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        if (line.startsWith("#")) {
                            continue;
                        }
                        String className = pkg + "." + line;
                        try {
                            implementations.add(cl.loadClass(className));
                        } catch (ClassNotFoundException ex) {
                            throw new NodeFactoryException("Failed to load class " + className, ex);
                        }
                    }
                } finally {
                    in.close();
                }
            } catch (IOException ex) {
                throw new NodeFactoryException("Failed to load node type index for package " + pkg, ex);
            }
        }
        implementations = TopologicalSort.sort(implementations, new EdgeRelation<Class<?>>() {
            @Override
            public boolean isEdge(Class<?> from, Class<?> to) {
                return to.isAssignableFrom(from);
            }
        });
        Map<Class<?>,Class<?>> interfaceToImplementationMap = new HashMap<Class<?>,Class<?>>();
        Map<Class<?>,Constructor<?>> implementationToConstructorMap = new HashMap<Class<?>,Constructor<?>>();
        Set<Class<?>> ambiguousInterfaces = new HashSet<Class<?>>();
        for (Class<?> implementation : implementations) {
            Set<Class<?>> interfaces = new HashSet<Class<?>>();
            collectInterfaces(implementation, interfaces);
            for (Class<?> iface : interfaces) {
                if (!ambiguousInterfaces.contains(iface)) {
                    Class<?> clazz = interfaceToImplementationMap.get(iface);
                    if (clazz == null || implementation.isAssignableFrom(clazz)) {
                        interfaceToImplementationMap.put(iface, implementation);
                    } else if (!clazz.isAssignableFrom(implementation)) {
                        interfaceToImplementationMap.remove(iface);
                        ambiguousInterfaces.add(iface);
                    }
                }
            }
            try {
                implementationToConstructorMap.put(implementation, implementation.getConstructor());
            } catch (NoSuchMethodException ex) {
                throw new NodeFactoryException("Failed to get constructor for " + implementation.getName(), ex);
            }
        }
        constructorMap = new HashMap<Class<?>,Constructor<?>>();
        for (Map.Entry<Class<?>,Class<?>> entry : interfaceToImplementationMap.entrySet()) {
            constructorMap.put(entry.getKey(), implementationToConstructorMap.get(entry.getValue()));
        }
        // TODO: this should eventually go away
        constructorMap.putAll(implementationToConstructorMap);
    }

    private static void collectInterfaces(Class<?> clazz, Set<Class<?>> interfaces) {
        for (Class<?> iface : clazz.getInterfaces()) {
            if (interfaces.add(iface)) {
                collectInterfaces(iface, interfaces);
            }
        }
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            collectInterfaces(superclass, interfaces);
        }
    }
    
    @Override
    public final <T extends CoreNode> T createNode(Class<T> type) {
        Constructor<?> constructor = constructorMap.get(type);
        if (constructor == null) {
            throw new NodeFactoryException("Unknown node type " + type.getName());
        } else {
            try {
                return type.cast(constructor.newInstance());
            } catch (InvocationTargetException ex) {
                throw new NodeFactoryException("Caught exception thrown by constructor", ex.getCause());
            } catch (InstantiationException ex) {
                throw new NodeFactoryException("Failed to invoke constructor", ex);
            } catch (IllegalAccessException ex) {
                throw new NodeFactoryException("Failed to invoke constructor", ex);
            }
        }
    }
}
