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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for multitons. A multiton is a class that has a fixed set of instances.
 *
 * <p>The set of instances of a multiton is determined by:
 *
 * <ul>
 *   <li>The values of all public static final fields in the multiton class that have types that are
 *       assignment compatible with the multiton class.
 *   <li>The array elements returned by methods in the multiton class annotated with {@link
 *       Instances}. These methods must be private static, have no parameters and return an array
 *       with an component type assignment compatible with the multiton class.
 * </ul>
 */
@SuppressWarnings({"unchecked"})
public abstract class Multiton extends Adaptable {
    private static final Map<Class<?>, List<?>> instancesMap = new HashMap<>();

    /**
     * Get all instances of the given multiton. See the Javadoc of the {@link Multiton} class for
     * information about how the instances are determined.
     *
     * @param multitonClass the multiton class
     * @return the list of instances
     * @throws MultitonInstantiationException if an error occurred
     */
    public static synchronized <T extends Multiton> List<T> getInstances(Class<T> multitonClass) {
        List<T> instances = (List<T>) instancesMap.get(multitonClass);
        if (instances == null) {
            instances = new ArrayList<>();
            for (Field field : multitonClass.getDeclaredFields()) {
                int mod = field.getModifiers();
                if (Modifier.isPublic(mod)
                        && Modifier.isStatic(mod)
                        && Modifier.isFinal(mod)
                        && multitonClass.isAssignableFrom(field.getType())) {
                    try {
                        instances.add(multitonClass.cast(field.get(null)));
                    } catch (IllegalAccessException ex) {
                        throw new MultitonInstantiationException(ex);
                    }
                }
            }
            for (Method method : multitonClass.getDeclaredMethods()) {
                if (method.getAnnotation(Instances.class) != null) {
                    int mod = method.getModifiers();
                    if (!Modifier.isPrivate(mod) || !Modifier.isStatic(mod)) {
                        throw new MultitonInstantiationException(
                                "Methods annotated with @Instances must be private static");
                    }
                    if (method.getParameterTypes().length > 0) {
                        throw new MultitonInstantiationException(
                                "Methods annotated with @Instances must not take any parameters");
                    }
                    Class<?> returnType = method.getReturnType();
                    if (!returnType.isArray()
                            || !multitonClass.isAssignableFrom(returnType.getComponentType())) {
                        throw new MultitonInstantiationException(
                                "Invalid return type for method annotated with @Instances");
                    }
                    method.setAccessible(true);
                    try {
                        for (Object instance : (Object[]) method.invoke(null)) {
                            instances.add(multitonClass.cast(instance));
                        }
                    } catch (IllegalAccessException ex) {
                        throw new MultitonInstantiationException(ex);
                    } catch (InvocationTargetException ex) {
                        throw new MultitonInstantiationException(ex.getCause());
                    }
                }
            }
            instances = Collections.unmodifiableList(instances);
            instancesMap.put(multitonClass, instances);
        }
        return instances;
    }
}
