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

package org.apache.axiom.soap.impl.dom.soap11;

import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.dom.ParentNode;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.SOAPVersion;
import org.apache.axiom.soap.SOAP11Version;
import org.apache.axiom.soap.impl.common.SOAPHelper;
import org.apache.axiom.soap.impl.dom.SOAPHeaderBlockImpl;

public class SOAP11HeaderBlockImpl extends SOAPHeaderBlockImpl {
    public SOAP11HeaderBlockImpl(ParentNode parentNode, String localName, OMNamespace ns,
            OMXMLParserWrapper builder, OMFactory factory, boolean generateNSDecl) {
        super(parentNode, localName, ns, builder, factory, generateNSDecl);
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP11HeaderImpl)) {
            throw new SOAPProcessingException(
                    "Expecting SOAP 1.1 implementation of SOAP Body as the parent. But received some other implementation");
        }
    }

    public void setRole(String roleURI) {
        setAttribute(SOAP11Constants.ATTR_ACTOR,
                     roleURI,
                     SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);

    }

    public String getRole() {
        return getAttribute(SOAP11Constants.ATTR_ACTOR,
                            SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
    }

    //TODO : implement
    public void setRelay(boolean relay) {
        throw new UnsupportedOperationException("Not supported for SOAP 1.1");
    }

    //TODO : implement
    public boolean getRelay() {
        throw new UnsupportedOperationException("Not supported for SOAP 1.1");
    }


    public void setMustUnderstand(boolean mustUnderstand) {
        setAttribute(SOAPConstants.ATTR_MUSTUNDERSTAND,
                     mustUnderstand ? "1" : "0",
                     SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI);
    }

    /**
     * What SOAP version is this HeaderBlock?
     *
     * @return a SOAPVersion, one of the two singletons.
     */
    public SOAPVersion getVersion() {
        return SOAP11Version.getSingleton();
    }

    protected SOAPHelper getSOAPHelper() {
        return SOAPHelper.SOAP11;
    }

    protected OMElement createClone(OMCloneOptions options, ParentNode targetParent, boolean generateNSDecl) {
        SOAPHeaderBlock clone = new SOAP11HeaderBlockImpl(targetParent, getLocalName(), getNamespace(), null, getOMFactory(), generateNSDecl);
        copyData(options, clone);
        return clone;
    }
}
