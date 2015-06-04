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

import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.common.AxiomSOAP12FaultNode;
import org.apache.axiom.soap.impl.llom.SOAPElement;

public class SOAP12FaultNodeImpl extends SOAPElement implements AxiomSOAP12FaultNode {

    public SOAP12FaultNodeImpl(SOAPFactory factory) {
        super(SOAP12Constants.SOAP_FAULT_NODE_LOCAL_NAME, factory.getNamespace(), factory);
    }

    public SOAP12FaultNodeImpl(SOAPFault parent, SOAPFactory factory)
            throws SOAPProcessingException {
        super(parent, SOAP12Constants.SOAP_FAULT_NODE_LOCAL_NAME, true, factory);
    }

    public SOAP12FaultNodeImpl(SOAPFault parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        super(parent, SOAP12Constants.SOAP_FAULT_NODE_LOCAL_NAME, builder, factory);
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP12FaultImpl)) {
            throw new SOAPProcessingException(
                    "Expecting SOAP12FaultImpl, got " + parent.getClass());
        }
    }

    public void setFaultNodeValue(String uri) {
        this.setText(uri);
    }

    public String getFaultNodeValue() {
        return this.getText();
    }

    public void setNodeValue(String uri) {
        setFaultNodeValue(uri);
    }

    public String getNodeValue() {
        return getFaultNodeValue();
    }

    protected OMElement createClone(OMCloneOptions options, OMContainer targetParent) {
        return ((SOAPFactory)getOMFactory()).createSOAPFaultNode((SOAPFault)targetParent);
    }
}
