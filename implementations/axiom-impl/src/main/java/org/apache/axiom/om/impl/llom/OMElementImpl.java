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

import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.common.AxiomElement;

import java.util.Iterator;

/** Class OMElementImpl */
public class OMElementImpl extends OMNodeImpl
        implements AxiomElement, OMConstants {
    
    private int lineNumber;

    public OMElementImpl(OMFactory factory) {
        super(factory);
    }

    public void checkChild(OMNode child) {
    }

    public final void build() throws OMException {
        /**
         * builder is null. Meaning this is a programatical created element but it has children which are not completed
         * Build them all.
         */
        if (getBuilder() == null && getState() == INCOMPLETE) {
            for (Iterator childrenIterator = this.getChildren(); childrenIterator.hasNext();) {
                OMNode omNode = (OMNode) childrenIterator.next();
                omNode.build();
            }
        } else {
            defaultBuild();
        }

    }

///////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////

    public final void setNamespace(OMNamespace namespace) {
        setNamespace(namespace, true);
    }

    public final void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public final int getLineNumber() {
        return lineNumber;
    }
}

