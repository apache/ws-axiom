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

package org.apache.axiom.om.impl.llom;

import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMInformationItem;
import org.apache.axiom.om.OMNode;

/** Class OMNodeImpl */
public abstract class OMNodeImpl extends OMSerializableImpl implements OMNode {
    /**
     * Constructor OMNodeImpl
     *
     * @param factory The <code>OMFactory</code> that created this
     */
    public OMNodeImpl(OMFactory factory) {
        super(factory);
    }

    /**
     * Method setParent.
     *
     * @param element
     */
    public void coreSetParent(CoreParentNode element) {
        CoreParentNode currentParent = coreGetParent();

        if (currentParent == element) {
            return;
        }

        //If we are asked to assign a new parent in place
        //of an existing one. We should detach this node
        //from the previous parent.
        if (element != null) {
            if (currentParent != null) {
                this.detach();
            }
            internalSetParent(element);
        } else {
            internalUnsetParent(null);
        }
    }

    public OMInformationItem clone(OMCloneOptions options) {
        return clone(options, null);
    }

    abstract OMNode clone(OMCloneOptions options, OMContainer targetParent);
}
