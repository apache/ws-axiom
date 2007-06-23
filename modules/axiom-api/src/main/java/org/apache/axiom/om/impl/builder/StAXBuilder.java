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

package org.apache.axiom.om.impl.builder;

import java.io.InputStream;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.util.OMSerializerUtil;
import org.apache.axiom.om.util.StAXUtils;

/**
 * OM should be able to be built from any data source. And the model it builds may be a SOAP
 * specific one or just an XML model. This class will give some common functionality of OM Building
 * from StAX.
 */
public abstract class StAXBuilder implements OMXMLParserWrapper {

    /** Field parser */
    protected XMLStreamReader parser;

    /** Field omfactory */
    protected OMFactory omfactory;

    /** Field lastNode */
    protected OMNode lastNode;

    // returns the state of completion

    /** Field done */
    protected boolean done = false;

    // keeps the state of the cache

    /** Field cache */
    protected boolean cache = true;

    // keeps the state of the parser access. if the parser is
    // accessed atleast once,this flag will be set

    /** Field parserAccessed */
    protected boolean parserAccessed = false;
    protected OMDocument document;

    protected String charEncoding = null;
    /**
     * Constructor StAXBuilder.
     *
     * @param ombuilderFactory
     * @param parser
     */
    protected StAXBuilder(OMFactory ombuilderFactory, XMLStreamReader parser) {
        this.parser = parser;
        omfactory = ombuilderFactory;
        charEncoding = parser.getCharacterEncodingScheme();

        if (parser instanceof BuilderAwareReader) {
            ((BuilderAwareReader) parser).setBuilder(this);
        }
    }

    /**
     * Constructor StAXBuilder.
     *
     * @param parser
     */
    protected StAXBuilder(XMLStreamReader parser) {
        this(OMAbstractFactory.getOMFactory(), parser);
    }

    /** Init() *must* be called after creating the builder using this constructor. */
    protected StAXBuilder() {
    }

    /**
     * @deprecated Not used anywhere
     */
    public void init(InputStream inputStream, String charSetEncoding, String url,
                     String contentType) throws OMException {
        try {
            this.parser = StAXUtils.createXMLStreamReader(inputStream);
        } catch (XMLStreamException e1) {
            throw new OMException(e1);
        }
        omfactory = OMAbstractFactory.getOMFactory();
    }

    /**
     * Method setOMBuilderFactory.
     *
     * @param ombuilderFactory
     */
    public void setOMBuilderFactory(OMFactory ombuilderFactory) {
        this.omfactory = ombuilderFactory;
    }

    /**
     * Method processNamespaceData.
     *
     * @param node
     */
    protected abstract void processNamespaceData(OMElement node);

    // since the behaviors are different when it comes to namespaces
    // this must be implemented differently

    /**
     * Method processAttributes.
     *
     * @param node
     */
    protected void processAttributes(OMElement node) {
        int attribCount = parser.getAttributeCount();
        for (int i = 0; i < attribCount; i++) {
            String uri = parser.getAttributeNamespace(i);
            String prefix = parser.getAttributePrefix(i);


            OMNamespace namespace = null;
            if (uri != null && uri.length() > 0) {

                // prefix being null means this elements has a default namespace or it has inherited
                // a default namespace from its parent
                namespace = node.findNamespace(uri, prefix);
                if (namespace == null) {
                    if (prefix == null || "".equals(prefix)) {
                        prefix = OMSerializerUtil.getNextNSPrefix();
                    }
                    namespace = node.declareNamespace(uri, prefix);
                }
            }

            // todo if the attributes are supposed to namespace qualified all the time
            // todo then this should throw an exception here

            node.addAttribute(parser.getAttributeLocalName(i),
                              parser.getAttributeValue(i), namespace);
        }
    }

