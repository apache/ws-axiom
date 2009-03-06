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

package org.apache.axiom.om;

import org.apache.axiom.injection.FactoryInjectionComponent;
import org.apache.axiom.soap.SOAPFactory;

/**
 * Provides default instances for object model and meta factories.
 * <p>
 * The {@link #getMetaFactory()} method returns the default {@link OMMetaFactory} instance.
 * The implementation class is determined by the <code>org.apache.axiom.om.OMMetaFactory</code>
 * system property. If this property is not set, the meta factory for the LLOM implementation
 * is used.
 * <p>
 * The {@link #getOMFactory()}, {@link #getSOAP11Factory()} and {@link #getSOAP12Factory()}
 * methods return default instances for plain XML, SOAP 1.1 and SOAP 1.2 object model factories.
 * They are convenience methods calling {@link #getMetaFactory()} and then delegating to the
 * returned {@link OMMetaFactory}.
 * <p>
 * Note that while {@link #getMetaFactory()} always returns the same instance, the other methods
 * may return new instances on every invocation, depending on the {@link OMMetaFactory}
 * implementation.
 * <p>
 */
public class OMAbstractFactory {
    public static final String META_FACTORY_NAME_PROPERTY = "org.apache.axiom.om.OMMetaFactory";

    private static final String DEFAULT_META_FACTORY_CLASS_NAME =
            "org.apache.axiom.om.impl.llom.factory.OMLinkedListMetaFactory";

    private static OMMetaFactory defaultMetaFactory;

    private OMAbstractFactory() {}

    /**
     * Get the default meta factory instance. The implementation class is determined by the
     * <code>org.apache.axiom.om.OMMetaFactory</code> system property. If this property is not
     * set, the meta factory for the LLOM implementation is returned.
     * <p>
     * This method uses {@link System#getProperty(String)} to determine the value of
     * the <code>org.apache.axiom.om.OMMetaFactory</code> system property. A
     * {@link SecurityException} thrown by this method is simply ignored
     * and the default factory implementation is used.
     *
     * @return the default OM factory instance
     * @throws OMException if the factory's implementation class can't be found
     *                     or if the class can't be instantiated
     */
    public static OMMetaFactory getMetaFactory() {
        OMMetaFactory of = FactoryInjectionComponent.getMetaFactory();
        if(of!=null){
            return of;
        }
        
        if (defaultMetaFactory != null) {
            return defaultMetaFactory;
        }
        
        String omFactory;
        try {
            omFactory = System.getProperty(META_FACTORY_NAME_PROPERTY);
            if (omFactory == null || "".equals(omFactory)) {
                omFactory = DEFAULT_META_FACTORY_CLASS_NAME;
            }
        } catch (SecurityException e) {
            omFactory = DEFAULT_META_FACTORY_CLASS_NAME;
        }

        try {
            defaultMetaFactory = (OMMetaFactory) Class.forName(omFactory).newInstance();
        } catch (InstantiationException e) {
            throw new OMException(e);
        } catch (IllegalAccessException e) {
            throw new OMException(e);
        } catch (ClassNotFoundException e) {
            throw new OMException(e);
        }
        return defaultMetaFactory;
    }
    
    /**
     * Get the default OM factory instance.
     *
     * @return the default OM factory instance
     * @throws OMException if the factory's implementation class can't be found
     *                     or if the class can't be instantiated
     */
    public static OMFactory getOMFactory() {
        return getMetaFactory().getOMFactory();
    }


    /**
     * Get the default SOAP 1.1 OM factory instance.
     *
     * @return the default SOAP 1.1 OM factory instance
     * @throws OMException if the factory's implementation class can't be found
     *                     or if the class can't be instantiated
     */
    public static SOAPFactory getSOAP11Factory() {
        return getMetaFactory().getSOAP11Factory();
    }


    /**
     * Get the default SOAP 1.2 OM factory instance.
     *
     * @return the default SOAP 1.2 OM factory instance
     * @throws OMException if the factory's implementation class can't be found
     *                     or if the class can't be instantiated
     */
    public static SOAPFactory getSOAP12Factory() {
        return getMetaFactory().getSOAP12Factory();
    }
}
