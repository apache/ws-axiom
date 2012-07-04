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
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.dom.ParentNode;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.dom.SOAPBodyImpl;

public class SOAP11BodyImpl extends SOAPBodyImpl {
    /** @param envelope  */
    public SOAP11BodyImpl(SOAPEnvelope envelope, SOAPFactory factory)
            throws SOAPProcessingException {
        super(envelope, factory);
    }

    public SOAP11BodyImpl(ParentNode parentNode, OMNamespace ns, OMXMLParserWrapper builder,
            OMFactory factory, boolean generateNSDecl) {
        super(parentNode, ns, builder, factory, generateNSDecl);
    }

    public SOAPFault addFault(Exception e) throws OMException {
        return new SOAP11FaultImpl(this, e, (SOAPFactory) this.factory);
    }

    protected OMElement createClone(OMCloneOptions options, ParentNode targetParent, boolean generateNSDecl) {
        return new SOAP11BodyImpl(targetParent, namespace, null, factory, generateNSDecl);
    }
}
