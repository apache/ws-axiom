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

package org.apache.axiom.soap.impl.dom.soap12;

import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.dom.ParentNode;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.SOAPVersion;
import org.apache.axiom.soap.SOAP12Version;
import org.apache.axiom.soap.impl.common.SOAPHelper;
import org.apache.axiom.soap.impl.dom.SOAPHeaderBlockImpl;

public class SOAP12HeaderBlockImpl extends SOAPHeaderBlockImpl {
    public SOAP12HeaderBlockImpl(ParentNode parentNode, String localName, OMNamespace ns,
            OMXMLParserWrapper builder, OMFactory factory, boolean generateNSDecl) {
        super(parentNode, localName, ns, builder, factory, generateNSDecl);
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP12HeaderImpl)) {
            throw new SOAPProcessingException(
                    "Expecting SOAP 1.2 implementation of SOAP Body as the parent. But received some other implementation");
        }
    }

    public void setRole(String roleURI) {
        setAttribute(SOAP12Constants.SOAP_ROLE,
                     roleURI,
                     SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
    }

    public String getRole() {
        return getAttribute(SOAP12Constants.SOAP_ROLE,
                            SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);

    }

    public void setMustUnderstand(boolean mustUnderstand) {
        setAttribute(SOAPConstants.ATTR_MUSTUNDERSTAND,
                     mustUnderstand ? SOAPConstants.ATTR_MUSTUNDERSTAND_TRUE :
                             SOAPConstants.ATTR_MUSTUNDERSTAND_FALSE,
                     SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);

    }

    public void setRelay(boolean relay) {
        setAttribute(SOAP12Constants.SOAP_RELAY,
                     String.valueOf(relay),
                     SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
    }

    public boolean getRelay() {
        String val = getAttribute(SOAP12Constants.SOAP_RELAY,
                                  SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
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

    protected OMElement createClone(OMCloneOptions options, ParentNode targetParent, boolean generateNSDecl) {
        SOAPHeaderBlock clone = new SOAP12HeaderBlockImpl(targetParent, getLocalName(), getNamespace(), null, getOMFactory(), generateNSDecl);
        copyData(options, clone);
        return clone;
    }
}
