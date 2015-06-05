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

package org.apache.axiom.om.impl.dom;

import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.dom.DOMDocumentFragment;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;

public class DocumentFragmentImpl extends RootNode implements DOMDocumentFragment {

    public DocumentFragmentImpl(OMFactory factory) {
        super(factory);
    }

    public final NodeFactory coreGetNodeFactory() {
        return (NodeFactory)getOMFactory();
    }

    ParentNode shallowClone(OMCloneOptions options, ParentNode targetParent, boolean namespaceRepairing) {
        return new DocumentFragmentImpl(getOMFactory());
    }

    public final void build() {
        // A document fragment doesn't have a builder
    }

    public final void checkChild(OMNode child) {
    }
}
