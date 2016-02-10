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
import org.apache.axiom.core.builder.Builder;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.intf.AxiomDocument;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;
import org.apache.axiom.om.impl.stream.NamespaceRepairingFilterHandler;
import org.apache.axiom.om.impl.stream.XmlHandler;

public abstract class AbstractBuilder implements OMXMLParserWrapper, Builder, org.apache.axiom.om.impl.builder.Builder {
    protected final BuilderHandler builderHandler;
    protected final XmlHandler handler;

    public AbstractBuilder(NodeFactory nodeFactory, Model model, AxiomSourcedElement root, boolean repairNamespaces) {
        builderHandler = new BuilderHandler(nodeFactory, model, root, this);
        handler = repairNamespaces ? new NamespaceRepairingFilterHandler(builderHandler) : builderHandler;
    }

    public final void addNodePostProcessor(NodePostProcessor nodePostProcessor) {
        builderHandler.addNodePostProcessor(nodePostProcessor);
    }

    public final boolean isCompleted() {
        return builderHandler.isCompleted();
    }

    public final OMDocument getDocument() {
        AxiomDocument document;
        while ((document = builderHandler.getDocument()) == null) {
            next();
        }
        return document;
    }
}
