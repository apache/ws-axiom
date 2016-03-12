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

import org.apache.axiom.core.Builder;
import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.core.stream.NamespaceRepairingFilterHandler;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlInput;
import org.apache.axiom.core.stream.XmlReader;
import org.apache.axiom.om.DeferredParsingException;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.ds.custombuilder.CustomBuilder;
import org.apache.axiom.om.ds.custombuilder.CustomBuilderSupport;
import org.apache.axiom.om.ds.custombuilder.CustomBuilder.Selector;
import org.apache.axiom.om.impl.builder.Detachable;
import org.apache.axiom.om.impl.intf.AxiomDocument;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;

public class BuilderImpl implements OMXMLParserWrapper, Builder, CustomBuilderSupport {
    private final XmlReader reader;
    private final Detachable detachable;
    private final BuilderHandler builderHandler;
    private final CustomBuilderManager customBuilderManager = new CustomBuilderManager();

    public BuilderImpl(XmlInput input, NodeFactory nodeFactory, Model model,
            AxiomSourcedElement root, boolean repairNamespaces, Detachable detachable) {
        builderHandler = new BuilderHandler(nodeFactory, model, root, this);
        reader = input.createReader(repairNamespaces ? new NamespaceRepairingFilterHandler(builderHandler, null, false) : builderHandler);
        this.detachable = detachable;
        addListener(customBuilderManager);
    }

    public final void addListener(BuilderListener listener) {
        builderHandler.addListener(listener);
    }
    
    @Override
    public void registerCustomBuilder(Selector selector, CustomBuilder customBuilder) {
        customBuilderManager.register(selector, customBuilder);
    }
    
    @Override
    public void next() {
        if (isCompleted()) {
            throw new OMException();
        }
        try {
            reader.proceed();
        } catch (StreamException ex) {
            throw new DeferredParsingException(ex);
        }
        builderHandler.executeDeferredActions();
    }

    @Override
    public final boolean isCompleted() {
        return builderHandler.isCompleted();
    }

    @Override
    public final OMDocument getDocument() {
        AxiomDocument document;
        while ((document = builderHandler.getDocument()) == null) {
            next();
        }
        return document;
    }
    
    @Override
    public final OMElement getDocumentElement() {
        return getDocumentElement(false);
    }

    @Override
    public final OMElement getDocumentElement(boolean discardDocument) {
        OMDocument document = getDocument();
        OMElement element = document.getOMDocumentElement();
        if (discardDocument) {
            element.detach();
            ((AxiomDocument)document).coreDiscard(false);
        }
        return element;
    }

    @Override
    public final void close() {
        reader.dispose();
    }

    @Override
    public final void detach() throws OMException {
        if (detachable != null) {
            detachable.detach();
        } else {
            while (!isCompleted()) {
                next();
            }
        }
    }
}
