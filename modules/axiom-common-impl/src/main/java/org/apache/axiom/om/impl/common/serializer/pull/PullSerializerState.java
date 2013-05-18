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
package org.apache.axiom.om.impl.common.serializer.pull;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.ext.stax.CharacterDataReader;
import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.impl.OMXMLStreamReaderEx;

/**
 * Encapsulates the state of the {@link PullSerializer}. Different subclasses are used depending on
 * the position reached in the OM tree or original document and the state of the serializer:
 * <ul>
 * <li>{@link PullThroughWrapper} is used if caching is turned off and the node for the current
 * event has not been instantiated. In this case, events are pulled directly from the underlying
 * parser.
 * <li>{@link IncludeWrapper} is used for events pulled directly from an {@link OMDataSource}.
 * <li>{@link EndDocumentState} is used when the {@link XMLStreamConstants#END_DOCUMENT} event has
 * been reached.
 * <li>{@link ClosedState} is used when {@link XMLStreamReader#close()} has been called on the
 * serializer.
 * <li>In all other cases, {@link Navigator} is used. This class is responsible for
 * generating StAX events from nodes in the object model.
 * </ul>
 */
abstract class PullSerializerState {
    /**
     * Get the {@link DTDReader} extension.
     * 
     * @return the {@link DTDReader} extension; must not be <code>null</code>
     */
    abstract DTDReader getDTDReader();
    
    /**
     * Get the {@link DataHandlerReader} extension.
     * 
     * @return the {@link DataHandlerReader} extension; must not be <code>null</code>
     */
    abstract DataHandlerReader getDataHandlerReader();
    
    /**
     * Get the {@link CharacterDataReader} extension.
     * 
     * @return the {@link CharacterDataReader} extension; must not be <code>null</code>
     */
    abstract CharacterDataReader getCharacterDataReader();
    
    abstract int getEventType();

    abstract boolean hasNext() throws XMLStreamException;

    abstract void next() throws XMLStreamException;

    /**
     * Skip to the next {@link XMLStreamConstants#START_ELEMENT} or
     * {@link XMLStreamConstants#END_ELEMENT} event. This method has the same contract as
     * {@link XMLStreamReader#nextTag()}, except that it is an optional operation: if the
     * implementation can't satisfy the request, it can return -1 to let {@link PullSerializer}
     * handle it. The reason is that skipping to the next start or end element event may require
     * switching to another state.
     * 
     * @return the event type, or -1 if the request could be satisfied
     * @throws XMLStreamException
     */
    abstract int nextTag() throws XMLStreamException ;

    abstract Object getProperty(String name) throws IllegalArgumentException;

    abstract String getVersion();

    abstract String getCharacterEncodingScheme();

    abstract String getEncoding();

    abstract boolean isStandalone();

    abstract boolean standaloneSet();

    abstract String getPrefix();

    abstract String getNamespaceURI();

    abstract String getLocalName();

    abstract QName getName();

    abstract int getNamespaceCount();

    abstract String getNamespacePrefix(int index);

    abstract String getNamespaceURI(int index);

    abstract int getAttributeCount();

    abstract String getAttributePrefix(int index);

    abstract String getAttributeNamespace(int index);

    abstract String getAttributeLocalName(int index);

    abstract QName getAttributeName(int index);

    abstract boolean isAttributeSpecified(int index);

    abstract String getAttributeType(int index);

    abstract String getAttributeValue(int index);

    abstract String getAttributeValue(String namespaceURI, String localName);

    abstract NamespaceContext getNamespaceContext();

    abstract String getNamespaceURI(String prefix);

    /**
     * Reads the content of a text-only element. This method has the same contract as
     * {@link XMLStreamReader#getElementText()}, except that it is an optional operation: if the
     * implementation can't satisfy the request, it can return <code>null</code> to let
     * {@link PullSerializer} handle it. The reason is that skipping to the next start or end
     * element event may require switching to another state.
     * 
     * @return the element text, or <code>null</code> if the request could be satisfied
     * @throws XMLStreamException
     */
    abstract String getElementText() throws XMLStreamException;

    abstract String getText();

    abstract char[] getTextCharacters();

    abstract int getTextStart();

    abstract int getTextLength();

    abstract int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException;

    /**
     * Check if the current event consists of all whitespace.
     * 
     * @return {@link Boolean#TRUE} if the current event consists of all whitespace,
     *         {@link Boolean#FALSE} if the current event doesn't consist of all whitespace, or
     *         <code>null</code> if the implementation has no optimized algorithm to determine if
     *         the event consists of whitespace and {@link PullSerializer} should use a default
     *         algorithm
     * 
     * @see XMLStreamReader#isWhiteSpace()
     */
    abstract Boolean isWhiteSpace();

    abstract String getPIData();

    abstract String getPITarget();

    /**
     * @see OMXMLStreamReaderEx#getDataSource()
     */
    abstract OMDataSource getDataSource();

    /**
     * Inform this state object that it has been released due to a call to
     * {@link PullSerializer#switchState(PullSerializerState)} or {@link PullSerializer#popState()}.
     * 
     * @throws XMLStreamException
     */
    abstract void released() throws XMLStreamException;
    
    /**
     * Inform this state object that it has been restored by a call to
     * {@link PullSerializer#popState()}.
     * 
     * @throws XMLStreamException 
     */
    abstract void restored() throws XMLStreamException;
}
