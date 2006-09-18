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

    private static final String DEFAULT_OM_FACTORY_CLASS_NAME = "org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory";
    private static final String DEFAULT_SOAP11_FACTORY_CLASS_NAME = "org.apache.axiom.soap.impl.llom.soap11.SOAP11Factory";
    private static final String DEFAULT_SOAP12_FACTORY_CLASS_NAME = "org.apache.axiom.soap.impl.llom.soap12.SOAP12Factory";


    public static OMFactory getOMFactory() {

        String omFactory;
        try {
            omFactory = System.getProperty(OM_FACTORY_NAME_PROPERTY);
            if (omFactory == null || "".equals(omFactory)) {
                omFactory = DEFAULT_OM_FACTORY_CLASS_NAME;
            }
        } catch (SecurityException e) {
            // security exception can be thrown when trying to access system variables within a sand box like an applet.
            // please refer : http://issues.apache.org/jira/browse/WSCOMMONS-57
            omFactory = DEFAULT_OM_FACTORY_CLASS_NAME;
        }

        try {
           return (OMFactory) Class.forName(omFactory).newInstance();
        } catch (InstantiationException e) {
            throw new OMException(e);
        } catch (IllegalAccessException e) {
            throw new OMException(e);
        } catch (ClassNotFoundException e) {
            throw new OMException(e);
        }
    }


    /**
     * Gets the default factory implementation from the classpath.
     *
     * @return Returns SOAPFactory.
     */
    public static SOAPFactory getSOAP11Factory() {
        try {
            String omFactory = System.getProperty(SOAP11_FACTORY_NAME_PROPERTY);
            if (omFactory == null || "".equals(omFactory)) {
                omFactory = DEFAULT_SOAP11_FACTORY_CLASS_NAME;
            }
            return (SOAPFactory) Class.forName(omFactory).newInstance();
        } catch (InstantiationException e) {
            throw new OMException(e);
        } catch (IllegalAccessException e) {
            throw new OMException(e);
        } catch (ClassNotFoundException e) {
            throw new OMException(e);
        }
    }


    /**
     * Gets the default factory implementation from the classpath.
     *
     * @return Returns SOAPFactory.
     */
    public static SOAPFactory getSOAP12Factory() {
        try {
            String omFactory = System.getProperty(SOAP12_FACTORY_NAME_PROPERTY);
            if (omFactory == null || "".equals(omFactory)) {
                omFactory = DEFAULT_SOAP12_FACTORY_CLASS_NAME;
            }
            return (SOAPFactory) Class.forName(omFactory).newInstance();
        } catch (InstantiationException e) {
            throw new OMException(e);
        } catch (IllegalAccessException e) {
            throw new OMException(e);
        } catch (ClassNotFoundException e) {
            throw new OMException(e);
        }
    }


}
