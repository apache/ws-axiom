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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMSerializable;
import org.apache.axiom.om.impl.common.AxiomSemantics;
import org.apache.axiom.om.impl.intf.AxiomChildNode;
import org.apache.axiom.om.impl.intf.AxiomComment;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.intf.AxiomDocument;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class BuilderHandler {
    private static final Log log = LogFactory.getLog(BuilderHandler.class);
    
    public final NodeFactory nodeFactory;
    public final Model model;
    public AxiomContainer target;
    // returns the state of completion
    public boolean done;
    // keeps the state of the cache
    public boolean cache = true;
    public AxiomDocument document;
    
    /**
     * Tracks the depth of the node identified by {@link #target}. By definition, the level of the
     * root element is defined as 1. Note that if caching is disabled, then this depth may be
     * different from the actual depth reached by the underlying parser.
     */
    public int elementLevel;
    
    /**
     * Stores the stack trace of the code that caused a node to be discarded or consumed. This is
     * only used if debug logging was enabled when builder was created.
     */
    public Map<OMContainer,Throwable> discardTracker = log.isDebugEnabled() ? new LinkedHashMap<OMContainer,Throwable>() : null;
    
    private ArrayList<NodePostProcessor> nodePostProcessors;

    public BuilderHandler(NodeFactory nodeFactory, Model model) {
        this.nodeFactory = nodeFactory;
        this.model = model;
    }

    public void addNodePostProcessor(NodePostProcessor nodePostProcessor) {
        if (nodePostProcessors == null) {
            nodePostProcessors = new ArrayList<NodePostProcessor>();
        }
        nodePostProcessors.add(nodePostProcessor);
    }
    
    public void postProcessNode(OMSerializable node) {
        if (nodePostProcessors != null) {
            for (int i=0, size=nodePostProcessors.size(); i<size; i++) {
                nodePostProcessors.get(i).postProcessNode(node);
            }
        }
    }

    private void addChild(AxiomChildNode node) {
        target.addChild(node, true);
        postProcessNode(node);
    }
    
    public void createComment(String content) {
        AxiomComment node = nodeFactory.createNode(AxiomComment.class);
        node.coreSetCharacterData(content, AxiomSemantics.INSTANCE);
        addChild(node);
    }
}
