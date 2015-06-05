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

import org.apache.axiom.dom.DOMDocumentType;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.AxiomDocType;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.w3c.dom.NamedNodeMap;

public class DocumentTypeImpl extends LeafNode implements DOMDocumentType, AxiomDocType {
    private final String rootName;
    private final String publicId;
    private final String systemId;
    private final String internalSubset;
    
    public DocumentTypeImpl(String rootName, String publicId, String systemId,
            String internalSubset, OMFactory factory) {
        super(factory);
        this.rootName = rootName;
        this.publicId = publicId;
        this.systemId = systemId;
        this.internalSubset = internalSubset;
    }

    public String getNodeName() {
        return getName();
    }

    public short getNodeType() {
        return DOCUMENT_TYPE_NODE;
    }

    public void internalSerialize(Serializer serializer, OMOutputFormat format, boolean cache) throws OutputException {
        serializer.writeDTD(rootName, publicId, systemId, internalSubset);
    }

    public int getType() {
        return DTD_NODE;
    }

    public NamedNodeMap getEntities() {
        throw new UnsupportedOperationException();
    }

    public String getInternalSubset() {
        return internalSubset;
    }

    public String getName() {
        return rootName;
    }

    public String getRootName() {
        return rootName;
    }

    public NamedNodeMap getNotations() {
        throw new UnsupportedOperationException();
    }

    public String getPublicId() {
        return publicId;
    }

    public String getSystemId() {
        return systemId;
    }

    ChildNode createClone() {
        return new DocumentTypeImpl(rootName, publicId, systemId, internalSubset, getOMFactory());
    }
}
