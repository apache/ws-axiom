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

package org.apache.axiom.util.stax.xop;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.activation.DataHandler;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.util.base64.Base64Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * {@link XMLStreamReader} wrapper that decodes XOP. It uses the extension defined by
 * {@link DataHandlerReader} to expose the {@link DataHandler} objects referenced by
 * <tt>xop:Include</tt> elements encountered in the underlying stream. If the consumer uses
 * {@link #getText()}, {@link #getTextCharacters()},
 * {@link #getTextCharacters(int, char[], int, int)} or {@link #getElementText()} when an
 * <tt>xop:Include</tt> element is present in the underlying stream, then the decoder will produce
 * a base64 representation of the data.
 * <p>
 * Note that this class only implements infoset transformation, but doesn't handle MIME processing.
 * A {@link MimePartProvider} implementation must be provided to the constructor of this class. This
 * object will be used to load MIME parts referenced by <tt>xop:Include</tt> elements encountered
 * in the underlying stream.
 * <p>
 * This class supports deferred loading of MIME parts: If the consumer uses
 * {@link DataHandlerReader#getDataHandlerProvider()}, then the {@link MimePartProvider} will only
 * be invoked when {@link DataHandlerProvider#getDataHandler()} is called.
 */
public class XOPDecodingStreamReader implements XMLStreamReader, DataHandlerReader {
    private static final String SOLE_CHILD_MSG =
            "Expected xop:Include as the sole child of an element information item (see section " +
            "3.2 of http://www.w3.org/TR/xop10/)";
    
    private static class DataHandlerProviderImpl implements DataHandlerProvider {
        private final MimePartProvider mimePartProvider;
        private final String contentID;
        
        public DataHandlerProviderImpl(MimePartProvider mimePartProvider, String contentID) {
            this.mimePartProvider = mimePartProvider;
            this.contentID = contentID;
        }

        public String getContentID() {
            return contentID;
        }

        public DataHandler getDataHandler() throws XMLStreamException {
            return mimePartProvider.getMimePart(contentID);
        }
    }
    
    private static final Log log = LogFactory.getLog(XOPDecodingStreamReader.class);
    
    private final XMLStreamReader parent;
    private final MimePartProvider mimePartProvider;
    private DataHandlerProviderImpl dh;
    private String base64;

    /**
     * Constructor.
     * 
     * @param parent
     *            the XML stream to decode
     * @param mimePartProvider
     *            An implementation of the {@link MimePartProvider} interface that will be used to
     *            load the {@link DataHandler} objects for MIME parts referenced by
     *            <tt>xop:Include</tt> element information items encountered in the underlying
     *            stream.
     */
    public XOPDecodingStreamReader(XMLStreamReader parent, MimePartProvider mimePartProvider) {
        this.parent = parent;
        this.mimePartProvider = mimePartProvider;
    }

    private void resetDataHandler() {
        dh = null;
        base64 = null;
    }
    
    /**
     * Process an <tt>xop:Include</tt> event and return the content ID.
     * <p>
     * Precondition: The parent reader is on the START_ELEMENT event for the <tt>xop:Include</tt>
     * element. Note that the method doesn't check this condition.
     * <p>
     * Postcondition: The parent reader is on the event following the END_ELEMENT event for the
     * <tt>xop:Include</tt> element, i.e. the parent reader is on the END_ELEMENT event of the
     * element enclosing the <tt>xop:Include</tt> element.
     * 
     * @return the content ID the <tt>xop:Include</tt> refers to
     * 
     * @throws XMLStreamException
     */
    private String processXopInclude() throws XMLStreamException {
        if (parent.getAttributeCount() != 1 ||
                !parent.getAttributeLocalName(0).equals("href")) {
            throw new XMLStreamException("Expected xop:Include element information item with " +
                    "a (single) href attribute");
        }
        String href = parent.getAttributeValue(0);
        if (!href.startsWith("cid:")) {
            throw new XMLStreamException("Expected href attribute containing a URL in the " +
                    "cid scheme");
        }
        String contentID;
        try {
            // URIs should always be decoded using UTF-8. On the other hand, since non ASCII
            // characters are not allowed in content IDs, we can simply decode using ASCII
            // (which is a subset of UTF-8)
            contentID = URLDecoder.decode(href.substring(4), "ascii");
        } catch (UnsupportedEncodingException ex) {
            // We should never get here
            throw new XMLStreamException(ex);
        }
        if (parent.next() != END_ELEMENT) {
            throw new XMLStreamException(
                    "Expected xop:Include element information item to be empty");
        }
        // Also consume the END_ELEMENT event of the xop:Include element. There are
        // two reasons for this:
        //  - It allows us to validate that the message conforms to the XOP specs.
        //  - It makes it easier to implement the getNamespaceContext method.
        if (parent.next() != END_ELEMENT) {
            throw new XMLStreamException(SOLE_CHILD_MSG);
        }
        if (log.isDebugEnabled()) {
            log.debug("Encountered xop:Include for content ID '" + contentID + "'");
        }
        return contentID;
    }
    
    public int next() throws XMLStreamException {
        boolean wasStartElement;
        int event;
        if (dh != null) {
            resetDataHandler();
            // We already advanced to the next event after the xop:Include (see below), so there
            // is no call to parent.next() here
            event = END_ELEMENT;
            wasStartElement = false;
        } else {
            wasStartElement = parent.getEventType() == START_ELEMENT;
            event = parent.next();
        }
        if (event == START_ELEMENT
                && parent.getLocalName().equals(XOPConstants.INCLUDE)
                && parent.getNamespaceURI().equals(XOPConstants.NAMESPACE_URI)) {
            if (!wasStartElement) {
                throw new XMLStreamException(SOLE_CHILD_MSG);
            }
            dh = new DataHandlerProviderImpl(mimePartProvider, processXopInclude());
            return CHARACTERS;
        } else {
            return event;
        }
    }

    public int getEventType() {
        return dh == null ? parent.getEventType() : CHARACTERS;
    }

    public int nextTag() throws XMLStreamException {
        if (dh != null) {
            resetDataHandler();
            // We already advanced to the next event after the xop:Include (see the implementation
            // of the next() method) and we now that it is an END_ELEMENT event.
            return END_ELEMENT;
        } else {
            return parent.nextTag();
        }
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        if (DataHandlerReader.PROPERTY.equals(name)) {
            return this;
        } else {
            return parent.getProperty(name);
        }
    }

    public void close() throws XMLStreamException {
        parent.close();
    }

    public int getAttributeCount() {
        return parent.getAttributeCount();
    }

    public String getAttributeLocalName(int index) {
        return parent.getAttributeLocalName(index);
    }

    public QName getAttributeName(int index) {
        return parent.getAttributeName(index);
    }

    public String getAttributeNamespace(int index) {
        return parent.getAttributeNamespace(index);
    }

    public String getAttributePrefix(int index) {
        return parent.getAttributePrefix(index);
    }

    public String getAttributeType(int index) {
        return parent.getAttributeType(index);
    }

    public String getAttributeValue(int index) {
        return parent.getAttributeValue(index);
    }

    public boolean isAttributeSpecified(int index) {
        return parent.isAttributeSpecified(index);
    }

    public String getAttributeValue(String namespaceURI, String localName) {
        return parent.getAttributeValue(namespaceURI, localName);
    }

    public String getCharacterEncodingScheme() {
        return parent.getCharacterEncodingScheme();
    }

    public String getElementText() throws XMLStreamException {
        if (parent.getEventType() != START_ELEMENT) {
            throw new XMLStreamException("The current event is not a START_ELEMENT event");
        }
        int event = parent.next();
        // Note that an xop:Include must be the first child of the element
        if (event == START_ELEMENT
                && parent.getLocalName().equals(XOPConstants.INCLUDE)
                && parent.getNamespaceURI().equals(XOPConstants.NAMESPACE_URI)) {
            return toBase64(mimePartProvider.getMimePart(processXopInclude()));
        } else {
            String text = null;
            StringBuffer buffer = null;
            while (event != END_ELEMENT) {
                switch (event) {
                    case CHARACTERS:
                    case CDATA:
                    case SPACE:
                    case ENTITY_REFERENCE:
                        if (text == null && buffer == null) {
                            text = parent.getText();
                        } else {
                            String thisText = parent.getText();
                            if (buffer == null) {
                                buffer = new StringBuffer(text.length() + thisText.length());
                                buffer.append(text);
                            }
                            buffer.append(thisText);
                        }
                        break;
                    case PROCESSING_INSTRUCTION:
                    case COMMENT:
                        // Skip this event
                        break;
                    default:
                        throw new XMLStreamException("Unexpected event " +
                                StAXUtils.getEventTypeString(event) +
                                " while reading element text");
                }
                event = parent.next();
            }
            if (buffer != null) {
                return buffer.toString();
            } else if (text != null) {
                return text;
            } else {
                return "";
            }
        }
    }

    public String getEncoding() {
        return parent.getEncoding();
    }

    public String getPrefix() {
        if (dh != null) {
            throw new IllegalStateException();
        } else {
            return parent.getPrefix();
        }
    }

    public String getNamespaceURI() {
        if (dh != null) {
            throw new IllegalStateException();
        } else {
            return parent.getNamespaceURI();
        }
    }

    public String getLocalName() {
        if (dh != null) {
            throw new IllegalStateException();
        } else {
            return parent.getLocalName();
        }
    }

    public QName getName() {
        if (dh != null) {
            throw new IllegalStateException();
        } else {
            return parent.getName();
        }
    }

    public Location getLocation() {
        return parent.getLocation();
    }

    // Attention!
    public NamespaceContext getNamespaceContext() {
        return parent.getNamespaceContext();
    }

    public String getNamespaceURI(String prefix) {
        String uri = parent.getNamespaceURI(prefix);
        if ("xop".equals(prefix) && uri != null) {
            System.out.println(prefix + " -> " + uri);
        }
        return uri;
    }

    public int getNamespaceCount() {
        if (dh != null) {
            throw new IllegalStateException();
        } else {
            return parent.getNamespaceCount();
        }
    }

    public String getNamespacePrefix(int index) {
        if (dh != null) {
            throw new IllegalStateException();
        } else {
            return parent.getNamespacePrefix(index);
        }
    }

    public String getNamespaceURI(int index) {
        if (dh != null) {
            throw new IllegalStateException();
        } else {
            return parent.getNamespaceURI(index);
        }
    }

    public String getPIData() {
        return parent.getPIData();
    }

    public String getPITarget() {
        return parent.getPITarget();
    }

    private static String toBase64(DataHandler dh) throws XMLStreamException {
        try {
            return Base64Utils.encode(dh);
        } catch (IOException ex) {
            throw new XMLStreamException("Exception when encoding data handler as base64", ex);
        }
    }
    
    private String toBase64() throws XMLStreamException {
        if (base64 == null) {
            base64 = toBase64(dh.getDataHandler());
        }
        return base64;
    }
    
    public String getText() {
        if (dh != null) {
            try {
                return toBase64();
            } catch (XMLStreamException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            return parent.getText();
        }
    }

    public char[] getTextCharacters() {
        if (dh != null) {
            try {
                return toBase64().toCharArray();
            } catch (XMLStreamException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            return parent.getTextCharacters();
        }
    }

    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length)
            throws XMLStreamException {
        if (dh != null) {
            String text = toBase64();
            int copied = Math.min(length, text.length()-sourceStart);
            text.getChars(sourceStart, sourceStart + copied, target, targetStart);
            return copied;
        } else {
            return parent.getTextCharacters(sourceStart, target, targetStart, length);
        }
    }

    public int getTextLength() {
        if (dh != null) {
            try {
                return toBase64().length();
            } catch (XMLStreamException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            return parent.getTextLength();
        }
    }

    public int getTextStart() {
        if (dh != null) {
            return 0;
        } else {
            return parent.getTextStart();
        }
    }

    public String getVersion() {
        return parent.getVersion();
    }

    public boolean hasNext() throws XMLStreamException {
        return parent.hasNext();
    }

    public boolean isStandalone() {
        return parent.isStandalone();
    }

    public boolean standaloneSet() {
        return parent.standaloneSet();
    }

    public boolean hasText() {
        return dh != null || parent.hasText();
    }

    public boolean isCharacters() {
        return dh != null || parent.isCharacters();
    }

    public boolean isStartElement() {
        return dh == null && parent.isStartElement();
    }

    public boolean isEndElement() {
        return dh == null && parent.isEndElement();
    }

    public boolean hasName() {
        return dh == null && parent.hasName();
    }

    public boolean isWhiteSpace() {
        return dh == null && parent.isWhiteSpace();
    }

    public void require(int type, String namespaceURI, String localName)
            throws XMLStreamException {
        if (dh != null) {
            if (type != CHARACTERS) {
                throw new XMLStreamException("Expected CHARACTERS event");
            }
        } else {
            parent.require(type, namespaceURI, localName);
        }
    }

    public boolean isBinary() {
        return dh != null;
    }

    public boolean isDeferred() {
        return true;
    }
    
    public String getContentID() {
        return dh.getContentID();
    }

    public DataHandler getDataHandler() throws XMLStreamException{
        return dh.getDataHandler();
    }

    public DataHandlerProvider getDataHandlerProvider() {
        return dh;
    }
}
