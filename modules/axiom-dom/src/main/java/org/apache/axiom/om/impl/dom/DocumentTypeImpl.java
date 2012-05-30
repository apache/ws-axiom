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

package org.apache.axiom.om.impl.dom;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocType;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.OMNodeEx;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;

public class DocumentTypeImpl extends LeafNode implements DocumentType, OMDocType, OMNodeEx {
    private String value;
    
    public DocumentTypeImpl(DocumentImpl ownerDocument, OMFactory factory) {
        super(ownerDocument, factory);
        done = true;
    }

    public String getNodeName() {
        return getName();
    }

    public short getNodeType() {
        return DOCUMENT_TYPE_NODE;
    }

    public void internalSerialize(XMLStreamWriter writer, boolean cache) throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    public int getType() {
        return DTD_NODE;
    }

    public NamedNodeMap getEntities() {
        throw new UnsupportedOperationException();
    }

    public String getInternalSubset() {
        throw new UnsupportedOperationException();
    }

    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    public NamedNodeMap getNotations() {
        throw new UnsupportedOperationException();
    }

    public String getPublicId() {
        throw new UnsupportedOperationException();
    }

    public String getSystemId() {
        throw new UnsupportedOperationException();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String text) {
        value = text;
    }

    OMNode clone(OMCloneOptions options, OMContainer targetParent) {
        return factory.createOMDocType(targetParent, value);
    }
}
