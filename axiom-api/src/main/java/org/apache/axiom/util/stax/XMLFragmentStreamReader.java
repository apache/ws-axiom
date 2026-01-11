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

package org.apache.axiom.util.stax;

import java.util.NoSuchElementException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Wrapping XML stream reader that reads a single element from the underlying stream. It will
 * generate START_DOCUMENT and END_DOCUMENT events as required to make the sequence of events appear
 * as a complete document.
 *
 * <p>Assume for example that the parent reader is parsing the following document:
 *
 * <pre>&lt;a&gt;&lt;b&gt;text&lt;/b&gt;&lt;/a&gt;</pre>
 *
 * If the current event is <code>&lt;b&gt;</code> when the wrapper is created, it will produce the
 * following sequence of events:
 *
 * <p>
 *
 * <ul>
 *   <li>A synthetic START_DOCUMENT event.
 *   <li>START_ELEMENT, CHARACTERS and END_ELEMENT events for <code>&lt;b&gt;text&lt;/b&gt;</code>.
 *       For these events, the wrapper directly delegates to the parent reader.
 *   <li>A synthetic END_DOCUMENT event.
 * </ul>
 *
 * By default, after all events have been consumed from the wrapper, the current event on the parent
 * reader will be the event following the last END_ELEMENT of the fragment. In the example above
 * this will be <code>&lt;/a&gt;</code>. The {@link #XMLFragmentStreamReader(XMLStreamReader,
 * boolean)} constructor allows to override this behavior.
 *
 * <p>The wrapper will release the reference to the parent reader when {@link #close()} is called.
 * For obvious reasons, the wrapper will never call {@link XMLStreamReader#close()} on the parent
 * reader.
 */
public class XMLFragmentStreamReader implements XMLStreamReader {
    // The current event is a synthetic START_DOCUMENT event
    private static final int STATE_START_DOCUMENT = 0;

    // The current event is from the fragment and there will be more events from the fragment
    private static final int STATE_IN_FRAGMENT = 1;

    // The current event is the final END_ELEMENT event from the fragment
    private static final int STATE_FRAGMENT_END = 2;

    // The current event is a synthetic END_DOCUMENT event
    private static final int STATE_END_DOCUMENT = 3;

    private XMLStreamReader parent;
    private final boolean proceedToNext;
    private int state;
    private int depth;

    /**
     * Constructor.
     *
     * @param parent the parent reader to read the fragment from
     * @throws IllegalStateException if the current event on the parent is not a START_ELEMENT
     */
    public XMLFragmentStreamReader(XMLStreamReader parent) {
        this(parent, true);
    }

    /**
     * Constructor.
     *
     * @param parent the parent reader to read the fragment from
     * @param proceedToNext determines whether the parent reader should be positioned on the {@link
     *     XMLStreamConstants#END_ELEMENT} event of the fragment ({@code false}) or on event
     *     following the {@link XMLStreamConstants#END_ELEMENT} event ({@code true}) after all
     *     events have been consumed from the wrapper
     * @throws IllegalStateException if the current event on the parent is not a START_ELEMENT
     */
    public XMLFragmentStreamReader(XMLStreamReader parent, boolean proceedToNext) {
        this.parent = parent;
        this.proceedToNext = proceedToNext;
        if (parent.getEventType() != START_ELEMENT) {
            throw new IllegalStateException("Expected START_ELEMENT as current event");
        }
    }

    @Override
    public int getEventType() {
        return switch (state) {
            case STATE_START_DOCUMENT -> START_DOCUMENT;
            case STATE_IN_FRAGMENT -> parent.getEventType();
            case STATE_FRAGMENT_END -> END_ELEMENT;
            case STATE_END_DOCUMENT -> END_DOCUMENT;
            default -> throw new IllegalStateException();
        };
    }

    @Override
    public int next() throws XMLStreamException {
        return switch (state) {
            case STATE_START_DOCUMENT -> {
                state = STATE_IN_FRAGMENT;
                yield START_ELEMENT;
            }
            case STATE_IN_FRAGMENT -> {
                int type = parent.next();
                switch (type) {
                    case START_ELEMENT -> depth++;
                    case END_ELEMENT -> {
                        if (depth == 0) {
                            state = STATE_FRAGMENT_END;
                        } else {
                            depth--;
                        }
                    }
                }
                yield type;
            }
            case STATE_FRAGMENT_END -> {
                if (proceedToNext) {
                    parent.next();
                }
                state = STATE_END_DOCUMENT;
                yield END_DOCUMENT;
            }
            default -> throw new NoSuchElementException("End of document reached");
        };
    }

    @Override
    public int nextTag() throws XMLStreamException {
        return switch (state) {
            case STATE_START_DOCUMENT -> {
                state = STATE_IN_FRAGMENT;
                yield START_ELEMENT;
            }
            case STATE_END_DOCUMENT, STATE_FRAGMENT_END -> throw new NoSuchElementException();
            default -> {
                int result = parent.nextTag();
                switch (result) {
                    case START_ELEMENT -> depth++;
                    case END_ELEMENT -> {
                        if (depth == 0) {
                            state = STATE_FRAGMENT_END;
                        } else {
                            depth--;
                        }
                    }
                }
                yield result;
            }
        };
    }

    @Override
    public void close() throws XMLStreamException {
        parent = null;
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        return parent.getProperty(name);
    }

    @Override
    public String getCharacterEncodingScheme() {
        if (state == STATE_START_DOCUMENT) {
            return null;
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public String getEncoding() {
        if (state == STATE_START_DOCUMENT) {
            return null;
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public boolean isStandalone() {
        return true;
    }

    @Override
    public boolean standaloneSet() {
        return false;
    }

    @Override
    public Location getLocation() {
        return parent.getLocation();
    }

    @Override
    public int getAttributeCount() {
        if (state == STATE_START_DOCUMENT || state == STATE_END_DOCUMENT) {
            throw new IllegalStateException();
        } else {
            return parent.getAttributeCount();
        }
    }

    @Override
    public String getAttributeLocalName(int index) {
        if (state == STATE_START_DOCUMENT || state == STATE_END_DOCUMENT) {
            throw new IllegalStateException();
        } else {
            return parent.getAttributeLocalName(index);
        }
    }

    @Override
    public QName getAttributeName(int index) {
        if (state == STATE_START_DOCUMENT || state == STATE_END_DOCUMENT) {
            throw new IllegalStateException();
        } else {
            return parent.getAttributeName(index);
        }
    }

    @Override
    public String getAttributeNamespace(int index) {
        if (state == STATE_START_DOCUMENT || state == STATE_END_DOCUMENT) {
            throw new IllegalStateException();
        } else {
            return parent.getAttributeNamespace(index);
        }
    }

    @Override
    public String getAttributePrefix(int index) {
        if (state == STATE_START_DOCUMENT || state == STATE_END_DOCUMENT) {
            throw new IllegalStateException();
        } else {
            return parent.getAttributePrefix(index);
        }
    }

    @Override
    public String getAttributeType(int index) {
        if (state == STATE_START_DOCUMENT || state == STATE_END_DOCUMENT) {
            throw new IllegalStateException();
        } else {
            return parent.getAttributeType(index);
        }
    }

    @Override
    public String getAttributeValue(int index) {
        if (state == STATE_START_DOCUMENT || state == STATE_END_DOCUMENT) {
            throw new IllegalStateException();
        } else {
            return parent.getAttributeValue(index);
        }
    }

    @Override
    public boolean isAttributeSpecified(int index) {
        if (state == STATE_START_DOCUMENT || state == STATE_END_DOCUMENT) {
            throw new IllegalStateException();
        } else {
            return parent.isAttributeSpecified(index);
        }
    }

    @Override
    public String getAttributeValue(String namespaceURI, String localName) {
        if (state == STATE_START_DOCUMENT || state == STATE_END_DOCUMENT) {
            throw new IllegalStateException();
        } else {
            return parent.getAttributeValue(namespaceURI, localName);
        }
    }

    @Override
    public String getElementText() throws XMLStreamException {
        if (state == STATE_START_DOCUMENT || state == STATE_END_DOCUMENT) {
            throw new IllegalStateException();
        } else {
            return parent.getElementText();
        }
    }

    @Override
    public String getLocalName() {
        if (state == STATE_START_DOCUMENT || state == STATE_END_DOCUMENT) {
            throw new IllegalStateException();
        } else {
            return parent.getLocalName();
        }
    }

    @Override
    public QName getName() {
        if (state == STATE_START_DOCUMENT || state == STATE_END_DOCUMENT) {
            throw new IllegalStateException();
        } else {
            return parent.getName();
        }
    }

    @Override
    public String getPrefix() {
        if (state == STATE_START_DOCUMENT || state == STATE_END_DOCUMENT) {
            throw new IllegalStateException();
        } else {
            return parent.getPrefix();
        }
    }

    @Override
    public String getNamespaceURI() {
        if (state == STATE_START_DOCUMENT || state == STATE_END_DOCUMENT) {
            throw new IllegalStateException();
        } else {
            return parent.getNamespaceURI();
        }
    }

    @Override
    public int getNamespaceCount() {
        if (state == STATE_START_DOCUMENT || state == STATE_END_DOCUMENT) {
            throw new IllegalStateException();
        } else {
            return parent.getNamespaceCount();
        }
    }

    @Override
    public String getNamespacePrefix(int index) {
        if (state == STATE_START_DOCUMENT || state == STATE_END_DOCUMENT) {
            throw new IllegalStateException();
        } else {
            return parent.getNamespacePrefix(index);
        }
    }

    @Override
    public String getNamespaceURI(int index) {
        if (state == STATE_START_DOCUMENT || state == STATE_END_DOCUMENT) {
            throw new IllegalStateException();
        } else {
            return parent.getNamespaceURI(index);
        }
    }

    @Override
    public String getNamespaceURI(String prefix) {
        // It is not clear whether this method is allowed in all states.
        // The XMLStreamReader Javadoc suggest it is, but Woodstox doesn't
        // allow it on states other than START_ELEMENT and END_ELEMENT.
        // We emulate behavior of Woodstox.
        if (state == STATE_START_DOCUMENT || state == STATE_END_DOCUMENT) {
            throw new IllegalStateException();
        } else {
            return parent.getNamespaceURI(prefix);
        }
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return parent.getNamespaceContext();
    }

    @Override
    public String getPIData() {
        if (state == STATE_START_DOCUMENT || state == STATE_END_DOCUMENT) {
            throw new IllegalStateException();
        } else {
            return parent.getPIData();
        }
    }

    @Override
    public String getPITarget() {
        if (state == STATE_START_DOCUMENT || state == STATE_END_DOCUMENT) {
            throw new IllegalStateException();
        } else {
            return parent.getPITarget();
        }
    }

    @Override
    public String getText() {
        if (state == STATE_START_DOCUMENT || state == STATE_END_DOCUMENT) {
            throw new IllegalStateException();
        } else {
            return parent.getText();
        }
    }

    @Override
    public char[] getTextCharacters() {
        if (state == STATE_START_DOCUMENT || state == STATE_END_DOCUMENT) {
            throw new IllegalStateException();
        } else {
            return parent.getTextCharacters();
        }
    }

    @Override
    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length)
            throws XMLStreamException {
        if (state == STATE_START_DOCUMENT || state == STATE_END_DOCUMENT) {
            throw new IllegalStateException();
        } else {
            return getTextCharacters(sourceStart, target, targetStart, length);
        }
    }

    @Override
    public int getTextLength() {
        if (state == STATE_START_DOCUMENT || state == STATE_END_DOCUMENT) {
            throw new IllegalStateException();
        } else {
            return parent.getTextLength();
        }
    }

    @Override
    public int getTextStart() {
        if (state == STATE_START_DOCUMENT || state == STATE_END_DOCUMENT) {
            throw new IllegalStateException();
        } else {
            return parent.getTextStart();
        }
    }

    @Override
    public boolean hasName() {
        return state != STATE_START_DOCUMENT && state != STATE_END_DOCUMENT && parent.hasName();
    }

    @Override
    public boolean hasNext() throws XMLStreamException {
        return state != STATE_END_DOCUMENT;
    }

    @Override
    public boolean hasText() {
        return state != STATE_START_DOCUMENT && state != STATE_END_DOCUMENT && parent.hasText();
    }

    @Override
    public boolean isCharacters() {
        return state != STATE_START_DOCUMENT
                && state != STATE_END_DOCUMENT
                && parent.isCharacters();
    }

    @Override
    public boolean isStartElement() {
        return state != STATE_START_DOCUMENT
                && state != STATE_END_DOCUMENT
                && parent.isStartElement();
    }

    @Override
    public boolean isEndElement() {
        return state != STATE_START_DOCUMENT
                && state != STATE_END_DOCUMENT
                && parent.isEndElement();
    }

    @Override
    public boolean isWhiteSpace() {
        return state != STATE_START_DOCUMENT
                && state != STATE_END_DOCUMENT
                && parent.isWhiteSpace();
    }

    @Override
    public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
        switch (state) {
            case STATE_START_DOCUMENT -> {
                if (type != START_DOCUMENT) {
                    throw new XMLStreamException("Expected START_DOCUMENT");
                }
            }
            case STATE_END_DOCUMENT -> {
                if (type != END_DOCUMENT) {
                    throw new XMLStreamException("Expected END_DOCUMENT");
                }
            }
            default -> parent.require(type, namespaceURI, localName);
        }
    }
}
