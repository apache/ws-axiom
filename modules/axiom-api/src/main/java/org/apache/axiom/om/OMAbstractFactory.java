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

import org.apache.axiom.om.dom.DOMMetaFactory;
import org.apache.axiom.soap.SOAPFactory;

/**
 * Provides instances for object model and meta factories.
 * <p>
 * The {@link #getMetaFactory()} method returns the {@link OMMetaFactory} instance for the default
 * Axiom implementation. The {@link #getMetaFactory(String)} method locates a {@link OMMetaFactory}
 * instance for an Axiom implementation with a given feature. The following features are predefined
 * by the Axiom API:
 * <dl>
 * <dt>{@link #FEATURE_DEFAULT}
 * <dd>Identifies the default Axiom implementation. An implementation with that feature is expected
 * to provide a full implementation of the Axiom API.
 * <dt>{@link #FEATURE_DOM}
 * <dd>Used by Axiom implementations that implement DOM in addition to the Axiom API.
 * </dl>
 * Implementations may define other custom features.
 * <p>
 * Axiom discovers implementations by looking for <tt>META-INF/axiom.xml</tt> resources. They
 * specify the {@link OMMetaFactory} implementation as well as the set of features that each
 * implementation supports. If multiple implementations with the same feature are found, then Axiom
 * will select the one with the highest priority. The priority for a given feature is also declared
 * in <tt>META-INF/axiom.xml</tt>. This algorithm is used both in non OSGi and OSGi environments.
 * <p>
 * In a non OSGi environment, the default Axiom implementation can be overridden using the system
 * property specified by {@link #META_FACTORY_NAME_PROPERTY}.
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
     * Feature for Axiom implementations that implement DOM in addition to the Axiom API. The
     * {@link OMMetaFactory} for such an Axiom implementation must implement {@link DOMMetaFactory}.
     */
    public static final String FEATURE_DOM = "dom";
    
    private static final String DEFAULT_LOCATOR_CLASS_NAME =
            "org.apache.axiom.locator.DefaultOMMetaFactoryLocator";

    /**
     * The default {@link OMMetaFactoryLocator} that will be used if no locator has been set
     * explicitly.
     */
    private static final OMMetaFactoryLocator defaultMetaFactoryLocator;
    
    /**
     * The {@link OMMetaFactoryLocator} set through
     * {@link #setMetaFactoryLocator(OMMetaFactoryLocator)}. If this is <code>null</code>, then
     * {@link #defaultMetaFactoryLocator} will be used.
     */
    private static volatile OMMetaFactoryLocator metaFactoryLocator;

    static {
        // We could actually instantiate the default locator directly, but doing it using
        // reflection avoids introducing a circular dependency between the org.apache.axiom.om
        // and org.apache.axiom.locator packages.
        try {
            defaultMetaFactoryLocator = (OMMetaFactoryLocator)Class.forName(DEFAULT_LOCATOR_CLASS_NAME).newInstance();
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
     * Get the meta factory instance for the default Axiom implementation. This method is equivalent
     * to {@link #getMetaFactory(String)} with {@link #FEATURE_DEFAULT} as parameter.
     * 
     * @return the meta factory instance for the default Axiom implementation
     * @throws OMException
     *             if no Axiom implementation with {@link #FEATURE_DEFAULT} could be located
     */
    public static OMMetaFactory getMetaFactory() {
        return getMetaFactory(FEATURE_DEFAULT);
    }
    
    /**
     * Get the meta factory instance for the Axiom implementation with a given feature. If multiple
     * Axiom implementations declare the same feature, then the method will return the meta factory
     * for the implementation that declares the highest priority for that feature in its
     * <tt>META-INF/axiom.xml</tt> descriptor.
     * 
     * @param feature
     *            the requested feature
     * @return the meta factory instance for the Axiom implementation with the given feature.
     * @throws OMException
     *             if no Axiom implementation with the requested feature could be located
     */
    public static OMMetaFactory getMetaFactory(String feature) {
        OMMetaFactoryLocator locator = metaFactoryLocator;
        if (locator == null) {
            locator = defaultMetaFactoryLocator;
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
