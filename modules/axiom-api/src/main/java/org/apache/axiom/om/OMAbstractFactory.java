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
 * <p>Since {@link OMFactory} instances are supposed to be stateless, each method in this class
 * returns the same instance on every invocation, i.e. the factory for each OM type is instantiated
 * only once.</p>
 * <p>Each method in this class uses {@link System#getProperty(String)} to determine the value of
 * the relevant system property. A {@link SecurityException} thrown by this method is simply ignored
 * and the default factory implementation is used.</p> 
 */
public class OMAbstractFactory {
    public static final String OM_FACTORY_NAME_PROPERTY = "om.factory";
    public static final String SOAP11_FACTORY_NAME_PROPERTY = "soap11.factory";
    public static final String SOAP12_FACTORY_NAME_PROPERTY = "soap12.factory";

    private static final String DEFAULT_OM_FACTORY_CLASS_NAME =
            "org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory";
    private static final String DEFAULT_SOAP11_FACTORY_CLASS_NAME =
            "org.apache.axiom.soap.impl.llom.soap11.SOAP11Factory";
    private static final String DEFAULT_SOAP12_FACTORY_CLASS_NAME =
            "org.apache.axiom.soap.impl.llom.soap12.SOAP12Factory";

    private static OMFactory defaultOMFactory = null;
    private static SOAPFactory defaultSOAP11OMFactory = null;
    private static SOAPFactory defaultSOAP12OMFactory = null;

    private OMAbstractFactory() {}

    /**
     * Get the default OM factory instance.
     *
     * @return the default OM factory instance
     * @throws OMException if the factory's implementation class can't be found
     *                     or if the class can't be instantiated
     */
    public static OMFactory getOMFactory() {
        if (defaultOMFactory != null) {
            return defaultOMFactory;
        }
        String omFactory;
        try {
            omFactory = System.getProperty(OM_FACTORY_NAME_PROPERTY);
            if (omFactory == null || "".equals(omFactory)) {
                omFactory = DEFAULT_OM_FACTORY_CLASS_NAME;
            }
        } catch (SecurityException e) {
            omFactory = DEFAULT_OM_FACTORY_CLASS_NAME;
        }

        try {
            defaultOMFactory = (OMFactory) Class.forName(omFactory).newInstance();
        } catch (InstantiationException e) {
            throw new OMException(e);
        } catch (IllegalAccessException e) {
            throw new OMException(e);
        } catch (ClassNotFoundException e) {
            throw new OMException(e);
        }
        return defaultOMFactory;
    }


    /**
     * Get the default SOAP 1.1 OM factory instance.
     *
     * @return the default SOAP 1.1 OM factory instance
     * @throws OMException if the factory's implementation class can't be found
     *                     or if the class can't be instantiated
     */
    public static SOAPFactory getSOAP11Factory() {
        if (defaultSOAP11OMFactory != null) {
            return defaultSOAP11OMFactory;
        }
        String omFactory;
        try {
            omFactory = System.getProperty(SOAP11_FACTORY_NAME_PROPERTY);
            if (omFactory == null || "".equals(omFactory)) {
                omFactory = DEFAULT_SOAP11_FACTORY_CLASS_NAME;
            }
        } catch (SecurityException e) {
            omFactory = DEFAULT_SOAP11_FACTORY_CLASS_NAME;
        }
        
        try {
            defaultSOAP11OMFactory = (SOAPFactory) Class.forName(omFactory).newInstance();
        } catch (InstantiationException e) {
            throw new OMException(e);
        } catch (IllegalAccessException e) {
            throw new OMException(e);
        } catch (ClassNotFoundException e) {
            throw new OMException(e);
        }
        return defaultSOAP11OMFactory;
    }


    /**
     * Get the default SOAP 1.2 OM factory instance.
     *
     * @return the default SOAP 1.2 OM factory instance
     * @throws OMException if the factory's implementation class can't be found
     *                     or if the class can't be instantiated
     */
    public static SOAPFactory getSOAP12Factory() {
        if (defaultSOAP12OMFactory != null) {
            return defaultSOAP12OMFactory;
        }
        String omFactory;
        try {
            omFactory = System.getProperty(SOAP12_FACTORY_NAME_PROPERTY);
            if (omFactory == null || "".equals(omFactory)) {
                omFactory = DEFAULT_SOAP12_FACTORY_CLASS_NAME;
            }
        } catch (SecurityException e) {
            omFactory = DEFAULT_SOAP12_FACTORY_CLASS_NAME;
        }
        
        try {
            defaultSOAP12OMFactory = (SOAPFactory) Class.forName(omFactory).newInstance();
        } catch (InstantiationException e) {
            throw new OMException(e);
        } catch (IllegalAccessException e) {
            throw new OMException(e);
        } catch (ClassNotFoundException e) {
            throw new OMException(e);
        }
        return defaultSOAP12OMFactory;
    }
}
