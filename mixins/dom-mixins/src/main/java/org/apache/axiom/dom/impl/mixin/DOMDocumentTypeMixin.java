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
package org.apache.axiom.dom.impl.mixin;

import org.apache.axiom.dom.DOMDocumentType;
import org.apache.axiom.weaver.annotation.Mixin;
import org.w3c.dom.NamedNodeMap;

@Mixin
public abstract class DOMDocumentTypeMixin implements DOMDocumentType {
    @Override
    public final short getNodeType() {
        return DOCUMENT_TYPE_NODE;
    }

    @Override
    public final String getNodeName() {
        return getName();
    }

    @Override
    public final String getNodeValue() {
        return null;
    }

    @Override
    public final void setNodeValue(String nodeValue) {
    }

    @Override
    public final String getName() {
        return coreGetRootName();
    }

    @Override
    public final String getPublicId() {
        return coreGetPublicId();
    }

    @Override
    public final String getSystemId() {
        return coreGetSystemId();
    }

    @Override
    public final String getInternalSubset() {
        return coreGetInternalSubset();
    }

    @Override
    public final NamedNodeMap getEntities() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final NamedNodeMap getNotations() {
        throw new UnsupportedOperationException();
    }
}
