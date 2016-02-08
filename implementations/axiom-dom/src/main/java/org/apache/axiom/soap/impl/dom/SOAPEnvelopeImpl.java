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

import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.stream.StreamException;
import org.apache.axiom.om.impl.stream.XmlHandler;
import org.apache.axiom.soap.SOAP11Version;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.intf.AxiomSOAPEnvelope;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public abstract class SOAPEnvelopeImpl extends SOAPElement implements AxiomSOAPEnvelope,
        OMConstants {
    public void addChild(OMNode child, boolean fromBuilder) {
        internalCheckChild(child);

        if (child instanceof SOAPHeader) {
            // The SOAPHeader is added before the SOAPBody
            // We must be sensitive to the state of the parser.  It is possible that the
            // has not been processed yet.
            if (getState() == COMPLETE) {
                // Parsing is complete, therefore it is safe to
                // call getBody.
                SOAPBody body = getBody();
                if (body != null) {
                    body.insertSiblingBefore(child);
                    return;
                }
            } else {
                // Flow to here indicates that we are still expanding the
                // envelope.  The body or body contents may not be
                // parsed yet.  We can't use getBody() yet...it will
                // cause a failure.  So instead, carefully find the
                // body and insert the header.  If the body is not found,
                // this indicates that it has not been parsed yet...and
                // the code will fall through to the super.addChild.
                OMNode node = (OMNode)coreGetLastKnownChild();
                while (node != null) {
                    if (node instanceof SOAPBody) {
                        node.insertSiblingBefore(child);
                        return;
                    }
                    node = node.getPreviousOMSibling();
                }
            }
        }
        super.addChild(child, fromBuilder);
    }

    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        // Check that the child to be added is valid in the context of a SOAP envelope.
        // Note that this also covers the appendChild case, since that method
        // calls insertBefore with refChild == null.
        
        // SOAP 1.1 allows for arbitrary elements after SOAPBody so do NOT check for
        // allowed node types when appending to SOAP 1.1 envelope.
        if (!(getVersion() instanceof SOAP11Version && refChild == null)) {
            checkChild((OMNode)newChild);
        }
        return super.insertBefore(newChild, refChild);
    }

    /**
     * Returns the <CODE>SOAPBody</CODE> object associated with this <CODE>SOAPEnvelope</CODE>
     * object.
     * <p/>
     * This SOAPBody will just be a container for all the BodyElements in the <CODE>OMMessage</CODE>
     * </P>
     *
     * @return the <CODE>SOAPBody</CODE> object for this <CODE> SOAPEnvelope</CODE> object or
     *         <CODE>null</CODE> if there is none
     * @throws org.apache.axiom.om.OMException
     *                     if there is a problem obtaining the <CODE>SOAPBody</CODE> object
     * @throws OMException
     */
    public SOAPBody getBody() throws OMException {
        // check for the first element
        OMElement element = getFirstElement();
        if (element != null) {
            if (SOAPConstants.BODY_LOCAL_NAME.equals(element.getLocalName())) {
                return (SOAPBody) element;
            } else { // if not second element SHOULD be the body
                OMNode node = element.getNextOMSibling();
                while (node != null && node.getType() != OMNode.ELEMENT_NODE) {
                    node = node.getNextOMSibling();
                }
                if (node == null) {
                    // The envelope only contains a header
                    return null;
                } else if (SOAPConstants.BODY_LOCAL_NAME.equals(((OMElement)node).getLocalName())) {
                    return (SOAPBody)node;
                } else {
                    throw new OMException("SOAPEnvelope must contain a body element " +
                            "which is either first or second child element of the SOAPEnvelope.");
                }
            }
        }
        return null;
    }

    public void checkParent(OMElement parent) throws SOAPProcessingException {
        // here do nothing as SOAPEnvelope doesn't have a parent !!!
    }

    public void internalSerialize(XmlHandler handler, OMOutputFormat format, boolean cache)
            throws StreamException {

        if (!format.isIgnoreXMLDeclaration()) {
            String charSetEncoding = format.getCharSetEncoding();
            String xmlVersion = format.getXmlVersion();
            handler.startDocument(
                    null,
                    xmlVersion == null ? OMConstants.DEFAULT_XML_VERSION
                            : xmlVersion,
                    charSetEncoding == null ? OMConstants.DEFAULT_CHAR_SET_ENCODING
                            : charSetEncoding,
                    true);
        }
        super.internalSerialize(handler, format, cache);
        handler.endDocument();
    }
}
