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
package org.apache.axiom.core.stream.stax.pull.output;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.XmlReader;
import org.apache.axiom.core.stream.stax.StAXExceptionUtil;
import org.apache.axiom.core.stream.util.CharacterDataAccumulator;
import org.apache.axiom.util.xml.QNameCache;
import org.apache.axiom.util.xml.stream.XMLEventUtils;

public final class StAXPivot implements InternalXMLStreamReader, XmlHandler {
    private class NamespaceContextImpl implements NamespaceContext {
        @Override
        public String getNamespaceURI(String prefix) {
            if (prefix == null) {
                throw new IllegalArgumentException();
            }
            String namespaceURI = lookupNamespaceURI(prefix);
            return namespaceURI == null ? XMLConstants.NULL_NS_URI : namespaceURI;
        }

        @Override
        public String getPrefix(String namespaceURI) {
            if (namespaceURI == null) {
                throw new IllegalArgumentException();
            } else if (namespaceURI.equals(XMLConstants.XML_NS_URI)) {
                return XMLConstants.XML_NS_PREFIX;
            } else if (namespaceURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
                return XMLConstants.XMLNS_ATTRIBUTE;
            } else {
                int bindings = getNamespaceBindingsCount();
                outer: for (int i=(bindings-1)*2; i>=0; i-=2) {
                    if (namespaceURI.equals(namespaceStack[i+1])) {
                        String prefix = namespaceStack[i];
                        // Now check that the prefix is not masked
                        for (int j=i+2; j<bindings*2; j+=2) {
                            if (prefix.equals(namespaceStack[j])) {
                                continue outer;
                            }
                        }
                        return prefix;
                    }
                }
                return null;
            }
        }

