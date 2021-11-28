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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.axiom.core.Builder;
import org.apache.axiom.core.CoreDocument;
import org.apache.axiom.core.CoreNSAwareElement;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.DeferredParsingException;
import org.apache.axiom.core.NodeFactory2;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;

final class BuilderHandler implements XmlHandler {
    final NodeFactory2 nodeFactory;
    final Model model;
    final Builder builder;
    final Object namespaceHelper;
    private final Context rootContext;
    private Context context;
    private int activeContextCount;
    // returns the state of completion
    private boolean done;
    private CoreDocument document;
    
    /**
     * Tracks the depth of the node identified by {@link #target}. By definition, the document has
     * depth 0. Note that if caching is disabled, then this depth may be different from the actual
     * depth reached by the underlying parser.
     */
    public int depth;
    
    private ArrayList<BuilderListener> listeners;
    private Queue<DeferredAction> deferredActions;

    BuilderHandler(NodeFactory2 nodeFactory, Model model, CoreNSAwareElement root, Builder builder) {
        this.nodeFactory = nodeFactory;
        this.model = model;
        this.builder = builder;
        namespaceHelper = nodeFactory.createNamespaceHelper();
        rootContext = root == null ? new BuildableContext(this, null, 0) : new UnwrappingContext(this, root);
        context = rootContext;
        activeContextCount = 1;
    }

    void addListener(BuilderListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<BuilderListener>();
        }
        listeners.add(listener);
    }
    
    void nodeAdded(CoreNode node) {
        if (node instanceof CoreDocument) {
            document = (CoreDocument)node;
        }
        if (listeners != null) {
            for (int i=0, size=listeners.size(); i<size; i++) {
                DeferredAction action = listeners.get(i).nodeAdded(node, depth);
                if (action != null) {
                    scheduleDeferredAction(action);
                }
            }
        }
    }

    private void scheduleDeferredAction(DeferredAction action) {
        if (deferredActions == null) {
            deferredActions = new LinkedList<>();
        }
        deferredActions.add(action);
    }

    void executeDeferredActions() throws DeferredParsingException {
        if (deferredActions != null) {
            DeferredAction action;
            while ((action = deferredActions.poll()) != null) {
                action.run();
            }
        }
    }
    
    void incrementActiveContextCount() {
        activeContextCount++;
    }
    
    void decrementActiveContextCount() {
        if (--activeContextCount == 0) {
            scheduleDeferredAction(() -> {
                while (!done) {
                    builder.next();
                }
            });
        }
    }
    
    boolean isCompleted() {
        return done;
    }
    
    CoreDocument getDocument() {
        if (rootContext instanceof UnwrappingContext) {
            throw new UnsupportedOperationException("There is no document linked to this builder");
        } else {
            return document;
        }
    }
    
    @Override
    public void startDocument(String inputEncoding, String xmlVersion, String xmlEncoding, Boolean standalone) {
        context.startDocument(inputEncoding, xmlVersion, xmlEncoding, standalone);
    }
    
    @Override
    public void startFragment() throws StreamException {
        context.startFragment();
    }

    @Override
    public void processDocumentTypeDeclaration(String rootName, String publicId, String systemId,
            String internalSubset) throws StreamException {
        context.processDocumentTypeDeclaration(rootName, publicId, systemId, internalSubset);
    }
    
    @Override
    public void startElement(String namespaceURI, String localName, String prefix) throws StreamException {
        depth++;
        context = context.startElement(namespaceURI, localName, prefix);
    }
    
    @Override
    public void endElement() throws StreamException {
        context = context.endElement();
        depth--;
    }

    @Override
    public void processAttribute(String namespaceURI, String localName, String prefix, String value, String type, boolean specified) throws StreamException {
        context.processAttribute(namespaceURI, localName, prefix, value, type, specified);
    }
    
    @Override
    public void processAttribute(String name, String value, String type, boolean specified) throws StreamException {
        context.processAttribute(name, value, type, specified);
    }
    
    @Override
    public void processNamespaceDeclaration(String prefix, String namespaceURI) throws StreamException {
        context.processNamespaceDeclaration(prefix, namespaceURI);
    }
    
    @Override
    public void attributesCompleted() throws StreamException {
        context.attributesCompleted();
    }
    
    @Override
    public void processCharacterData(Object data, boolean ignorable) throws StreamException {
        context.processCharacterData(data, ignorable);
    }
    
    @Override
    public void startProcessingInstruction(String target) throws StreamException {
        context = context.startProcessingInstruction(target);
    }

    @Override
    public void endProcessingInstruction() throws StreamException {
        context = context.endProcessingInstruction();
    }

    @Override
    public void startComment() throws StreamException {
        context = context.startComment();
    }

    @Override
    public void endComment() throws StreamException {
        context = context.endComment();
    }
    
    @Override
    public void startCDATASection() throws StreamException {
        context = context.startCDATASection();
    }

    @Override
    public void endCDATASection() throws StreamException {
        context = context.endCDATASection();
    }
    
    @Override
    public void processEntityReference(String name, String replacementText) throws StreamException {
        context.processEntityReference(name, replacementText);
    }
    
    @Override
    public void completed() throws StreamException {
        if (depth != 0) {
            throw new IllegalStateException();
        }
        context.completed();
        context = null;
        done = true;
    }

    @Override
    public boolean drain() throws StreamException {
        return true;
    }
}
