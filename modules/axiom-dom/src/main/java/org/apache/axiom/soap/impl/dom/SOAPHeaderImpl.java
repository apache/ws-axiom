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
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.dom.ElementImpl;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.RolePlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public abstract class SOAPHeaderImpl extends SOAPElement implements SOAPHeader {


    /** @param envelope  */
    public SOAPHeaderImpl(SOAPEnvelope envelope, SOAPFactory factory)
            throws SOAPProcessingException {
        super(envelope, SOAPConstants.HEADER_LOCAL_NAME, true, factory);

    }

    /**
     * Constructor SOAPHeaderImpl
     *
     * @param envelope
     * @param builder
     */
    public SOAPHeaderImpl(SOAPEnvelope envelope, OMXMLParserWrapper builder,
                          SOAPFactory factory) {
        super(envelope, SOAPConstants.HEADER_LOCAL_NAME, builder, factory);
    }

    public abstract SOAPHeaderBlock addHeaderBlock(String localName,
                                                   OMNamespace ns)
            throws OMException;

    public Iterator getHeadersToProcess(RolePlayer rolePlayer) {
        return null; // TODO: Implement this!
    }

    public Iterator getHeadersToProcess(RolePlayer rolePlayer, String namespace) {
        return null; // TODO: Implement this!    
    }

    public Iterator examineHeaderBlocks(String paramRole) {
        /* Iterator headerBlocksIter = this.getChildren();
       ArrayList headersWithGivenActor = new ArrayList();

       if (paramRole == null || "".equals(paramRole)) {
           return returnAllSOAPHeaders(this.getChildren());
       }

       while (headerBlocksIter.hasNext()) {
           Object o = headerBlocksIter.next();
           if (o instanceof SOAPHeaderBlock) {
               SOAPHeaderBlock soapHeaderBlock = (SOAPHeaderBlock) o;
               String role = soapHeaderBlock.getRole();
               if ((role != null) && role.equalsIgnoreCase(paramRole)) {
                   headersWithGivenActor.add(soapHeaderBlock);
               }
           }
       }
       return headersWithGivenActor.iterator();*/

        if (paramRole == null || paramRole.trim().length() == 0) {
            return examineAllHeaderBlocks();
        }
        Collection elements = new ArrayList();
        for (Iterator iter = examineAllHeaderBlocks(); iter.hasNext();) {
            SOAPHeaderBlock headerBlock = (SOAPHeaderBlock) iter.next();
            /*
            if (headerBlock.getRole() == null ||
                headerBlock.getRole().trim().length() == 0 ||
                headerBlock.getRole().equals(paramRole)) {
                elements.add(headerBlock);
            }
            */
            if (headerBlock.getRole() != null &&
                    headerBlock.getRole().trim().length() > 0 &&
                    headerBlock.getRole().equals(paramRole)) {
                elements.add(headerBlock);
            }

        }
        return elements.iterator();
    }

//    private Iterator returnAllSOAPHeaders(Iterator children) {
//        ArrayList headers = new ArrayList();
//        while (children.hasNext()) {
//            Object o = children.next();
//            if (o instanceof SOAPHeaderBlock) {
//                headers.add(o);
//            }
//        }
//
//        return headers.iterator();
//
//    }

    public abstract Iterator extractHeaderBlocks(String role);

    public Iterator examineMustUnderstandHeaderBlocks(String actor) {
        Iterator headerBlocksIter = this.getChildren();
        ArrayList mustUnderstandHeadersWithGivenActor = new ArrayList();
        while (headerBlocksIter.hasNext()) {
            Object o = headerBlocksIter.next();
            if (o instanceof SOAPHeaderBlock) {
                SOAPHeaderBlock soapHeaderBlock = (SOAPHeaderBlock) o;
                String role = soapHeaderBlock.getRole();
                boolean mustUnderstand = soapHeaderBlock.getMustUnderstand();
                if ((role != null) && role.equals(actor) && mustUnderstand) {
                    mustUnderstandHeadersWithGivenActor.add(soapHeaderBlock);
                }
            }
        }
        return mustUnderstandHeadersWithGivenActor.iterator();
    }

    public Iterator examineAllHeaderBlocks() {
        return this.getChildrenWithName(null);
    }

    public Iterator extractAllHeaderBlocks() {
        Collection result = new ArrayList();
        for (Iterator iter = getChildrenWithName(null); iter.hasNext();) {
            ElementImpl headerBlock = (ElementImpl) iter.next();
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
