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

package org.apache.axiom.soap.impl.llom.soap11;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.SOAPVersion;
import org.apache.axiom.soap.SOAP11Version;
import org.apache.axiom.soap.impl.common.SOAPHelper;
import org.apache.axiom.soap.impl.llom.SOAPHeaderBlockImpl;

public class SOAP11HeaderBlockImpl extends SOAPHeaderBlockImpl {

    public SOAP11HeaderBlockImpl(OMContainer parent, String localName, OMNamespace ns,
            OMXMLParserWrapper builder, OMFactory factory, boolean generateNSDecl) {
        super(parent, localName, ns, builder, factory, generateNSDecl);
    }

    public SOAP11HeaderBlockImpl(SOAPFactory factory, OMDataSource source) {
        super(factory, source);
    }

    public SOAP11HeaderBlockImpl(String localName, OMNamespace ns,
                                 SOAPFactory factory, 
                                 OMDataSource ds) {
        super(localName, ns, factory, ds);
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP11HeaderImpl)) {
            throw new SOAPProcessingException(
                    "Expecting SOAP11HeaderImpl, got " + parent.getClass());
        }
    }

    public void setRole(String roleURI) {
        setAttribute(SOAP11Constants.ATTR_ACTOR,
                     roleURI,
                     SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);

    }

    public String getRole() {
//      Get the property or attribute
        String val;
        if (this.hasOMDataSourceProperty(ROLE_PROPERTY)) {
            val = this.getOMDataSourceProperty(ROLE_PROPERTY);
        } else {
            val = getAttribute(SOAP11Constants.ATTR_ACTOR,
                               SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
        }
        return val;
    }

    public void setMustUnderstand(boolean mustUnderstand) {
        setAttribute(SOAPConstants.ATTR_MUSTUNDERSTAND,
                     mustUnderstand ? "1" : "0",
                     SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
    }

    public void setRelay(boolean relay) {
        throw new UnsupportedOperationException("Not supported for SOAP 1.1");
    }

    public boolean getRelay() {
        throw new UnsupportedOperationException("Not supported for SOAP 1.1");
    }

    public SOAPVersion getVersion() {
        return SOAP11Version.getSingleton();
    }

    protected SOAPHelper getSOAPHelper() {
        return SOAPHelper.SOAP11;
    }
}
