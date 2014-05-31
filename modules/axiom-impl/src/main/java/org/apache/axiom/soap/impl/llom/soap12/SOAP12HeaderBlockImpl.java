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

package org.apache.axiom.soap.impl.llom.soap12;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.SOAPVersion;
import org.apache.axiom.soap.SOAP12Version;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.impl.common.SOAPHelper;
import org.apache.axiom.soap.impl.llom.SOAPHeaderBlockImpl;

public class SOAP12HeaderBlockImpl extends SOAPHeaderBlockImpl implements SOAP12Constants {

    public SOAP12HeaderBlockImpl(OMContainer parent, String localName, OMNamespace ns,
            OMXMLParserWrapper builder, OMFactory factory, boolean generateNSDecl) {
        super(parent, localName, ns, builder, factory, generateNSDecl);
    }

    public SOAP12HeaderBlockImpl(SOAPFactory factory, OMDataSource source) {
        super(factory, source);
    }

    public SOAP12HeaderBlockImpl(String localName, OMNamespace ns,
                                 SOAPFactory factory, OMDataSource ds) {
        super(localName, ns, factory, ds);
    }
    
    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP12HeaderImpl)) {
            throw new SOAPProcessingException(
                    "Expecting SOAP12HeaderImpl as parent, got " + parent.getClass());
        }
    }

    public void setRole(String roleURI) {
        setAttribute(SOAP_ROLE, roleURI, SOAP_ENVELOPE_NAMESPACE_URI);
    }

    public String getRole() {
        // Get the property or attribute
        String val;
        if (this.hasOMDataSourceProperty(ROLE_PROPERTY)) {
            val = this.getOMDataSourceProperty(ROLE_PROPERTY);
        } else {
            val = getAttributeValue(QNAME_ROLE);
        }
       return val;
    }

    public void setMustUnderstand(boolean mustUnderstand) {
        setAttribute(SOAPConstants.ATTR_MUSTUNDERSTAND,
                     mustUnderstand ? SOAPConstants.ATTR_MUSTUNDERSTAND_TRUE :
                             SOAPConstants.ATTR_MUSTUNDERSTAND_FALSE,
                     SOAP_ENVELOPE_NAMESPACE_URI);

    }

    public void setRelay(boolean relay) {
        setAttribute(SOAP_RELAY, relay ? "true" : "false", SOAP_ENVELOPE_NAMESPACE_URI);
    }

    public boolean getRelay() {
        // Get the property or attribute
        String val;
        if (this.hasOMDataSourceProperty(RELAY_PROPERTY)) {
            val = this.getOMDataSourceProperty(RELAY_PROPERTY);
        } else {
            val = getAttributeValue(QNAME_RELAY);
        }
        
        if (val != null) {
            Boolean parsedValue = SOAPHelper.SOAP12.parseBoolean(val);
            if (parsedValue == null) {
                throw new SOAPProcessingException("Invalid relay attribute value");
            } else {
                return parsedValue.booleanValue();
            }
        } else {
            return false;
        }
    }

    /**
     * What SOAP version is this HeaderBlock?
     *
     * @return a SOAPVersion, one of the two singletons.
     */
    public SOAPVersion getVersion() {
        return SOAP12Version.getSingleton();
    }

    protected SOAPHelper getSOAPHelper() {
        return SOAPHelper.SOAP12;
    }
}
