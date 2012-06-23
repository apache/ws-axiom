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

package org.apache.axiom.soap.impl.dom;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.dom.ParentNode;
import org.apache.axiom.soap.RolePlayer;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.common.HeaderIterator;
import org.apache.axiom.soap.impl.common.MURoleChecker;
import org.apache.axiom.soap.impl.common.RoleChecker;
import org.apache.axiom.soap.impl.common.RolePlayerChecker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class SOAPHeaderImpl extends SOAPElement implements SOAPHeader {
    /** @param envelope  */
    public SOAPHeaderImpl(SOAPEnvelope envelope, SOAPFactory factory)
            throws SOAPProcessingException {
        super(envelope, SOAPConstants.HEADER_LOCAL_NAME, true, factory);

    }

    public SOAPHeaderImpl(ParentNode parentNode, OMNamespace ns,
            OMXMLParserWrapper builder, OMFactory factory, boolean generateNSDecl) {
        super(parentNode, SOAPConstants.HEADER_LOCAL_NAME, ns, builder, factory, generateNSDecl);
    }

    public SOAPHeaderBlock addHeaderBlock(String localName, OMNamespace ns)
            throws OMException {
        
        if (ns == null || ns.getNamespaceURI().length() == 0) {
            throw new OMException(
                    "All the SOAP Header blocks should be namespace qualified");
        }
        
        OMNamespace namespace = findNamespace(ns.getNamespaceURI(), ns.getPrefix());
        if (namespace != null) {
            ns = namespace;
        }
        
        SOAPHeaderBlock soapHeaderBlock;
        try {
            soapHeaderBlock = ((SOAPFactory)factory).createSOAPHeaderBlock(localName, ns, this);
        } catch (SOAPProcessingException e) {
            throw new OMException(e);
        }
        ((OMNodeEx) soapHeaderBlock).setComplete(true);
        return soapHeaderBlock;
    }

    public Iterator getHeadersToProcess(RolePlayer rolePlayer) {
        return new HeaderIterator(this, new RolePlayerChecker(rolePlayer));
    }

    public Iterator getHeadersToProcess(RolePlayer rolePlayer, String namespace) {
        return new HeaderIterator(this, new RolePlayerChecker(rolePlayer, namespace));
    }

    public Iterator examineHeaderBlocks(String role) {
        return new HeaderIterator(this, new RoleChecker(role));
    }

    public abstract Iterator extractHeaderBlocks(String role);

    public Iterator examineMustUnderstandHeaderBlocks(String actor) {
        return new HeaderIterator(this, new MURoleChecker(actor));
    }

    public Iterator examineAllHeaderBlocks() {
        return this.getChildrenWithName(null);
    }

    public Iterator extractAllHeaderBlocks() {
        List result = new ArrayList();
        for (Iterator iter = getChildElements(); iter.hasNext();) {
            OMElement headerBlock = (OMElement) iter.next();
            iter.remove();
            result.add(headerBlock);
        }
        return result.iterator();
    }

    public ArrayList getHeaderBlocksWithNSURI(String nsURI) {
        ArrayList headers = null;
        OMNode node;
        OMElement header = this.getFirstElement();

        if (header != null) {
            headers = new ArrayList();
        }

        node = header;

        while (node != null) {
            if (node.getType() == OMNode.ELEMENT_NODE) {
                header = (OMElement) node;
                OMNamespace namespace = header.getNamespace();
                if (nsURI == null) {
                    if (namespace == null) {
                        headers.add(header);
                    }
                } else {
                    if (namespace != null) {
                        if (nsURI.equals(namespace.getNamespaceURI())) {
                            headers.add(header);
                        }
                    }
                }
            }
            node = node.getNextOMSibling();

        }
        return headers;

    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAPEnvelopeImpl)) {
            throw new SOAPProcessingException(
                    "Expecting an implementation of SOAP Envelope as the " +
                            "parent. But received some other implementation");
        }
    }
}
