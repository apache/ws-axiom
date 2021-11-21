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

import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.XmlReader;

/**
 * Interface for parent nodes.
 */
public interface CoreParentNode extends CoreNode {
    int COMPLETE = 0;
    int ATTRIBUTES_PENDING = 1;
    int INCOMPLETE = 2;
    
    /**
     * A request was made to discard the node, but some of the events corresponding to this node
     * have not been consumed yet.
     */
    int DISCARDING = 3;
    
    /**
     * The node has been discarded and all corresponding events have been consumed.
     */
    int DISCARDED = 4;
    
    int COMPACT = 5;
    
    Builder coreGetBuilder();
    void internalBuildNext() throws CoreModelException;
    InputContext coreGetInputContext();
    void coreSetInputContext(InputContext context);
    int getState();
    boolean isExpanded();
    void forceExpand();
    void coreSetState(int state);
    void coreBuild() throws CoreModelException;

    void serializeStartEvent(XmlHandler handler) throws CoreModelException, StreamException;
    void serializeEndEvent(XmlHandler handler) throws StreamException;
    void internalAppendChildWithoutBuild(CoreChildNode child);

    /**
     * Get the first child if it is available. The child is available if it is complete or
     * if the builder has started building the node. In the latter case,
     * {@link OMNode#isComplete()} may return <code>false</code> when called on the child. 
     * In contrast to {@link OMContainer#getFirstOMChild()}, this method will never modify
     * the state of the underlying parser.
     * 
     * @return the first child or <code>null</code> if the container has no children or
     *         the builder has not yet started to build the first child
     */
    CoreChildNode coreGetFirstChildIfAvailable();
    
    CoreChildNode coreGetFirstChild() throws CoreModelException;
    CoreChildNode coreGetFirstChild(NodeFilter filter) throws CoreModelException;
    
    CoreChildNode coreGetLastChild() throws CoreModelException;
    CoreChildNode coreGetLastChild(NodeFilter filter) throws CoreModelException;
    
    void coreAppendChild(CoreChildNode child) throws CoreModelException;
    void coreAppendChildren(CoreDocumentFragment fragment) throws CoreModelException;
    
    XmlReader coreGetReader(XmlHandler handler, boolean cache, boolean incremental);
    
    <T extends CoreNode,S> NodeIterator<S> coreGetNodes(Axis axis, Class<T> type, Mapper<S,? super T> mapper, Semantics semantics);

    <T extends CoreElement,S> NodeIterator<S> coreGetElements(Axis axis, Class<T> type, ElementMatcher<? super T> matcher, String namespaceURI, String name, Mapper<S,? super T> mapper, Semantics semantics);
    
    void coreSetCharacterData(Object data, Semantics semantics) throws CoreModelException;
    
    void coreRemoveChildren(Semantics semantics) throws CoreModelException;
    
    void coreDiscard(boolean consumeInput) throws CoreModelException;

    void coreMoveChildrenFrom(CoreParentNode other, Semantics semantics) throws CoreModelException;

    void internalCheckNewChild(CoreChildNode newChild, CoreChildNode replacedChild) throws CoreModelException;
    void internalSetContent(Object content);
    Object internalGetContent();
    Content internalGetContent(boolean create);
    Object internalGetCharacterData(ElementAction elementAction) throws CoreModelException;
}