    /**
     * Method createOMText.
     *
     * @return Returns OMNode.
     * @throws OMException
     */
    protected OMNode createOMText(int textType) throws OMException {
        OMNode node;
        if (lastNode == null) {
            return null;
        } else if (!lastNode.isComplete()) {
            node = createOMText((OMElement) lastNode, textType);
        } else {
            node = createOMText(lastNode.getParent(), textType);
        }
        return node;
    }

    /**
     * This method will check whether the text can be optimizable using IS_BINARY flag. If that is
     * set then we try to get the data handler.
     *
     * @param omContainer
     * @param textType
     * @return omNode
     */
    private OMNode createOMText(OMContainer omContainer, int textType) {
        try {
            if (isDataHandlerAware(parser) &&
                    Boolean.TRUE.equals(parser.getProperty(OMConstants.IS_BINARY))) {
                Object dataHandler = parser.getProperty(OMConstants.DATA_HANDLER);
                OMText text = omfactory.createOMText(dataHandler, true);
                omContainer.addChild(text);
                return text;
            } else {
                return omfactory.createOMText(omContainer, parser.getText(), textType);
            }
        } catch (IllegalArgumentException e) {
            return omfactory.createOMText(omContainer, parser.getText(), textType);
        }
    }

    /**
     * Method reset.
     *
     * @param node
     * @throws OMException
     */
    public void reset(OMNode node) throws OMException {
        lastNode = null;
    }

    /**
     * Method discard.
     *
     * @param el
     * @throws OMException
     */
    public void discard(OMElement el) throws OMException {
        OMElement element = null;

        if (element.isComplete() || !cache) {
            throw new OMException();
        }
        try {
            cache = false;
            do {
                while (parser.next() != XMLStreamConstants.END_ELEMENT) ;

                // TODO:
            } while (!parser.getName().equals(element.getQName()));
            lastNode = element.getPreviousOMSibling();
            if (lastNode != null) {
                ((OMNodeEx) lastNode).setNextOMSibling(null);
            } else {
                OMElement parent = (OMElement) element.getParent();
                if (parent == null) {
                    throw new OMException();
                }
                ((OMContainerEx) parent).setFirstChild(null);
                lastNode = parent;
            }
            cache = true;
        } catch (OMException e) {
            throw e;
        } catch (Exception e) {
            throw new OMException(e);
        }
    }

    /**
     * Method getText.
     *
     * @return Returns String.
     * @throws OMException
     */
    public String getText() throws OMException {
        return parser.getText();
    }

    /**
     * Method getNamespace.
     *
     * @return Returns String.
     * @throws OMException
     */
    public String getNamespace() throws OMException {
        return parser.getNamespaceURI();
    }

    /**
     * Method getNamespaceCount.
     *
     * @return Returns int.
     * @throws OMException
     */
    public int getNamespaceCount() throws OMException {
        try {
            return parser.getNamespaceCount();
        } catch (Exception e) {
            throw new OMException(e);
        }
    }

    /**
     * Method getNamespacePrefix.
     *
     * @param index
     * @return Returns String.
     * @throws OMException
     */
    public String getNamespacePrefix(int index) throws OMException {
        try {
            return parser.getNamespacePrefix(index);
        } catch (Exception e) {
            throw new OMException(e);
        }
    }

    /**
     * Method getNamespaceUri.
     *
     * @param index
     * @return Returns String.
     * @throws OMException
     */
    public String getNamespaceUri(int index) throws OMException {
        try {
            return parser.getNamespaceURI(index);
        } catch (Exception e) {
            throw new OMException(e);
        }
    }

    /**
     * Method setCache.
     *
     * @param b
     */
    public void setCache(boolean b) {
        if (parserAccessed && b) {
            throw new UnsupportedOperationException(
                    "parser accessed. cannot set cache");
        }
        cache = b;
    }

    /**
     * Method getName.
     *
     * @return Returns String.
     * @throws OMException
     */
    public String getName() throws OMException {
        return parser.getLocalName();
    }

    /**
     * Method getPrefix.
     *
     * @return Returns String.
     * @throws OMException
     */
    public String getPrefix() throws OMException {
        return parser.getPrefix();
    }

