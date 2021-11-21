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

import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.core.stream.StreamException;

abstract class Context {
    protected final BuilderHandler builderHandler;
    protected final int depth;
    private BuildableContext nestedContext;

    Context(BuilderHandler builderHandler, int depth) {
        this.builderHandler = builderHandler;
        this.depth = depth;
    }

    protected final BuildableContext newContext(CoreParentNode target) {
        if (nestedContext == null) {
            nestedContext = new BuildableContext(builderHandler, this, depth+1);
        }
        nestedContext.init(target);
        target.coreSetInputContext(nestedContext);
        builderHandler.incrementActiveContextCount();
        return nestedContext;
    }
    
    abstract void startDocument(String inputEncoding, String xmlVersion, String xmlEncoding, Boolean standalone);
    
    abstract void startFragment();
    
    abstract void processDocumentTypeDeclaration(String rootName, String publicId, String systemId,
            String internalSubset) throws StreamException;
    
    abstract Context startElement(String namespaceURI, String localName, String prefix) throws StreamException;
    
    abstract Context endElement() throws StreamException;
    
    abstract void processAttribute(String namespaceURI, String localName, String prefix, String value, String type, boolean specified) throws StreamException;
    
    abstract void processAttribute(String name, String value, String type, boolean specified) throws StreamException;
    
    abstract void processNamespaceDeclaration(String prefix, String namespaceURI) throws StreamException;
    
    abstract void attributesCompleted() throws StreamException;
    
    abstract void processCharacterData(Object data, boolean ignorable) throws StreamException;
    
    abstract Context startProcessingInstruction(String piTarget) throws StreamException;
    
    abstract Context endProcessingInstruction() throws StreamException;
    
    abstract Context startComment() throws StreamException;
    
    abstract Context endComment() throws StreamException;
    
    abstract Context startCDATASection() throws StreamException;
    
    abstract Context endCDATASection() throws StreamException;
    
    abstract void processEntityReference(String name, String replacementText) throws StreamException;
    
    abstract void completed() throws StreamException;
}