        @Override
        public Iterator<String> getPrefixes(final String namespaceURI) {
            if (namespaceURI == null) {
                throw new IllegalArgumentException("namespaceURI can't be null");
            } else if (namespaceURI.equals(XMLConstants.XML_NS_URI)) {
                return Collections.singleton(XMLConstants.XML_NS_PREFIX).iterator();
            } else if (namespaceURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
                return Collections.singleton(XMLConstants.XMLNS_ATTRIBUTE).iterator();
            } else {
                final int bindings = getNamespaceBindingsCount();
                return new Iterator<String>() {
                    private int binding = bindings;
                    private String next;

                    public boolean hasNext() {
                        if (next == null) {
                            outer: while (--binding >= 0) {
                                if (namespaceURI.equals(namespaceStack[binding*2+1])) {
                                    String prefix = namespaceStack[binding*2];
                                    // Now check that the prefix is not masked
                                    for (int j=binding+1; j<bindings; j++) {
                                        if (prefix.equals(namespaceStack[j*2])) {
                                            continue outer;
                                        }
                                    }
                                    next = prefix;
                                    break;
                                }
                            }
                        }
                        return next != null;
                    }

                    public String next() {
                        if (hasNext()) {
                            String result = next;
                            next = null;
                            return result;
                        } else {
                            throw new NoSuchElementException();
                        }
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        }
    }
    
    private static final int STATE_DEFAULT = 0;
    
    /**
     * All data for the current StAX event has been received and the instance is not ready to
     * receive more data.
     */
    private static final int STATE_EVENT_COMPLETE = 1;
    
    /**
     * Indicates that all character data should be collected until a non character event is
     * encountered. This state is used to implement comment and CDATA section processing as well as
     * {@link XMLStreamReader#getElementText()}.
     */
    private static final int STATE_COLLECT_TEXT = 2;
    
    /**
     * Indicates that all events should be skipped until a start or end element event is
     * encountered. This state is used to implement {@link XMLStreamReader#nextTag()}.
     */
    private static final int STATE_NEXT_TAG = 3;
    
    /**
     * Indicates that all content (character data) in a comment or processing instruction should be
     * skipped. This state is used when comment or processing instruction is encountered in states
     * {@link #STATE_COLLECT_TEXT} or {@link #STATE_NEXT_TAG}. The {@link #previousState} attribute
     * is used to store the previous state, so that the state can be restored when the end of the
     * comment or processing instruction is reached.
     */
    private static final int STATE_SKIP_CONTENT = 4;
    
    /**
     * Indicates that an error has occurred an that the instance is no longer usable.
     */
    private static final int STATE_ERROR = 5;
    
    private final XMLStreamReaderExtensionFactory extensionFactory;
    private XmlReader reader;
    private Map<String,Object> extensions;
    private int state = STATE_DEFAULT;
    private int previousState = -1;
    private int eventType = -1;
    private int depth;
    private String[] elementStack = new String[24];
    private String[] namespaceStack = new String[16];
    private String[] attributeStack = new String[40];
    private int[] scopeStack = new int[8];
    private int attributeCount;
    private CharacterDataAccumulator accumulator;
    private NamespaceContextImpl namespaceContext;
    private String encoding;
    private String version;
    private String characterEncodingScheme;
    private Boolean standalone;
    private String rootName;
    private String publicId;
    private String systemId;
    private Object characterData;
    private String text;
    // Entity reference name or processing instruction target
    private String name;
    
    // TODO: The constructor should take an XmlInput object as input
    public StAXPivot(XMLStreamReaderExtensionFactory extensionFactory) {
        this.extensionFactory = extensionFactory;
    }

    private static String emptyToNull(String value) {
        return value == null || value.isEmpty() ? null : value;
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    public void setReader(XmlReader reader) throws StreamException {
        this.reader = reader;
        while (state != STATE_EVENT_COMPLETE) {
            reader.proceed();
        }
    }

    public void setPrefix(String prefix, String namespaceURI) {
        if (depth != 0) {
            throw new IllegalStateException();
        }
        putNamespaceDeclaration(scopeStack[0]++, prefix, namespaceURI);
    }

    private void putNamespaceDeclaration(int index, String prefix, String namespaceURI) {
        if (index*2 == namespaceStack.length) {
            String[] newNamespaceStack = new String[namespaceStack.length*2];
            System.arraycopy(namespaceStack, 0, newNamespaceStack, 0, namespaceStack.length);
            namespaceStack = newNamespaceStack;
        }
        namespaceStack[2*index] = prefix;
        namespaceStack[2*index+1] = namespaceURI;
    }

    private void checkState() {
        if (state == STATE_EVENT_COMPLETE) {
            throw new IllegalStateException();
        }
    }
    
    private void startCollectingText() {
        state = STATE_COLLECT_TEXT;
        if (accumulator == null) {
            accumulator = new CharacterDataAccumulator();
        }
    }
    
    private String stopCollectingText() {
        String data = accumulator.toString();
        accumulator.clear();
        state = STATE_EVENT_COMPLETE;
        return data;
    }
    
    @Override
    public void startDocument(String inputEncoding, String xmlVersion, String xmlEncoding, Boolean standalone) throws StreamException {
        checkState();
        eventType = START_DOCUMENT;
        encoding = inputEncoding;
        version = xmlVersion;
        characterEncodingScheme = xmlEncoding;
        this.standalone = standalone;
        state = STATE_EVENT_COMPLETE;
    }

    @Override
    public void startFragment() throws StreamException {
        checkState();
        eventType = START_DOCUMENT;
        state = STATE_EVENT_COMPLETE;
    }

    @Override
    public void processDocumentTypeDeclaration(String rootName, String publicId, String systemId, String internalSubset) throws StreamException {
        checkState();
        eventType = DTD;
        this.rootName = rootName;
        this.publicId = publicId;
        this.systemId = systemId;
        text = internalSubset;
        state = STATE_EVENT_COMPLETE;
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix) throws StreamException {
        checkState();
        eventType = START_ELEMENT;
        if (state == STATE_NEXT_TAG) {
            state = STATE_DEFAULT;
        }
        if (depth*3 == elementStack.length) {
            String[] newElementStack = new String[elementStack.length*2];
            System.arraycopy(elementStack, 0, newElementStack, 0, elementStack.length);
            elementStack = newElementStack;
        }
        elementStack[depth*3] = namespaceURI;
        elementStack[depth*3+1] = localName;
        elementStack[depth*3+2] = prefix;
        if (depth+1 == scopeStack.length) {
            int[] newScopeStack = new int[scopeStack.length*2];
            System.arraycopy(scopeStack, 0, newScopeStack, 0, scopeStack.length);
            scopeStack = newScopeStack;
        }
        scopeStack[depth+1] = scopeStack[depth];
        attributeCount = 0;
    }

    @Override
    public void endElement() throws StreamException {
        checkState();
        eventType = END_ELEMENT;
        depth--;
        state = STATE_EVENT_COMPLETE;
    }

    @Override
    public void processAttribute(String namespaceURI, String localName, String prefix, String value, String type, boolean specified) throws StreamException {
        if (attributeCount*5 == attributeStack.length) {
            String[] newAttributeStack = new String[attributeStack.length*2];
            System.arraycopy(attributeStack, 0, newAttributeStack, 0, attributeStack.length);
            attributeStack = newAttributeStack;
        }
        attributeStack[5*attributeCount] = namespaceURI;
        attributeStack[5*attributeCount+1] = localName;
        attributeStack[5*attributeCount+2] = prefix;
        attributeStack[5*attributeCount+3] = value;
        attributeStack[5*attributeCount+4] = type;
        attributeCount++;
    }

    @Override
    public void processAttribute(String name, String value, String type, boolean specified)
            throws StreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public void processNamespaceDeclaration(String prefix, String namespaceURI) throws StreamException {
        putNamespaceDeclaration(scopeStack[depth+1]++, prefix, namespaceURI);
    }

    @Override
    public void attributesCompleted() throws StreamException {
        state = STATE_EVENT_COMPLETE;
    }

    @Override
    public void processCharacterData(Object data, boolean ignorable) throws StreamException {
        switch (state) {
            case STATE_DEFAULT:
                eventType = ignorable ? SPACE : CHARACTERS;
                characterData = data;
                state = STATE_EVENT_COMPLETE;
                return;
            case STATE_COLLECT_TEXT:
                accumulator.append(data);
                return;
            case STATE_NEXT_TAG:
                // TODO: check that the character data only contains whitespace!
                break;
            case STATE_SKIP_CONTENT:
                break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void startProcessingInstruction(String target) throws StreamException {
        switch (state) {
            case STATE_DEFAULT:
                eventType = PROCESSING_INSTRUCTION;
                name = target;
                startCollectingText();
                break;
            case STATE_COLLECT_TEXT:
            case STATE_NEXT_TAG:
                previousState = state;
                state = STATE_SKIP_CONTENT;
                break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void endProcessingInstruction() throws StreamException {
        switch (state) {
            case STATE_COLLECT_TEXT:
                text = stopCollectingText();
                break;
            case STATE_SKIP_CONTENT:
                state = previousState;
                break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void startComment() throws StreamException {
        switch (state) {
            case STATE_DEFAULT:
                eventType = COMMENT;
                startCollectingText();
                break;
            case STATE_COLLECT_TEXT:
            case STATE_NEXT_TAG:
                previousState = state;
                state = STATE_SKIP_CONTENT;
                break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void endComment() throws StreamException {
        switch (state) {
            case STATE_COLLECT_TEXT:
                text = stopCollectingText();
                break;
            case STATE_SKIP_CONTENT:
                state = previousState;
                break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void startCDATASection() throws StreamException {
        checkState();
        eventType = CDATA;
        startCollectingText();
    }

    @Override
    public void endCDATASection() throws StreamException {
        text = stopCollectingText();
    }

    @Override
    public void processEntityReference(String name, String replacementText) throws StreamException {
        checkState();
        eventType = ENTITY_REFERENCE;
        this.name = name;
        text = replacementText;
        state = STATE_EVENT_COMPLETE;
    }

    @Override
    public void completed() throws StreamException {
        checkState();
        eventType = END_DOCUMENT;
        state = STATE_EVENT_COMPLETE;
    }

    @Override
    public boolean drain() throws StreamException {
        return true;
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        if (extensions != null) {
            Object extension = extensions.get(name);
            if (extension != null) {
                return extension;
            }
        }
        if (extensionFactory != null) {
            Object extension = extensionFactory.createExtension(name, this);
            if (extension != null) {
                if (extensions == null) {
                    extensions = new HashMap<String,Object>();
                }
                extensions.put(name, extension);
                return extension;
            }
        }
        return null;
    }

    @Override
    public int next() throws XMLStreamException {
        try {
            switch (state) {
                case STATE_EVENT_COMPLETE:
                    state = STATE_DEFAULT;
                    break;
                case STATE_ERROR:
                    throw new IllegalStateException();
            }
            switch (eventType) {
                case CHARACTERS:
                case SPACE:
                    characterData = null;
                    // Fall through
                case CDATA:
                case COMMENT:
                case PROCESSING_INSTRUCTION:
                case DTD:
                case ENTITY_REFERENCE:
                    text = null;
                    break;
                case START_ELEMENT:
                    depth++;
                    break;
                case END_DOCUMENT:
                    throw new NoSuchElementException();
            }
            while (state != STATE_EVENT_COMPLETE) {
                reader.proceed();
            }
            return eventType;
        } catch (StreamException ex) {
            state = STATE_ERROR;
            throw StAXExceptionUtil.toXMLStreamException(ex);
        } catch (RuntimeException ex) {
            state = STATE_ERROR;
            throw ex;
        } catch (Error ex) {
            state = STATE_ERROR;
            throw ex;
        }
    }

    @Override
    public void require(int expectedType, String expectedNamespaceURI, String expectedLocalName) throws XMLStreamException {
        if (expectedType != eventType) {
            throw new XMLStreamException("Required type " + XMLEventUtils.getEventTypeString(expectedType)
                    + ", actual type " + XMLEventUtils.getEventTypeString(eventType));
        }

        if (expectedLocalName != null) {
            if (eventType != START_ELEMENT && eventType != END_ELEMENT
                && eventType != ENTITY_REFERENCE) {
                throw new XMLStreamException("Required a non-null local name, but current token " +
                        "not a START_ELEMENT, END_ELEMENT or ENTITY_REFERENCE (was " +
                        XMLEventUtils.getEventTypeString(eventType) + ")");
            }
            String localName = getLocalName();
            if (!localName.equals(expectedLocalName)) {
                throw new XMLStreamException("Required local name '" + expectedLocalName +
                        "'; current local name '" + localName + "'.");
            }
        }
        
        if (expectedNamespaceURI != null) {
            if (eventType != START_ELEMENT && eventType != END_ELEMENT) {
                throw new XMLStreamException("Required non-null namespace URI, but current token " +
                        "not a START_ELEMENT or END_ELEMENT (was " +
                        XMLEventUtils.getEventTypeString(eventType) + ")");
            }
            String namespaceURI = elementStack[3*depth];
            if (!expectedNamespaceURI.equals(namespaceURI)) {
                throw new XMLStreamException("Required namespace '" + expectedNamespaceURI + "'; have '" + namespaceURI +"'.");
            }
        }
    }

    @Override
    public String getEncoding() {
        if (eventType == START_DOCUMENT) {
            return encoding;
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public String getVersion() {
        // Although the Javadoc clearly indicates that getVersion is only supported in state
        // START_DOCUMENT, the XSLT implementation in the JRE calls this in other states.
        return version;
    }

    @Override
    public String getCharacterEncodingScheme() {
        if (eventType == START_DOCUMENT) {
            return characterEncodingScheme;
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public boolean isStandalone() {
        if (eventType == START_DOCUMENT) {
            return standalone != null && standalone;
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public String getElementText() throws XMLStreamException {
        startCollectingText();
        next();
        return stopCollectingText();
    }

    @Override
    public int nextTag() throws XMLStreamException {
        state = STATE_NEXT_TAG;
        return next();
    }

    @Override
    public boolean hasNext() throws XMLStreamException {
        return eventType != END_DOCUMENT;
    }

    @Override
    public void close() throws XMLStreamException {
        // TODO: update state
        reader.dispose();
    }

    int getNamespaceBindingsCount() {
        return scopeStack[eventType == START_ELEMENT || eventType == END_ELEMENT ? depth + 1 : depth];
    }
    
    String lookupNamespaceURI(String prefix) {
        if (prefix.equals(XMLConstants.XML_NS_PREFIX)) {
            return XMLConstants.XML_NS_URI;
        } else if (prefix.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
            return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
        } else {
            for (int i=(getNamespaceBindingsCount()-1)*2; i>=0; i-=2) {
                if (prefix.equals(namespaceStack[i])) {
                    return namespaceStack[i+1];
                }
            }
            return prefix.isEmpty() ? "" : null;
        }
    }
    
    @Override
    public String getNamespaceURI(String prefix) {
        return emptyToNull(lookupNamespaceURI(nullToEmpty(prefix)));
    }

    @Override
    public boolean isStartElement() {
        return eventType == START_ELEMENT;
    }

    @Override
    public boolean isEndElement() {
        return eventType == END_ELEMENT;
    }

    @Override
    public boolean isCharacters() {
        return eventType == CHARACTERS;
    }

    @Override
    public boolean isWhiteSpace() {
        switch (getEventType()) {
            case SPACE:
                return true;
            case CHARACTERS:
                // XMLStreamReader Javadoc says that isWhiteSpace "returns true if the cursor
                // points to a character data event that consists of all whitespace". This
                // means that this method may return true for a CHARACTER event and we need
                // to scan the text of the node.
                String text = internalGetText();
                for (int i=0; i<text.length(); i++) {
                    char c = text.charAt(i);
                    if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                        return false;
                    }
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public int getAttributeCount() {
        if (eventType == START_ELEMENT) {
            return attributeCount;
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public QName getAttributeName(int index) {
        if (eventType == START_ELEMENT) {
            return QNameCache.getQName(attributeStack[5*index], attributeStack[5*index+1], attributeStack[5*index+2]);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public String getAttributeNamespace(int index) {
        if (eventType == START_ELEMENT) {
            return emptyToNull(attributeStack[5*index]);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public String getAttributeLocalName(int index) {
        if (eventType == START_ELEMENT) {
            return attributeStack[5*index+1];
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public String getAttributePrefix(int index) {
        if (eventType == START_ELEMENT) {
            return emptyToNull(attributeStack[5*index+2]);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public String getAttributeValue(int index) {
        if (eventType == START_ELEMENT) {
            return attributeStack[5*index+3];
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public String getAttributeType(int index) {
        if (eventType == START_ELEMENT) {
            return attributeStack[5*index+4];
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public boolean isAttributeSpecified(int index) {
        if (eventType == START_ELEMENT) {
            // TODO Auto-generated method stub
            return true;
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public String getAttributeValue(String namespaceURI, String localName) {
        if (eventType == START_ELEMENT) {
            namespaceURI = nullToEmpty(namespaceURI);
            for (int i=0; i<attributeCount; i++) {
                if (localName.equals(attributeStack[i*5+1]) && namespaceURI.equals(attributeStack[i*5])) {
                    return attributeStack[i*5+3];
                }
            }
            return null;
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public int getNamespaceCount() {
        switch (eventType) {
            case START_ELEMENT:
            case END_ELEMENT:
                return scopeStack[depth+1]-scopeStack[depth];
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public String getNamespacePrefix(int index) {
        switch (eventType) {
            case START_ELEMENT:
            case END_ELEMENT:
                return emptyToNull(namespaceStack[2*(scopeStack[depth]+index)]);
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public String getNamespaceURI(int index) {
        switch (eventType) {
            case START_ELEMENT:
            case END_ELEMENT:
                // The XSLT implementation in the JRE doesn't like null values returned here.
                // Returning empty strings is also what Woodstox does.
                return namespaceStack[2*(scopeStack[depth]+index)+1];
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        if (namespaceContext == null) {
            namespaceContext = new NamespaceContextImpl();
        }
        return namespaceContext;
    }

    @Override
    public int getEventType() {
        return eventType;
    }

    private String internalGetText() {
        if (text == null && characterData != null) {
            text = characterData.toString();
        }
        return text;
    }
    
    @Override
    public String getText() {
        switch (eventType) {
            case CHARACTERS:
            case CDATA:
            case SPACE:
            case COMMENT:
            case DTD:
            case ENTITY_REFERENCE:
                return internalGetText();
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public char[] getTextCharacters() {
        switch (eventType) {
            case CHARACTERS:
            case CDATA:
            case SPACE:
            case COMMENT:
                // TODO: optimize this
                return internalGetText().toCharArray();
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
        String text = internalGetText();
        int copied = Math.min(length, text.length()-sourceStart);
        text.getChars(sourceStart, sourceStart + copied, target, targetStart);
        return copied;
    }

    @Override
    public int getTextStart() {
        switch (eventType) {
            case CHARACTERS:
            case CDATA:
            case SPACE:
            case COMMENT:
                return 0;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public int getTextLength() {
        switch (eventType) {
            case CHARACTERS:
            case CDATA:
            case SPACE:
            case COMMENT:
                // TODO: optimize this
                return internalGetText().length();
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public boolean hasText() {
        return eventType == CHARACTERS || eventType == DTD
                || eventType == CDATA || eventType == ENTITY_REFERENCE
                || eventType == COMMENT || eventType == SPACE;
    }

    @Override
    public Location getLocation() {
        return DummyLocation.INSTANCE;
    }

    @Override
    public QName getName() {
        switch (eventType) {
            case START_ELEMENT:
            case END_ELEMENT:
                return QNameCache.getQName(elementStack[3*depth], elementStack[3*depth+1], elementStack[3*depth+2]);
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public boolean hasName() {
        return eventType == START_ELEMENT || eventType == END_ELEMENT;
    }

    @Override
    public String getNamespaceURI() {
        switch (eventType) {
            case START_ELEMENT:
            case END_ELEMENT:
                return emptyToNull(elementStack[3*depth]);
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public String getLocalName() {
        switch (eventType) {
            case START_ELEMENT:
            case END_ELEMENT:
                return emptyToNull(elementStack[3*depth+1]);
            case ENTITY_REFERENCE:
                return name;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public String getPrefix() {
        switch (eventType) {
            case START_ELEMENT:
            case END_ELEMENT:
                // Saxon assumes that getPrefix returns "" instead of null.
                return elementStack[3*depth+2];
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public boolean standaloneSet() {
        if (eventType == START_DOCUMENT) {
            return standalone != null;
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public String getPITarget() {
        if (eventType == PROCESSING_INSTRUCTION) {
            return name;
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public String getPIData() {
        if (eventType == PROCESSING_INSTRUCTION) {
            return text;
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public String getRootName() {
        return rootName;
    }

    @Override
    public String getPublicId() {
        return publicId;
    }

    @Override
    public String getSystemId() {
        return systemId;
    }

    @Override
    public Object getCharacterData() {
        return characterData;
    }
}
