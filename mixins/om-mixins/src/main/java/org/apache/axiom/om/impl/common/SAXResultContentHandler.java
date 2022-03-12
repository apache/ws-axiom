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
package org.apache.axiom.om.impl.common;

import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.util.CharacterDataAccumulator;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;

public final class SAXResultContentHandler implements XmlHandler {
    private final OMContainer root;
    private final OMFactory factory;
    private OMContainer target;
    private final CharacterDataAccumulator buffer = new CharacterDataAccumulator();
    private boolean buffering;
    private String piTarget;

    public SAXResultContentHandler(OMContainer root) {
        this.root = root;
        factory = root.getOMFactory();
    }

    private String stopBuffering() {
        String content = buffer.toString();
        buffer.clear();
        buffering = false;
        return content;
    }

    @Override
    public void startDocument(
            String inputEncoding, String xmlVersion, String xmlEncoding, Boolean standalone) {
        target = root;
    }

    @Override
    public void startFragment() throws StreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public void completed() {}

    @Override
    public void processDocumentTypeDeclaration(
            String rootName, String publicId, String systemId, String internalSubset) {
        if (target instanceof OMDocument) {
            factory.createOMDocType(target, rootName, publicId, systemId, internalSubset);
        }
    }

    @Override
    public void startElement(String namespaceURI, String localName, String prefix) {
        // TODO: inefficient: we should not create a new OMNamespace instance every time
        target =
                factory.createOMElement(
                        localName, factory.createOMNamespace(namespaceURI, prefix), target);
    }

    @Override
    public void endElement() {
        target = ((OMNode) target).getParent();
    }

    @Override
    public void processAttribute(
            String namespaceURI,
            String localName,
            String prefix,
            String value,
            String type,
            boolean specified) {
        OMElement element = (OMElement) target;
        OMNamespace ns;
        if (namespaceURI.length() > 0) {
            ns = element.findNamespace(namespaceURI, prefix);
            if (ns == null) {
                throw new OMException("Unbound namespace " + namespaceURI);
            }
        } else {
            ns = null;
        }
        OMAttribute attr = element.addAttribute(localName, value, ns);
        attr.setAttributeType(type);
    }

    @Override
    public void processAttribute(String name, String value, String type, boolean specified)
            throws StreamException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public void processNamespaceDeclaration(String prefix, String namespaceURI) {
        if (prefix.isEmpty()) {
            ((OMElement) target).declareDefaultNamespace(namespaceURI);
        } else {
            ((OMElement) target).declareNamespace(namespaceURI, prefix);
        }
    }

    @Override
    public void attributesCompleted() {}

    @Override
    public void processCharacterData(Object data, boolean ignorable) {
        if (buffering) {
            buffer.append(data);
        } else {
            factory.createOMText(
                    target, data.toString(), ignorable ? OMNode.SPACE_NODE : OMNode.TEXT_NODE);
        }
    }

    @Override
    public void startCDATASection() throws StreamException {
        buffering = true;
    }

    @Override
    public void endCDATASection() throws StreamException {
        factory.createOMText(target, stopBuffering(), OMNode.CDATA_SECTION_NODE);
    }

    @Override
    public void startProcessingInstruction(String target) throws StreamException {
        buffering = true;
        piTarget = target;
    }

    @Override
    public void endProcessingInstruction() throws StreamException {
        factory.createOMProcessingInstruction(target, piTarget, stopBuffering());
        piTarget = null;
    }

    @Override
    public void startComment() throws StreamException {
        buffering = true;
    }

    @Override
    public void endComment() throws StreamException {
        factory.createOMComment(target, stopBuffering());
    }

    @Override
    public void processEntityReference(String name, String replacementText) {
        if (replacementText == null) {
            factory.createOMEntityReference(target, name);
        } else {
            // Since we set expandEntityReferences=true, we should never get here
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean drain() throws StreamException {
        return true;
    }
}
