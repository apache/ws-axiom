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

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.dom.AttrImpl;
import org.apache.axiom.om.impl.dom.DocumentImpl;
import org.apache.axiom.om.impl.dom.ParentNode;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.dom.SOAPElement;

public class SOAP12FaultTextImpl extends SOAPElement implements SOAPFaultText {
    private OMAttribute langAttr;
    private final OMNamespace langNamespace;

    public SOAP12FaultTextImpl(SOAPFaultReason parent, SOAPFactory factory)
            throws SOAPProcessingException {
        super(parent, SOAP12Constants.SOAP_FAULT_TEXT_LOCAL_NAME, true, factory);
        this.langNamespace = factory.createOMNamespace(
                SOAP12Constants.SOAP_FAULT_TEXT_LANG_ATTR_NS_URI,
                SOAP12Constants.SOAP_FAULT_TEXT_LANG_ATTR_NS_PREFIX);
    }

    public SOAP12FaultTextImpl(ParentNode parentNode, OMNamespace ns, OMXMLParserWrapper builder,
            OMFactory factory, boolean generateNSDecl) {
        super(parentNode, SOAP12Constants.SOAP_FAULT_TEXT_LOCAL_NAME, ns, builder, factory, generateNSDecl);
        this.langNamespace = factory.createOMNamespace(
                SOAP12Constants.SOAP_FAULT_TEXT_LANG_ATTR_NS_URI,
                SOAP12Constants.SOAP_FAULT_TEXT_LANG_ATTR_NS_PREFIX);
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP12FaultReasonImpl)) {
            throw new SOAPProcessingException(
                    "Expecting SOAP 1.2 implementation of SOAP FaultReason " +
                            "as the parent. But received some other implementation");
        }
    }

    public void setLang(String lang) {
        langAttr =
                new AttrImpl((DocumentImpl)getOwnerDocument(),
                             SOAP12Constants.SOAP_FAULT_TEXT_LANG_ATTR_LOCAL_NAME,
                             langNamespace,
                             lang, getOMFactory());
        this.addAttribute(langAttr);
    }

    public String getLang() {
        if (langAttr == null) {
            langAttr =
                    this.getAttribute(
                            new QName(langNamespace.getNamespaceURI(),
                                      SOAP12Constants.SOAP_FAULT_TEXT_LANG_ATTR_LOCAL_NAME,
                                      SOAP12Constants.SOAP_FAULT_TEXT_LANG_ATTR_NS_PREFIX));
        }

        return langAttr == null ? null : langAttr.getAttributeValue();
    }

    protected OMElement createClone(OMCloneOptions options, ParentNode targetParent,
            boolean generateNSDecl) {
        return new SOAP12FaultTextImpl(targetParent, getNamespace(), null, getOMFactory(), generateNSDecl);
    }
}