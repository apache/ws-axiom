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

package org.apache.axiom.om.impl.builder;

import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.MTOMConstants;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.util.ElementHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class XOPAwareStAXOMBuilder 
    extends StAXOMBuilder implements XOPBuilder {
    
    private static final Log log = LogFactory.getLog(XOPAwareStAXOMBuilder.class);

    /** <code>Attachments</code> handles deferred parsing of incoming MIME Messages. */
    Attachments attachments;

    /**
     * Constructor StAXOMBuilder.
     *
     * @param ombuilderFactory
     * @param parser
     */
    public XOPAwareStAXOMBuilder(OMFactory ombuilderFactory,
                                 XMLStreamReader parser, Attachments attachments) {
        super(ombuilderFactory, parser);
        this.attachments = attachments;
    }

    /**
     * Constructor linked to existing element.
     *
     * @param factory
     * @param parser
     * @param element
     */
    public XOPAwareStAXOMBuilder(OMFactory factory, XMLStreamReader parser,
                                 OMElement element, Attachments attachments) {
        super(factory, parser, element);
        this.attachments = attachments;
    }

    /**
     * @param filePath - Path to the XML file
     * @throws XMLStreamException
     * @throws FileNotFoundException
     */
    public XOPAwareStAXOMBuilder(String filePath, Attachments attachments)
            throws XMLStreamException,
            FileNotFoundException {
        super(filePath);
        this.attachments = attachments;
    }

    /**
     * @param inStream - instream which contains the XML
     * @throws XMLStreamException
     */
    public XOPAwareStAXOMBuilder(InputStream inStream, Attachments attachments)
            throws XMLStreamException {
        super(inStream);
        this.attachments = attachments;
    }

    /**
     * Constructor StAXXOPAwareOMBuilder.
     *
     * @param parser
     */
    public XOPAwareStAXOMBuilder(XMLStreamReader parser, Attachments attachments) {
        super(parser);
        this.attachments = attachments;
    }

    /**
     * Method createOMElement. Overriding the createOMElement of StAXOMBuilder to to XOP aware
     * building
     *
     * @return Returns OMNode.
     * @throws OMException
     */
    protected OMNode createOMElement() throws OMException {

        String elementName = parser.getLocalName();
        String namespaceURI = parser.getNamespaceURI();
        if (MTOMConstants.XOP_INCLUDE.equals(elementName)
                && MTOMConstants.XOP_NAMESPACE_URI.equals(namespaceURI)) {
            OMText node;
            String contentID = ElementHelper.getContentID(parser);
            if (log.isDebugEnabled()) {
                log.debug("Encountered xop:include for cid:" + contentID);
            }

            if (lastNode == null) {
                throw new OMException(
                        "XOP:Include element is not supported here");
            } else if (lastNode.isComplete() & lastNode.getParent() != null) {
                node = omfactory.createOMText(contentID, (OMElement) lastNode
                        .getParent(), this);
                if (log.isDebugEnabled()) {
                    log.debug("Create createOMText for cid:" + contentID);
                    Object dh = node.getDataHandler();
                    String dhClass = (dh==null) ? "null" : dh.getClass().toString();
                    log.debug("The datahandler is " + dhClass);
                }
                ((OMNodeEx) lastNode).setNextOMSibling(node);
                ((OMNodeEx) node).setPreviousOMSibling(lastNode);
            } else {
                OMContainerEx e = (OMContainerEx) lastNode;
                node = omfactory.createOMText(contentID, (OMElement) lastNode, this);
                e.setFirstChild(node);
            }
            return node;
        } else {
            return super.createOMElement();
        }
    }

    public DataHandler getDataHandler(String blobContentID) throws OMException {
        return attachments.getDataHandler(blobContentID);
    }
    
    public Attachments getAttachments() {
        return attachments;
    }
}