    /**
     * Method getAttributeCount.
     *
     * @return Returns int.
     * @throws OMException
     */
    public int getAttributeCount() throws OMException {
        return parser.getAttributeCount();
    }

    /**
     * Method getAttributeNamespace.
     *
     * @param arg
     * @return Returns String.
     * @throws OMException
     */
    public String getAttributeNamespace(int arg) throws OMException {
        return parser.getAttributeNamespace(arg);
    }

    /**
     * Method getAttributeName.
     *
     * @param arg
     * @return Returns String.
     * @throws OMException
     */
    public String getAttributeName(int arg) throws OMException {
        return parser.getAttributeNamespace(arg);
    }

    /**
     * Method getAttributePrefix.
     *
     * @param arg
     * @return Returns String.
     * @throws OMException
     */
    public String getAttributePrefix(int arg) throws OMException {
        return parser.getAttributeNamespace(arg);
    }

    /**
     * Method getParser.
     *
     * @return Returns Object.
     */
    public Object getParser() {
        if (parserAccessed) {
            throw new IllegalStateException(
                    "Parser already accessed!");
        }
        if (!cache) {
            parserAccessed = true;
            return parser;
        } else {
            throw new IllegalStateException(
                    "cache must be switched off to access the parser");
        }
    }

    /**
     * Method isCompleted.
     *
     * @return Returns boolean.
     */
    public boolean isCompleted() {
        return done;
    }

    /**
     * This method is called with the XMLStreamConstants.START_ELEMENT event.
     *
     * @return Returns OMNode.
     * @throws OMException
     */
    protected abstract OMNode createOMElement() throws OMException;

    /**
     * Forwards the parser one step further, if parser is not completed yet. If this is called after
     * parser is done, then throw an OMException. If the cache is set to false, then returns the
     * event, *without* building the OM tree. If the cache is set to true, then handles all the
     * events within this, and builds the object structure appropriately and returns the event.
     *
     * @return Returns int.
     * @throws OMException
     */
    public abstract int next() throws OMException;

    /** @return Returns short. */
    public short getBuilderType() {
        return OMConstants.PULL_TYPE_BUILDER;
    }

    /**
     * Method registerExternalContentHandler.
     *
     * @param obj
     */
    public void registerExternalContentHandler(Object obj) {
        throw new UnsupportedOperationException();
    }

    /**
     * Method getRegisteredContentHandler.
     *
     * @return Returns Object.
     */
    public Object getRegisteredContentHandler() {
        throw new UnsupportedOperationException();
    }

    public OMDocument getDocument() {
        return document;
    }

    public String getCharsetEncoding() {
        return document.getCharsetEncoding();
    }

    public OMNode getLastNode() {
        return this.lastNode;
    }

    public void close() {
        try {
            parser.close();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the value of a feature/property from the underlying XMLStreamReader implementation
     * without accessing the XMLStreamReader. https://issues.apache.org/jira/browse/WSCOMMONS-155
     *
     * @param name
     * @return
     */
    public Object getReaderProperty(String name) throws IllegalArgumentException {
        return parser.getProperty(name);
    }

    /**
     * Check if the underlying parse is aware of data handlers. (example ADB generated code)
     *
     * @param parser
     * @return
     */
    private boolean isDataHandlerAware(XMLStreamReader parser) {
        // check whether data handlers are treated seperately
        try {
            if (parser != null &&
                    (Boolean.TRUE == parser.getProperty(OMConstants.IS_DATA_HANDLERS_AWARE))) {
                return true;
            }
        } catch (IllegalArgumentException e) {
            // according to the parser api, get property will return IllegalArgumentException, when that
            // property is not found.
        }
        return false;
    }

    /**
     * Returns the encoding style of the XML data
     * @return the character encoding, defaults to "UTF-8"
     */
    public String getCharacterEncoding() {
        if(this.charEncoding == null){
            return "UTF-8";
        }
        return this.charEncoding;
    }
}
