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
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.common.AxiomSOAP11Fault;
import org.apache.axiom.soap.impl.dom.SOAPFaultImpl;

public class SOAP11FaultImpl extends SOAPFaultImpl implements AxiomSOAP11Fault {
    public SOAP11FaultImpl(SOAPBody parent, Exception e, SOAPFactory factory)
            throws SOAPProcessingException {
        super(parent, e, factory);
    }

    public SOAP11FaultImpl(ParentNode parentNode, OMNamespace ns, OMXMLParserWrapper builder,
            OMFactory factory, boolean generateNSDecl) {
        super(parentNode, ns, builder, factory, generateNSDecl);
    }

    /**
     * This is a convenience method for the SOAP Fault Impl.
     *
     * @param parent
     */
    public SOAP11FaultImpl(SOAPBody parent, SOAPFactory factory)
            throws SOAPProcessingException {
        super(parent, factory);
    }

    protected SOAPFaultDetail getNewSOAPFaultDetail(SOAPFault fault)
            throws SOAPProcessingException {
        return new SOAP11FaultDetailImpl(fault, (SOAPFactory)getOMFactory());
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP11BodyImpl)) {
            throw new SOAPProcessingException(
                    "Expecting SOAP 1.1 implementation of SOAP Body as the " +
                            "parent. But received some other implementation");
        }
    }

    protected OMElement createClone(OMCloneOptions options, ParentNode targetParent,
            boolean generateNSDecl) {
        SOAPFault clone = new SOAP11FaultImpl(targetParent, getNamespace(), null, getOMFactory(), generateNSDecl);
        if (e != null) {
            clone.setException(e);
        }
        return clone;
    }
}
