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

import java.io.IOException;
import java.io.Writer;

import javax.activation.DataHandler;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.ext.stax.CharacterDataReader;
import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.util.stax.AbstractXMLStreamReader;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * {@link XMLStreamReader} implementation that generates events from a given Axiom tree.
 */
public final class PullSerializer extends AbstractXMLStreamReader implements DataHandlerReader, DTDReader, CharacterDataReader {
    private static final Log log = LogFactory.getLog(PullSerializer.class);
    
    /**
     * The current state of the serializer.
     */
    private PullSerializerState state;
    
    /**
     * The serializer state saved by {@link #pushState(PullSerializerState)}.
     */
    private PullSerializerState savedState;
    
    /**
     * Indicates if an OMSourcedElement with an OMDataSource should
     * be considered as an interior node or a leaf node.
     */
    private boolean isDataSourceALeaf;

    public PullSerializer(OMContainer startNode, boolean cache, boolean preserveNamespaceContext) {
        state = new Navigator(this, startNode, cache, preserveNamespaceContext);
        if (log.isDebugEnabled()) {
            log.debug("Pull serializer created; initial state is " + state);
        }
    }
    
    /**
     * Switch the state of the serializer and release the old state. This method will use the
     * {@link PullSerializerState#released()} method to inform the old state that it has been
     * released.
     * 
     * @param newState
     *            the new state
     * @throws XMLStreamException
     */
    void switchState(PullSerializerState newState) throws XMLStreamException {
        if (log.isDebugEnabled()) {
            log.debug("Switching to state " + newState);
        }
        internalSwitchState(newState);
    }
    
    private void internalSwitchState(PullSerializerState newState) throws XMLStreamException {
        PullSerializerState oldState = state;
        PullSerializerState savedState = this.savedState;
        state = newState;
        this.savedState = null;
        if (savedState != null) {
            savedState.released();
        }
        oldState.released();
    }
    
    /**
     * Save the current state of the serializer and switch to a new state. The old state can be
     * restored with {@link #popState()}.
     * 
     * @param newState
     *            the new state
     */
    void pushState(PullSerializerState newState) {
        if (savedState != null) {
            throw new IllegalStateException();
        }
        if (log.isDebugEnabled()) {
            log.debug("Switching to state " + newState);
        }
        savedState = state;
        state = newState;
    }
    
    /**
     * Release the current state and restore the state previously saved by
     * {@link #pushState(PullSerializerState)}.
     * 
     * @throws XMLStreamException 
     */
    void popState() throws XMLStreamException {
        PullSerializerState savedState = this.savedState;
        if (savedState == null) {
            throw new IllegalStateException();
        }
        if (log.isDebugEnabled()) {
            log.debug("Restoring state " + savedState);
        }
        this.savedState = null;
        internalSwitchState(savedState);
        savedState.restored();
    }

    OMDataSource getDataSource() {
        return state.getDataSource();
    }
    
    void enableDataSourceEvents(boolean value) {
        isDataSourceALeaf = true;
    }

    boolean isDataSourceALeaf() {
        return isDataSourceALeaf;
    }

    public int getEventType() {
        return state.getEventType();
    }

    public boolean hasNext() throws XMLStreamException {
        return state.hasNext();
    }

    public int next() throws XMLStreamException {
        // Note: the state may change as a side effect of the call to next()
        state.next();
        return state.getEventType();
    }

    public int nextTag() throws XMLStreamException {
        int eventType = state.nextTag();
        return eventType == -1 ? super.nextTag() : eventType;
    }

    public void close() throws XMLStreamException {
        switchState(ClosedState.INSTANCE);
    }

    public Object getProperty(String name) {
        Object value = XMLStreamReaderUtils.processGetProperty(this, name);
        if (value != null) {
            return value;
        } else if (DTDReader.PROPERTY.equals(name) || CharacterDataReader.PROPERTY.equals(name)) {
            return this;
        } else {
            return state.getProperty(name);
        }
    }

    public String getVersion() {
        return state.getVersion();
    }

    public String getCharacterEncodingScheme() {
        return state.getCharacterEncodingScheme();
    }

    public String getEncoding() {
        return state.getEncoding();
    }

    public boolean isStandalone() {
        return state.isStandalone();
    }

    public boolean standaloneSet() {
        return state.standaloneSet();
    }

    public String getPrefix() {
        return state.getPrefix();
    }

    public String getNamespaceURI() {
        return state.getNamespaceURI();
    }

    public String getLocalName() {
        return state.getLocalName();
    }

    public QName getName() {
        return state.getName();
    }

    public int getNamespaceCount() {
        return state.getNamespaceCount();
    }

    public String getNamespacePrefix(int index) {
        return state.getNamespacePrefix(index);
    }

    public String getNamespaceURI(int index) {
        return state.getNamespaceURI(index);
    }

    public int getAttributeCount() {
        return state.getAttributeCount();
    }

    public String getAttributePrefix(int index) {
        return state.getAttributePrefix(index);
    }

    public String getAttributeNamespace(int index) {
        return state.getAttributeNamespace(index);
    }

    public String getAttributeLocalName(int index) {
        return state.getAttributeLocalName(index);
    }

    public QName getAttributeName(int index) {
        return state.getAttributeName(index);
    }

    public boolean isAttributeSpecified(int index) {
        return state.isAttributeSpecified(index);
    }

    public String getAttributeType(int index) {
        return state.getAttributeType(index);
    }

    public String getAttributeValue(int index) {
        return state.getAttributeValue(index);
    }

    public String getAttributeValue(String namespaceURI, String localName) {
        return state.getAttributeValue(namespaceURI, localName);
    }

    public NamespaceContext getNamespaceContext() {
        return state.getNamespaceContext();
    }

    public String getNamespaceURI(String prefix) {
        return state.getNamespaceURI(prefix);
    }

    public String getElementText() throws XMLStreamException {
        String text = state.getElementText();
        return text == null ? super.getElementText() : text;
    }

    public String getText() {
        return state.getText();
    }

    public char[] getTextCharacters() {
        return state.getTextCharacters();
    }

    public int getTextStart() {
        return state.getTextStart();
    }

    public int getTextLength() {
        return state.getTextLength();
    }

    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
        return state.getTextCharacters(sourceStart, target, targetStart, length);
    }

    public boolean isWhiteSpace() {
        Boolean isWhiteSpace = state.isWhiteSpace();
        return isWhiteSpace == null ? super.isWhiteSpace() : isWhiteSpace.booleanValue();
    }

    public String getPIData() {
        return state.getPIData();
    }

    public String getPITarget() {
        return state.getPITarget();
    }
    
    public boolean isBinary() {
        return state.getDataHandlerReader().isBinary();
    }

    public boolean isOptimized() {
        return state.getDataHandlerReader().isOptimized();
    }

    public boolean isDeferred() {
        return state.getDataHandlerReader().isDeferred();
    }

    public String getContentID() {
        return state.getDataHandlerReader().getContentID();
    }

    public DataHandler getDataHandler() throws XMLStreamException {
        return state.getDataHandlerReader().getDataHandler();
    }

    public DataHandlerProvider getDataHandlerProvider() {
        return state.getDataHandlerReader().getDataHandlerProvider();
    }

    public String getRootName() {
        return state.getDTDReader().getRootName();
    }

    public String getPublicId() {
        return state.getDTDReader().getPublicId();
    }

    public String getSystemId() {
        return state.getDTDReader().getSystemId();
    }

    public void writeTextTo(Writer writer) throws XMLStreamException, IOException {
        state.getCharacterDataReader().writeTextTo(writer);
    }
}
