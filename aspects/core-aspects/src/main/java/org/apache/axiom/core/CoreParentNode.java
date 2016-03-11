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
    InputContext coreGetInputContext();
    void coreSetInputContext(InputContext context);
    int getState();
    void coreSetState(int state);
    void coreBuild() throws CoreModelException;

    <T> NodeIterator<T> coreGetNodes(Axis axis, Mapper<? super CoreNode,T> mapper, Semantics semantics);

    <T extends CoreElement,S> NodeIterator<S> coreGetElements(Axis axis, Class<T> type, ElementMatcher<? super T> matcher, String namespaceURI, String name, Mapper<? super T,S> mapper, Semantics semantics);
}
