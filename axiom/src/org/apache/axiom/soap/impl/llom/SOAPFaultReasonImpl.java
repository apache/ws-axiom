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

package org.apache.axiom.soap.impl.llom;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.util.ElementHelper;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.SOAPProcessingException;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class SOAPFaultReasonImpl extends SOAPElement implements SOAPFaultReason {

    protected SOAPFaultReasonImpl(OMNamespace ns, SOAPFactory factory) {
        super(SOAP12Constants.SOAP_FAULT_REASON_LOCAL_NAME, ns, factory);
    }

    /**
     * Constructor OMElementImpl
     *
     * @param parent
     * @param builder
     */
    public SOAPFaultReasonImpl(SOAPFault parent, OMXMLParserWrapper builder,
                               SOAPFactory factory) {
        super(parent, SOAP12Constants.SOAP_FAULT_REASON_LOCAL_NAME, builder,
                factory);
    }

    /**
     * @param parent
     */
    public SOAPFaultReasonImpl(OMElement parent,
                               boolean extractNamespaceFromParent, SOAPFactory factory)
            throws SOAPProcessingException {
        super(parent,
                SOAP12Constants.SOAP_FAULT_REASON_LOCAL_NAME,
                extractNamespaceFromParent,
                factory);
    }

    public void addSOAPText(SOAPFaultText soapFaultText) throws SOAPProcessingException {
        this.addChild(soapFaultText);
    }

    public SOAPFaultText getFirstSOAPText() {
        return (SOAPFaultText) ElementHelper.getChildWithName(this,
                SOAP12Constants.SOAP_FAULT_TEXT_LOCAL_NAME);
    }

    public List getAllSoapTexts() {
        List faultTexts = new ArrayList(1);
        Iterator childrenIter = this.getChildren();
        while (childrenIter.hasNext()) {
            OMNode node = (OMNode) childrenIter.next();
            if (node.getType() == OMNode.ELEMENT_NODE && (node instanceof SOAPFaultTextImpl)) {
                faultTexts.add(((SOAPFaultTextImpl) node));
            }
        }
        return faultTexts;
    }

    public SOAPFaultText getSOAPFaultText(String language) {
        Iterator childrenIter = this.getChildren();
        while (childrenIter.hasNext()) {
            OMNode node = (OMNode) childrenIter.next();
            if (node.getType() == OMNode.ELEMENT_NODE && (node instanceof SOAPFaultTextImpl) && language.equals(((SOAPFaultTextImpl) node).getLang()))
            {
                return (SOAPFaultText) node;
            }
        }

        return null;
    }

}
