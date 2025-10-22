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
package org.apache.axiom.util.stax.dialect;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class to remove the {@link XMLInputFactory} or {@link XMLOutputFactory} wrapper added by
 * JBoss 7. This class gives access the actual (implementation dependent) factory. This is important
 * to correct detect the StAX dialect.
 */
final class JBossFactoryUnwrapper {
    private static final Log log = LogFactory.getLog(JBossFactoryUnwrapper.class);

    private final Class<?> wrapperClass;
    private final Field actual;

    private JBossFactoryUnwrapper(Class<?> factoryType) throws Exception {
        wrapperClass = Class.forName("__redirected.__" + factoryType.getSimpleName());
        try {
            actual = wrapperClass.getDeclaredField("actual");
            AccessController.doPrivileged(
                    new PrivilegedAction<Void>() {
                        @Override
                        public Void run() {
                            actual.setAccessible(true);
                            return null;
                        }
                    });
        } catch (Exception ex) {
            log.error(
                    "Found JBoss wrapper class for "
                            + factoryType.getSimpleName()
                            + ", but unwrapping is not supported",
                    ex);
            throw ex;
        }
    }

    /**
     * Get the unwrapper for the given factory type.
     *
     * @param factoryType the factory type ({@link XMLInputFactory} or {@link XMLOutputFactory})
     * @return the unwrapper, or <code>null</code> if the unwrapper could not be created (which
     *     usually means that the code is not executed inside JBoss)
     */
    static JBossFactoryUnwrapper create(Class<?> factoryType) {
        try {
            return new JBossFactoryUnwrapper(factoryType);
        } catch (Exception ex) {
            return null;
        }
    }

    Object unwrap(Object factory) {
        if (wrapperClass.isInstance(factory)) {
            try {
                return actual.get(factory);
            } catch (IllegalAccessException ex) {
                throw new IllegalAccessError(ex.getMessage());
            }
        } else {
            return factory;
        }
    }
}
