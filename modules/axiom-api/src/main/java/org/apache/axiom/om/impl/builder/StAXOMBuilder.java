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

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.Location;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


/**
 * Constructs an OM without using SOAP specific classes like SOAPEnvelope, SOAPHeader,
 * SOAPHeaderBlock and SOAPBody. This has the document concept also.
 */
public class StAXOMBuilder extends StAXBuilder {
    /** Field document */

    private static final Log log = LogFactory.getLog(StAXOMBuilder.class);
    private boolean doTrace = log.isDebugEnabled();
    private static int nsCount = 0;

    /**
     * Constructor StAXOMBuilder.
     *
     * @param ombuilderFactory
     * @param parser
     */
    public StAXOMBuilder(OMFactory ombuilderFactory, XMLStreamReader parser) {
        super(ombuilderFactory, parser);
        document = ombuilderFactory.createOMDocument(this);
        if (charEncoding != null) {
            document.setCharsetEncoding(charEncoding);
        }
    }

    /**
     * Constructor linked to existing element.
     *
     * @param factory
     * @param parser
     * @param element
     * @param characterEncoding of existing element
     */
    public StAXOMBuilder(OMFactory factory, 
                         XMLStreamReader parser, 
                         OMElement element, 
                         String characterEncoding) {
        // Use this constructor because the parser is passed the START_DOCUMENT state.
        super(factory, parser, characterEncoding);  
        document = factory.createOMDocument(this);
        if (charEncoding != null) {
            document.setCharsetEncoding(charEncoding);
        }
        lastNode = element;
        document.setOMDocumentElement(element);
        populateOMElement(element);
    }
    
    /**
     * Constructor linked to existing element.
     *
     * @param factory
     * @param parser
     * @param element
     */
    public StAXOMBuilder(OMFactory factory, XMLStreamReader parser, OMElement element) {
        this(factory, parser, element, null);
    }

    /**
     * @param filePath - Path to the XML file
     * @throws XMLStreamException
     * @throws FileNotFoundException
     */
    public StAXOMBuilder(String filePath) throws XMLStreamException, FileNotFoundException {
        this(StAXUtils.createXMLStreamReader(new FileInputStream(filePath)));
    }

    /**
     * Constructor StAXOMBuilder.
     *
     * @param parser
     */
    public StAXOMBuilder(XMLStreamReader parser) {
        super(parser);
        omfactory = OMAbstractFactory.getOMFactory();
        document = omfactory.createOMDocument(this);
        if (charEncoding != null) {
            document.setCharsetEncoding(charEncoding);
        }
    }

    /**
     * @param inStream - instream which contains the XML
     * @throws XMLStreamException
     */
    public StAXOMBuilder(InputStream inStream) throws XMLStreamException {
        this(StAXUtils.createXMLStreamReader(inStream));
    }

    /**
     * Init() *must* be called after creating the builder using this constructor.
     */
    public StAXOMBuilder() {
        super();
    }

