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

package org.apache.axiom.soap.impl.builder;

import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.MTOMConstants;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.builder.XOPBuilder;
import org.apache.axiom.om.util.ElementHelper;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamReader;

public class MTOMStAXSOAPModelBuilder extends StAXSOAPModelBuilder implements
        MTOMConstants, XOPBuilder {
    
    private static final Log log = LogFactory.getLog(MTOMStAXSOAPModelBuilder.class);

    /** <code>Attachments</code> handles deferred parsing of incoming MIME Messages. */
    Attachments attachments;

    int partIndex = 0;

    public MTOMStAXSOAPModelBuilder(XMLStreamReader parser,
                                    SOAPFactory factory, Attachments attachments,
                                    String soapVersion) {
        super(parser, factory, soapVersion);
        this.attachments = attachments;
    }

    /**
     * @param reader
     * @param attachments
     */
    public MTOMStAXSOAPModelBuilder(XMLStreamReader reader,
                                    Attachments attachments, String soapVersion) {
        super(reader, soapVersion);
        this.attachments = attachments;
    }

    public MTOMStAXSOAPModelBuilder(XMLStreamReader reader,
                                    Attachments attachments) {
        super(reader);
        this.attachments = attachments;
    }

    protected OMNode createOMElement() throws OMException {

        String elementName = parser.getLocalName();
        String namespaceURI = parser.getNamespaceURI();

        // create an OMBlob if the element is an <xop:Include>
        if (XOP_INCLUDE.equals(elementName) && XOP_NAMESPACE_URI.equals(namespaceURI)) {
            OMText node;
            String contentID = ElementHelper.getContentID(parser);
            
            if (log.isDebugEnabled()) {
                log.debug("Encountered xop:include for cid:" + contentID);
            }

            if (lastNode == null) {
                throw new OMException(
                        "XOP:Include element is not supported here");
            } else if (lastNode.isComplete() & lastNode.getParent() != null) {
                node = omfactory.createOMText(contentID, lastNode.getParent(), this);
                ((OMNodeEx) lastNode).setNextOMSibling(node);
                ((OMNodeEx) node).setPreviousOMSibling(lastNode);
                if (log.isDebugEnabled()) {
                    log.debug("Create createOMText for cid:" + contentID);
                    Object dh = node.getDataHandler();
                    String dhClass = (dh==null) ? "null" : dh.getClass().toString();
                    log.debug("The datahandler is " + dhClass);
                }
            } else {
                OMContainerEx e = (OMContainerEx) lastNode;
                node = omfactory.createOMText(contentID, (OMElement) lastNode,
                                              this);
                e.setFirstChild(node);
            }
            return node;

        } else {
            return super.createOMElement();
        }
    }

    /* (non-Javadoc)
      * @see org.apache.axiom.soap.impl.builder.XOPBuilder#getDataHandler(java.lang.String)
      */
    public DataHandler getDataHandler(String blobContentID) throws OMException {
        DataHandler dataHandler = attachments.getDataHandler(blobContentID);
        /* The getDataHandler javadoc indicates that null indicate that the datahandler
         * was not found
         * 
        if (dataHandler == null) {
            throw new OMException(
                    "Referenced Attachment not found in the MIME Message. ContentID:"
                            + blobContentID);
        }
        */
        return dataHandler;
    }
    
    public Attachments getAttachments() {
        return attachments;
    }
}
