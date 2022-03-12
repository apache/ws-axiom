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

import org.apache.axiom.core.Builder;
import org.apache.axiom.core.CoreDocument;
import org.apache.axiom.core.CoreNSAwareElement;
import org.apache.axiom.core.DeferredParsingException;
import org.apache.axiom.core.NodeFactory2;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlInput;
import org.apache.axiom.core.stream.XmlReader;

public final class BuilderImpl implements Builder {
    private final XmlReader reader;
    private final BuilderHandler builderHandler;
    private Object facade;

    public BuilderImpl(
            XmlInput input, NodeFactory2 nodeFactory, Model model, CoreNSAwareElement root) {
        builderHandler = new BuilderHandler(nodeFactory, model, root, this);
        reader = input.createReader(builderHandler);
    }

    public void addListener(BuilderListener listener) {
        builderHandler.addListener(listener);
    }

    public Object getFacade() {
        return facade;
    }

    public void setFacade(Object facade) {
        this.facade = facade;
    }

    @Override
    public void next() throws DeferredParsingException {
        if (isCompleted()) {
            throw new IllegalStateException();
        }
        try {
            reader.proceed();
        } catch (StreamException ex) {
            throw new DeferredParsingException(ex);
        }
        builderHandler.executeDeferredActions();
    }

    @Override
    public boolean isCompleted() {
        return builderHandler.isCompleted();
    }

    public CoreDocument getDocument() throws DeferredParsingException {
        CoreDocument document;
        while ((document = builderHandler.getDocument()) == null) {
            next();
        }
        return document;
    }

    @Override
    public void close() {
        reader.dispose();
    }
}
