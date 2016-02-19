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
package org.apache.axiom.om.impl.common.serializer.push;

import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSerializable;

public abstract class SerializerImpl implements XmlHandler {
    /**
     * Constructor.
     * 
     * @param root
     *            the root node of the object model subtree that is being serialized; this
     *            information is used by the serializer in scenarios that require access to the
     *            namespace context of the parent of the root node
     * @param namespaceRepairing
     *            indicates if the serializer should perform namespace repairing
     * @param preserveNamespaceContext
     *            indicates if the namespace context determined by the ancestors of the root node
     *            should be strictly preserved in the output
     */
    public XmlHandler buildHandler(OMSerializable root, boolean namespaceRepairing, boolean preserveNamespaceContext) {
        OMElement contextElement;
        if (root instanceof OMNode) {
            OMContainer parent = ((OMNode)root).getParent();
            if (parent instanceof OMElement) {
                contextElement = (OMElement)parent; 
            } else {
                contextElement = null;
            }
        } else {
            contextElement = null;
        }
        XmlHandler handler = this;
        if (contextElement != null) {
            if (preserveNamespaceContext) {
                handler = new NamespaceContextPreservationFilterHandler(handler, (CoreElement)contextElement);
            } else {
                handler = new XsiTypeFilterHandler(handler, (CoreElement)contextElement);
            }
        }
        if (namespaceRepairing) {
            handler = new NamespaceHelper(this, handler);
        }
        return handler;
    }

    protected abstract boolean isAssociated(String prefix, String namespace) throws StreamException;
}
