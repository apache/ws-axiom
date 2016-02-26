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
package org.apache.axiom.om.impl.common.builder;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlInput;
import org.apache.axiom.core.stream.XmlReader;
import org.apache.axiom.om.DeferredParsingException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;

public final class PushBuilder extends AbstractBuilder {
    private final XmlReader reader;
    
    public PushBuilder(XmlInput input, NodeFactory nodeFactory, Model model, AxiomSourcedElement root,
            boolean repairNamespaces) {
        super(nodeFactory, model, root, repairNamespaces);
        reader = input.createReader(handler);
    }
    
    @Override
    public int next() throws OMException {
        try {
            reader.proceed();
        } catch (StreamException ex) {
            throw new DeferredParsingException(ex);
        }
        return -1;
    }

    public final void discard(OMElement el) throws OMException {
        throw new UnsupportedOperationException();
    }

    public final void setCache(boolean b) throws OMException {
        throw new UnsupportedOperationException();
    }

    public final boolean isCache() {
        throw new UnsupportedOperationException();
    }

    public final OMElement getDocumentElement() {
        return getDocument().getOMDocumentElement();
    }

    public final OMElement getDocumentElement(boolean discardDocument) {
        OMElement documentElement = getDocument().getOMDocumentElement();
        if (discardDocument) {
            documentElement.detach();
        }
        return documentElement;
    }

    public final String getCharacterEncoding() {
        throw new UnsupportedOperationException();
    }

    public final void close() {
        // This is a no-op
    }
    
    public void detach() {
        // Force processing of the SAX source
        getDocument();
    }

    @Override
    public final boolean isClosed() {
        return true;
    }

    @Override
    public final Object getReaderProperty(String name) throws IllegalArgumentException {
        return null;
    }

    @Override
    public final XMLStreamReader disableCaching() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void reenableCaching(CoreParentNode container) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void discard(CoreParentNode container) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void debugDiscarded(CoreParentNode container) {
    }
}
