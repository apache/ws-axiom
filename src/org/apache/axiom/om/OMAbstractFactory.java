/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axiom.om;

import org.apache.axiom.soap.SOAPFactory;

public class OMAbstractFactory {
    public static final String OM_FACTORY_NAME_PROPERTY = "om.factory";
    public static final String SOAP11_FACTORY_NAME_PROPERTY = "soap11.factory";
    public static final String SOAP12_FACTORY_NAME_PROPERTY = "soap12.factory";
    public static final String SOAP_FACTORY_NAME_PROPERTY = "soap.factory";

    /**
     * Picks up the default factory implementation from the classpath.
     *
     * @return Returns OMFactory.
     */
    public static OMFactory getOMFactory() {
        return FactoryFinder.findOMFactory(null);
    }

    /**
     * If user needs to provide his own factory implementation, provide the
     * Class Loader here.
     *
     * @param classLoader
     * @return Returns OMFactory.
     */
    public static OMFactory getOMFactory(ClassLoader classLoader) {
        return FactoryFinder.findOMFactory(classLoader);
    }

    /**
     * Gets the <code>soapFactory</code> factory implementation from the classpath
     *
     * @param soapFactory Fully qualified SOAP 1.1 or SOAP 1.2 Factory implementation class name
     * @return Returns the SOAP 1.1 or 1.2 Factory implementation instance corresponding to <code>soapFactory</code>
     */
    public static SOAPFactory getSOAPFactory(String soapFactory) {
        return FactoryFinder.findSOAPFactory(null, soapFactory);
    }

    /**
     * Gets the <code>soapFactory</code> factory implementation using the provided
     * <code>classLoader</code>
     *
     * @param classLoader
     * @param soapFactory Fully qualified SOAP 1.1 or SOAP 1.2 Factory implementation class name
     * @return Returns the SOAP 1.1 or 1.2 Factory implementation instance corresponding to <code>soapFactory</code>
     */
    public static SOAPFactory getSOAPFactory(ClassLoader classLoader, String soapFactory) {
        return FactoryFinder.findSOAPFactory(classLoader, soapFactory);
    }

    /**
     * Gets the default factory implementation from the classpath.
     *
     * @return Returns SOAPFactory.
     */
    public static SOAPFactory getSOAP11Factory() {
        return FactoryFinder.findSOAP11Factory(null);
    }

    /**
     * If user needs to provide his own factory implementation, provide the
     * Class Loader here.
     *
     * @param classLoader
     * @return Returns SOAPFactory.
     */
    public static SOAPFactory getSOAP11Factory(ClassLoader classLoader) {
        return FactoryFinder.findSOAP11Factory(classLoader);
    }

    /**
     * Gets the default factory implementation from the classpath.
     *
     * @return Returns SOAPFactory.
     */
    public static SOAPFactory getSOAP12Factory() {
        return FactoryFinder.findSOAP12Factory(null);
    }

    /**
     * If user needs to provide his own factory implementation, provide the
     * Class Loader here.
     *
     * @param classLoader
     * @return Returns SOAPFactory.
     */
    public static SOAPFactory getSOAP12Factory(ClassLoader classLoader) {
        return FactoryFinder.findSOAP12Factory(classLoader);
    }

}
