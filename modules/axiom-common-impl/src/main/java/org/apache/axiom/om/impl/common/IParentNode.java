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
package org.apache.axiom.om.impl.common;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.w3c.dom.Attr;
import org.w3c.dom.DocumentFragment;

/**
 * Interface for parent nodes. This interface is implemented by {@link OMContainer} implementations
 * as well as nodes that can have children, but that are not {@link OMContainer} implementations
 * (such as DOOM's {@link Attr} and {@link DocumentFragment} implementations).
 */
public interface IParentNode {
    int INCOMPLETE = 0;
    int COMPLETE = 1;
    int DISCARDED = 2;
    
    OMXMLParserWrapper getBuilder();
    int getState();
    boolean isComplete();
    void buildNext();

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
    public OMNode getFirstOMChildIfAvailable();
}
