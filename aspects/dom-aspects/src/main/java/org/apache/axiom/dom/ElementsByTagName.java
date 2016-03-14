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
package org.apache.axiom.dom;

import java.util.Iterator;

import org.apache.axiom.core.Axis;
import org.apache.axiom.core.ElementMatcher;
import org.apache.axiom.core.Mappers;
import org.w3c.dom.Node;

public class ElementsByTagName extends NodeListImpl {
    private final DOMParentNode node;
    private final String tagname;
    
    public ElementsByTagName(DOMParentNode node, String tagname) {
        this.node = node;
        this.tagname = tagname;
    }

    @Override
    protected Iterator<? extends Node> createIterator() {
        if (tagname.equals("*")) {
            return node.coreGetElements(Axis.DESCENDANTS, DOMElement.class, ElementMatcher.ANY, null, null, Mappers.<Node>identity(), DOMSemantics.INSTANCE);
        } else {
            return node.coreGetElements(Axis.DESCENDANTS, DOMElement.class, ElementMatcher.BY_NAME, null, tagname, Mappers.<Node>identity(), DOMSemantics.INSTANCE);
        }
    }
}
