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
package org.apache.axiom.core;

public aspect CoreDocumentSupport {
    private String CoreDocument.inputEncoding;
    private String CoreDocument.xmlVersion = "1.0";
    private String CoreDocument.xmlEncoding;
    private boolean CoreDocument.standalone;
    
    public final NodeType CoreDocument.coreGetNodeType() {
        return NodeType.DOCUMENT;
    }
    
    public final CoreNode CoreDocument.getRootOrOwnerDocument() {
        return this;
    }
    
    public final void CoreDocument.coreSetOwnerDocument(CoreDocument document) {
        if (document != this) {
            throw new IllegalArgumentException();
        }
    }
    
    public final CoreElement CoreDocument.coreGetDocumentElement() {
        CoreChildNode child = coreGetFirstChild();
        while (child != null) {
            if (child instanceof CoreElement) {
                return (CoreElement)child;
            }
            child = child.coreGetNextSibling();
        }
        return null;
    }
    
    public final String CoreDocument.coreGetInputEncoding() {
        return inputEncoding;
    }
    
    public final void CoreDocument.coreSetInputEncoding(String inputEncoding) {
        this.inputEncoding = inputEncoding;
    }
    
    public final String CoreDocument.coreGetXmlVersion() {
        return xmlVersion;
    }
    
    public final void CoreDocument.coreSetXmlVersion(String xmlVersion) {
        this.xmlVersion = xmlVersion;
    }
    
    public final String CoreDocument.coreGetXmlEncoding() {
        return xmlEncoding;
    }
    
    public final void CoreDocument.coreSetXmlEncoding(String xmlEncoding) {
        this.xmlEncoding = xmlEncoding;
    }
    
    public final boolean CoreDocument.coreIsStandalone() {
        return standalone;
    }
    
    public final void CoreDocument.coreSetStandalone(boolean standalone) {
        this.standalone = standalone;
    }
    
    public final CoreNode CoreDocument.shallowClone(ClonePolicy policy, Object options) {
        // TODO
        throw new UnsupportedOperationException();
    }
}
