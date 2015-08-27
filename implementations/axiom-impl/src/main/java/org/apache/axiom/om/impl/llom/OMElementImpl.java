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

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.common.AxiomContainer;
import org.apache.axiom.om.impl.common.AxiomElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;

/** Class OMElementImpl */
public class OMElementImpl extends OMNodeImpl
        implements AxiomElement, OMConstants {

    private static final Log log = LogFactory.getLog(OMElementImpl.class);
    
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

    public final OMElement cloneOMElement() {
        
        if (log.isDebugEnabled()) {
            log.debug("cloneOMElement start");
            log.debug("  element string =" + getLocalName());
            log.debug(" isComplete = " + isComplete());
            log.debug("  builder = " + getBuilder());
        }
        return (OMElement)clone(new OMCloneOptions());
    }

    OMNode clone(OMCloneOptions options, AxiomContainer targetParent) {
        return defaultClone(options, targetParent);
    }
    
    final OMNode defaultClone(OMCloneOptions options, AxiomContainer targetParent) {
        AxiomElement targetElement = shallowCloneWithoutAttributes(options, targetParent, true);
        for (Iterator it = getAllDeclaredNamespaces(); it.hasNext(); ) {
            OMNamespace ns = (OMNamespace)it.next();
            targetElement.declareNamespace(ns);
        }
        for (Iterator it = getAllAttributes(); it.hasNext(); ) {
            OMAttribute attr = (OMAttribute)it.next();
            targetElement.addAttribute(attr);
        }
        for (Iterator it = getChildren(); it.hasNext(); ) {
            ((OMNodeImpl)it.next()).clone(options, targetElement);
        }
        return targetElement;
    }

    public final void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public final int getLineNumber() {
        return lineNumber;
    }

    /* (non-Javadoc)
      * @see org.apache.axiom.om.OMNode#buildAll()
      */
    public void buildWithAttachments() {
        if (getState() == INCOMPLETE) {
            this.build();
        }
        Iterator iterator = getChildren();
        while (iterator.hasNext()) {
            OMNode node = (OMNode) iterator.next();
            node.buildWithAttachments();
        }
    }
}

