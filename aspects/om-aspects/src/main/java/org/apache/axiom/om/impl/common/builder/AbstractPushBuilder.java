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

import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;

public abstract class AbstractPushBuilder extends AbstractBuilder {
    public AbstractPushBuilder(NodeFactory nodeFactory, Model model, AxiomSourcedElement root) {
        super(nodeFactory, model, root);
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

    public final Object getParser() {
        throw new UnsupportedOperationException();
    }

    public final boolean isCompleted() {
        return handler.document != null && handler.document.isComplete();
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
}
