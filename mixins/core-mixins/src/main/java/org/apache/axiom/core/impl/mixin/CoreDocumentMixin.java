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
package org.apache.axiom.core.impl.mixin;

import org.apache.axiom.core.ChildNotAllowedException;
import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.core.CoreDocument;
import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.NodeType;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.weaver.annotation.Mixin;

@Mixin(CoreDocument.class)
public abstract class CoreDocumentMixin implements CoreDocument {
    private String inputEncoding;
    private String xmlVersion = "1.0";
    private String xmlEncoding;
    private Boolean standalone;
    
    @Override
    public final NodeType coreGetNodeType() {
        return NodeType.DOCUMENT;
    }
    
    @Override
    public final CoreNode getRootOrOwnerDocument() {
        return this;
    }
    
    @Override
    public final void coreSetOwnerDocument(CoreDocument document) {
        if (document != this) {
            throw new IllegalArgumentException();
        }
    }
    
    @Override
    public final CoreElement coreGetDocumentElement() throws CoreModelException {
        CoreChildNode child = coreGetFirstChild();
        while (child != null) {
            if (child instanceof CoreElement) {
                return (CoreElement)child;
            }
            child = child.coreGetNextSibling();
        }
        return null;
    }
    
    @Override
    public final String coreGetInputEncoding() {
        return inputEncoding;
    }
    
    @Override
    public final void coreSetInputEncoding(String inputEncoding) {
        this.inputEncoding = inputEncoding;
    }
    
    @Override
    public final String coreGetXmlVersion() {
        return xmlVersion;
    }
    
    @Override
    public final void coreSetXmlVersion(String xmlVersion) {
        this.xmlVersion = xmlVersion;
    }
    
    @Override
    public final String coreGetXmlEncoding() {
        return xmlEncoding;
    }
    
    @Override
    public final void coreSetXmlEncoding(String xmlEncoding) {
        this.xmlEncoding = xmlEncoding;
    }
    
    @Override
    public final Boolean coreGetStandalone() {
        return standalone;
    }
    
    @Override
    public final void coreSetStandalone(Boolean standalone) {
        this.standalone = standalone;
    }
    
    @Override
    public final <T> void init(ClonePolicy<T> policy, T options, CoreNode other) {
        CoreDocument o = (CoreDocument)other;
        coreSetXmlVersion(o.coreGetXmlVersion());
        coreSetXmlEncoding(o.coreGetXmlEncoding());
        coreSetStandalone(o.coreGetStandalone());
        coreSetInputEncoding(o.coreGetInputEncoding());
    }

    @Override
    public final void serializeStartEvent(XmlHandler handler) throws CoreModelException, StreamException {
        handler.startDocument(coreGetInputEncoding(), coreGetXmlVersion(), coreGetXmlEncoding(), coreGetStandalone());
    }
    
    @Override
    public final void serializeEndEvent(XmlHandler handler) throws StreamException {
        handler.completed();
    }

    final void internalCheckNewChild0(CoreChildNode newChild, CoreChildNode replacedChild) throws CoreModelException {
        if (newChild instanceof CoreElement && !(replacedChild instanceof CoreElement) && coreGetDocumentElement() != null) {
            throw new ChildNotAllowedException();
        }
    }
}