    /**
     * Method next.
     *
     * @return Returns int.
     * @throws OMException
     */
    public int next() throws OMException {
        try {
            if (done) {
                throw new OMException();
            }
            int token = parser.next();
            if (!cache) {
                return token;
            }
            switch (token) {
                case XMLStreamConstants.START_ELEMENT:
                    if (doTrace) {
                        log.trace(
                                "START_ELEMENT: " + parser.getName() + ":" + parser.getLocalName());
                    }
                    lastNode = createOMElement();
                    break;
                case XMLStreamConstants.START_DOCUMENT:
                    // Document has already being created.

                    document.setXMLVersion(parser.getVersion());
                    document.setCharsetEncoding(parser.getEncoding());
                    document.setStandalone(parser.isStandalone() ? "yes" : "no");
                    if (doTrace) {
                        log.trace("START_DOCUMENT: ");
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    if (doTrace) {
                        log.trace("CHARACTERS: [" + parser.getText() + "]");
                    }
                    lastNode = createOMText(XMLStreamConstants.CHARACTERS);
                    break;
                case XMLStreamConstants.CDATA:
                    if (doTrace) {
                        log.trace("CDATA: [" + parser.getText() + "]");
                    }
                    lastNode = createOMText(XMLStreamConstants.CDATA);
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if (doTrace) {
                        log.trace("END_ELEMENT: " + parser.getName() + ":" + parser.getLocalName());
                    }
                    endElement();
                    break;
                case XMLStreamConstants.END_DOCUMENT:
                    if (doTrace) {
                        log.trace("END_DOCUMENT: ");
                    }
                    done = true;
                    ((OMContainerEx) this.document).setComplete(true);
                    break;
                case XMLStreamConstants.SPACE:
                    if (doTrace) {
                        log.trace("SPACE: [" + parser.getText() + "]");
                    }
                    lastNode = createOMText(XMLStreamConstants.SPACE);
                    break;
                case XMLStreamConstants.COMMENT:
                    if (doTrace) {
                        log.trace("COMMENT: [" + parser.getText() + "]");
                    }
                    createComment();
                    break;
                case XMLStreamConstants.DTD:
                    if (doTrace) {
                        log.trace("DTD: [" + parser.getText() + "]");
                    }
                    createDTD();
                    break;
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                    if (doTrace) {
                        log.trace("PROCESSING_INSTRUCTION: [" + parser.getPITarget() + "][" +
                                parser.getPIData() + "]");
                    }
                    createPI();
                    break;
                case XMLStreamConstants.ENTITY_REFERENCE:
                    if (doTrace) {
                        log.trace("ENTITY_REFERENCE: " + parser.getLocalName() + "[" +
                                parser.getText() + "]");
                    }
                    lastNode = createOMText(XMLStreamConstants.ENTITY_REFERENCE);
                    break;
                default :
                    throw new OMException();
            }
            return token;
        } catch (OMException e) {
            throw e;
        } catch (Exception e) {
            throw new OMException(e);
        }
    }

    /**
     * Populate element with data from parser START_ELEMENT event. This is used when the source of
     * data for an element needs to be parsed on demand. The supplied element must already be set to
     * the proper name and namespace.
     *
     * @param node element to be populated
     */
    private void populateOMElement(OMElement node) {
        // create the namespaces
        processNamespaceData(node);
        // fill in the attributes
        processAttributes(node);
        Location location = parser.getLocation();
        if(location != null) {
            node.setLineNumber(location.getLineNumber());
        }
    }

    /**
     * Method createOMElement.
     *
     * @return Returns OMNode.
     * @throws OMException
     */
    protected OMNode createOMElement() throws OMException {
        OMElement node;
        String elementName = parser.getLocalName();
        if (lastNode == null) {
            node = omfactory.createOMElement(elementName, null, document, this);
        } else if (lastNode.isComplete()) {
            node = omfactory.createOMElement(elementName, null,
                                             lastNode.getParent(), this);
            ((OMNodeEx) lastNode).setNextOMSibling(node);
            ((OMNodeEx) node).setPreviousOMSibling(lastNode);
        } else {
            OMContainerEx e = (OMContainerEx) lastNode;
            node = omfactory.createOMElement(elementName, null,
                                             (OMElement) lastNode, this);
            e.setFirstChild(node);
        }
        populateOMElement(node);
        return node;
    }

    /**
     * Method createOMText.
     *
     * @return Returns OMNode.
     * @throws OMException
     */
    protected OMNode createComment() throws OMException {
        OMNode node;
        if (lastNode == null) {
            node = omfactory.createOMComment(document, parser.getText());
        } else if (lastNode.isComplete()) {
            node = omfactory.createOMComment(lastNode.getParent(), parser.getText());
        } else {
            node = omfactory.createOMComment((OMElement) lastNode, parser.getText());
        }
        return node;
    }

    /**
     * Method createDTD.
     *
     * @return Returns OMNode.
     * @throws OMException
     */
    protected OMNode createDTD() throws OMException {
        if (!parser.hasText())
            return null;
        lastNode = omfactory.createOMDocType(document, parser.getText());
        return lastNode;
    }

    /**
     * Method createPI.
     *
     * @return Returns OMNode.
     * @throws OMException
     */
    protected OMNode createPI() throws OMException {
        OMNode node;
        String target = parser.getPITarget();
        String data = parser.getPIData();
        if (lastNode == null) {
            node = omfactory.createOMProcessingInstruction(document, target, data);
        } else if (lastNode.isComplete()) {
            node = omfactory.createOMProcessingInstruction(lastNode.getParent(), target, data);
        } else if (lastNode instanceof OMText) {
            node = omfactory.createOMProcessingInstruction(lastNode.getParent(), target, data);
        } else {
            node = omfactory.createOMProcessingInstruction((OMContainer) lastNode, target, data);
        }
        return node;
    }

    protected void endElement() {
        if (lastNode.isComplete()) {
            OMNodeEx parent = (OMNodeEx) lastNode.getParent();
            parent.setComplete(true);
            lastNode = parent;
        } else {
            OMNodeEx e = (OMNodeEx) lastNode;
            e.setComplete(true);
        }

        //return lastNode;
    }

    /**
     * Method getDocumentElement.
     *
     * @return Returns root element.
     */
    public OMElement getDocumentElement() {
        return document.getOMDocumentElement();
    }

    /**
     * Method processNamespaceData.
     *
     * @param node
     */
    protected void processNamespaceData(OMElement node) {
        // set the own namespace
        String namespaceURI = parser.getNamespaceURI();
        String prefix = parser.getPrefix();


        int namespaceCount = parser.getNamespaceCount();
        String nsprefix;
        String namespaceURIFromParser;
        for (int i = 0; i < namespaceCount; i++) {
            nsprefix = parser.getNamespacePrefix(i);

            //if the namespace is not defined already when we write the start tag declare it
            // check whether this is the default namespace and make sure we have not declared that earlier
            namespaceURIFromParser = parser.getNamespaceURI(i);
            if (nsprefix == null || "".equals(nsprefix)) {
                node.declareDefaultNamespace(parser.getNamespaceURI(i));
            } else {
                node.declareNamespace(namespaceURIFromParser.intern(), nsprefix);
            }
        }

        if (namespaceURI != null && namespaceURI.length() > 0) {
            OMNamespace namespace = node.findNamespace(namespaceURI, prefix);
            if (namespace == null || (!namespace.getPrefix().equals(prefix))) {
                if (prefix == null || "".equals(prefix)) {
                    namespace = node.declareDefaultNamespace(namespaceURI.intern());
                } else {
                    namespace = node.declareNamespace(namespaceURI.intern(), prefix);
                }
            }
            node.setNamespaceWithNoFindInCurrentScope(namespace);
        }
    }

    /**
     * @param doDebug
     * @deprecated
     */
    public void setDoDebug(boolean doDebug) {
        this.doTrace = doDebug;
    }

    protected String createPrefix() {
        return "ns" + nsCount++;
    }

}
