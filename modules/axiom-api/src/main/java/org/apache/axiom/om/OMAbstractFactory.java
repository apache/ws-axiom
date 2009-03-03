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
 * Provides default instances for plain XML, SOAP 1.1 and SOAP 1.2 object model factories.
 * 
 * <p>The implementation class for each object model type is determined by a specific
 * system property. If the system property is not set, a default implementation class
 * is chosen. The following table summarizes the system properties and default implementation
 * used:</p>
 * <table border="1">
 *   <tr>
 *     <th>Object model</th>
 *     <th>Method</th>
 *     <th>System property</th>
 *     <th>Default implementation</th>
 *   </tr>
 *   <tr>
 *     <td>Plain XML</td>
 *     <td>{@link #getOMFactory()}</td>
 *     <td><tt>om.factory</tt></td>
 *     <td>{@link org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory}</td>
 *   </tr>
 *   <tr>
 *     <td>SOAP 1.1</td>
 *     <td>{@link #getSOAP11Factory()}</td>
 *     <td><tt>soap11.factory</tt></td>
 *     <td>{@link org.apache.axiom.soap.impl.llom.soap11.SOAP11Factory}</td>
 *   </tr>
 *   <tr>
 *     <td>SOAP 1.2</td>
 *     <td>{@link #getSOAP12Factory()}</td>
 *     <td><tt>soap12.factory</tt></td>
 *     <td>{@link org.apache.axiom.soap.impl.llom.soap12.SOAP12Factory}</td>
 *   </tr>
 * </table>
 * <p>The methods in this class assume that {@link OMFactory} instances are stateless and
 * return the same instance on every invocation, i.e. the factory for each OM type is instantiated
 * only once. Configuring the system properties with factory implementation that are not
 * stateless will lead to unexpected results. It should be noted that the factories provided
 * by the DOOM implementation are not stateless and should therefore never be used as default
 * factories.</p>
 * <p>Each method in this class uses {@link System#getProperty(String)} to determine the value of
 * the relevant system property. A {@link SecurityException} thrown by this method is simply ignored
 * and the default factory implementation is used.</p> 
 */
public class OMAbstractFactory {
    public static final String META_FACTORY_NAME_PROPERTY = "org.apache.axiom.om.OMMetaFactory";

    private static final String DEFAULT_META_FACTORY_CLASS_NAME =
            "org.apache.axiom.om.impl.llom.factory.OMLinkedListMetaFactory";

    private static OMMetaFactory defaultMetaFactory;

    private OMAbstractFactory() {}

    /**
     * Get the default meta factory instance.
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
