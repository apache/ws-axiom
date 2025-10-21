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

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Base class providing a simple mechanism that allows to extend the behavior of objects using
 * adapters.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class Adaptable {
    private static final Map<Class, List<AdapterFactory>> adapterFactoryMap = new HashMap<>();
    private final Adapters adapters = new Adapters();

    static {
        for (Iterator<AdapterFactory> it =
                        ServiceLoader.load(AdapterFactory.class, Adaptable.class.getClassLoader())
                                .iterator();
                it.hasNext(); ) {
            AdapterFactory adapterFactory = it.next();
            // TODO: only works in the basic case where the factory directly implements
            // AdapterFactory as the first interface
            Class clazz =
                    ((Class<?>)
                                    ((ParameterizedType)
                                                    adapterFactory.getClass()
                                                            .getGenericInterfaces()[0])
                                            .getActualTypeArguments()[0])
                            .asSubclass(Adaptable.class);
            List<AdapterFactory> adapterFactories = adapterFactoryMap.get(clazz);
            if (adapterFactories == null) {
                adapterFactories = new ArrayList<>();
                adapterFactoryMap.put(clazz, adapterFactories);
            }
            adapterFactories.add(adapterFactory);
        }
    }

    public final <T> T getAdapter(Class<T> type) {
        synchronized (adapters) {
            if (!adapters.initialized()) {
                Class<?> clazz = getClass();
                while (clazz != Adaptable.class) {
                    List<AdapterFactory> adapterFactories = adapterFactoryMap.get(clazz);
                    if (adapterFactories != null) {
                        for (AdapterFactory adapterFactory : adapterFactories) {
                            adapterFactory.createAdapters(this, adapters);
                        }
                    }
                    clazz = clazz.getSuperclass();
                }
            }
            return adapters.get(type);
        }
    }
}
