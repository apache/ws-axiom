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

import org.apache.axiom.soap.SOAPFactory;

/**
 * Provides default instances for object model and meta factories.
 * <p>
 * The {@link #getMetaFactory()} method returns the default {@link OMMetaFactory} instance.
 * See the Javadoc of the {@link #getMetaFactory()} method for details about how this
 * instance is determined.
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

    /**
     * Feature for Axiom implementations that can be used as default implementations.
     */
    public static final String FEATURE_DEFAULT = "default";
    
    /**
     * Feature for Axiom implementations that implement DOM in addition to the Axiom API.
     */
    public static final String FEATURE_DOM = "dom";
    
    private static final String DEFAULT_LOCATOR_CLASS_NAME =
            "org.apache.axiom.locator.DefaultOMMetaFactoryLocator";

    /**
     * The default {@link OMMetaFactoryLocator} that will be used if no locator has been set
     * explicitly.
     */
    private static final OMMetaFactoryLocator defaultLocator;
    
    /**
     * The {@link OMMetaFactoryLocator} set through
     * {@link #setMetaFactory(OMMetaFactory)}. If this is <code>null</code>,
     * then {@link #defaultMetaFactory} will be returned by
     * {@link #getMetaFactory()}.
     */
    private static volatile OMMetaFactoryLocator metaFactoryLocator;

    static {
        // We could actually instantiate the default locator directly, but doing it using
        // reflection avoids introducing a circular dependency between the org.apache.axiom.om
        // and org.apache.axiom.locator packages.
        try {
            defaultLocator = (OMMetaFactoryLocator)Class.forName(DEFAULT_LOCATOR_CLASS_NAME).newInstance();
        } catch (InstantiationException ex) {
            throw new InstantiationError(ex.getMessage());
        } catch (IllegalAccessException ex) {
            throw new IllegalAccessError(ex.getMessage());
        } catch (ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
    
    private OMAbstractFactory() {}

    /**
     * Explicitly set a meta factory locator. The new locator will be used by all subsequent calls
     * to {@link #getMetaFactory()} and {@link #getMetaFactory(String)} to locate the appropriate
     * meta factory. Note that the meta factory locator is an application wide setting. More
     * precisely, the configured locator will be used by all classes loaded from the class loader
     * where Axiom is deployed and all its child class loaders. Therefore this method should be used
     * with care and only be invoked during the initialization of the application.
     * <p>
     * When Axiom is deployed as a bundle in an OSGi environment, this method will be used to inject
     * the meta factory instances from the deployed implementation bundles.
     * 
     * @param locator
     *            the new meta factory locator, or <code>null</code> to revert to the default meta
     *            factory locator
     */
    public static void setMetaFactoryLocator(OMMetaFactoryLocator locator) {
        metaFactoryLocator = locator;
    }
    
    /**
     * Get the default meta factory instance. This method uses the following ordered lookup
     * procedure to determine the default instance:
     * <ol>
     * <li>If an instance has been set using {@link #setMetaFactory(OMMetaFactory)}, then that
     * instance is returned. Note that this will be the case in an OSGi runtime, where
     * {@link #setMetaFactory(OMMetaFactory)} is invoked by a helper component that is part of
     * Axiom.
     * <li>Use the <code>org.apache.axiom.om.OMMetaFactory</code> system property. This method uses
     * {@link System#getProperty(String)} to determine the value of the system property. A
     * {@link SecurityException} thrown by this method is simply ignored and the lookup procedure
     * continues.
     * <li>Use the JDK 1.3 service discovery mechanism to determine the classname of the meta
     * factory. The method will look for a classname in the file
     * <code>META-INF/services/org.apache.axiom.om.OMMetaFactory</code> in jars in the class path.
     * <li>Return the meta factory for the LLOM implementation is returned.
     * </ol>
     * 
     * @return the default OM factory instance
     * @throws OMException
     *             if the factory's implementation class can't be found or if the class can't be
     *             instantiated
     */
    public static OMMetaFactory getMetaFactory() {
        return getMetaFactory(FEATURE_DEFAULT);
    }
    
    public static OMMetaFactory getMetaFactory(String feature) {
        OMMetaFactoryLocator locator = metaFactoryLocator;
        if (locator == null) {
            locator = defaultLocator;
        }
        OMMetaFactory metaFactory = locator.getOMMetaFactory(feature);
        if (metaFactory == null) {
            throw new OMException("No meta factory found for feature '" + feature + "'");
        } else {
            return metaFactory;
        }
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
