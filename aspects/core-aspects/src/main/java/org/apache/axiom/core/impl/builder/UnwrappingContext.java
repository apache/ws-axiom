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
package org.apache.axiom.core.impl.builder;

import org.apache.axiom.core.CoreNSAwareElement;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.core.stream.StreamException;

final class UnwrappingContext extends Context {
    private final CoreNSAwareElement root;
    
    UnwrappingContext(BuilderHandler builderHandler, CoreNSAwareElement root) {
        super(builderHandler, 0);
        this.root = root;
    }

    @Override
    void startDocument(String inputEncoding, String xmlVersion, String xmlEncoding,
            Boolean standalone) {
    }

    @Override
    void startFragment() {
    }

    @Override
    void processDocumentTypeDeclaration(String rootName, String publicId, String systemId,
            String internalSubset) throws StreamException {
        // TODO Auto-generated method stub
        
    }

    @Override
    Context startElement(String namespaceURI, String localName, String prefix)
            throws StreamException {
        root.validateName(prefix, localName, namespaceURI);
        root.initName(namespaceURI, localName, prefix, builderHandler.namespaceHelper);
        root.coreSetState(CoreParentNode.ATTRIBUTES_PENDING);
        Context nestedContext = newContext(root);
        // We will basically ignore events in the epilog, so mark this context as inactive
        builderHandler.decrementActiveContextCount();
        return nestedContext;
    }

    @Override
    BuildableContext endElement() throws StreamException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    void processAttribute(String namespaceURI, String localName, String prefix, String value,
            String type, boolean specified) throws StreamException {
        // TODO Auto-generated method stub
        
    }

    @Override
    void processAttribute(String name, String value, String type, boolean specified)
            throws StreamException {
        // TODO Auto-generated method stub
        
    }

    @Override
    void processNamespaceDeclaration(String prefix, String namespaceURI) throws StreamException {
        // TODO Auto-generated method stub
        
    }

    @Override
    void attributesCompleted() throws StreamException {
        // TODO Auto-generated method stub
        
    }

    @Override
    void processCharacterData(Object data, boolean ignorable) throws StreamException {
        // TODO Auto-generated method stub
        
    }

    @Override
    BuildableContext startProcessingInstruction(String piTarget) throws StreamException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    BuildableContext endProcessingInstruction() throws StreamException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    BuildableContext startComment() throws StreamException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    BuildableContext endComment() throws StreamException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    BuildableContext startCDATASection() throws StreamException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    BuildableContext endCDATASection() throws StreamException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    void processEntityReference(String name, String replacementText) throws StreamException {
        // TODO Auto-generated method stub
        
    }

    @Override
    void completed() throws StreamException {
        // TODO Auto-generated method stub
        
    }
}
