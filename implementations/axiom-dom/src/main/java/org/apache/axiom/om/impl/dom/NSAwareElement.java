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

import java.util.Iterator;

import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.dom.DOMConfigurationImpl;
import org.apache.axiom.dom.DOMNSAwareElement;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.common.AxiomContainer;
import org.apache.axiom.om.impl.common.AxiomElement;

/** Implementation of the org.w3c.dom.Element and org.apache.axiom.om.Element interfaces. */
public class NSAwareElement extends ElementImpl implements DOMNSAwareElement, AxiomElement,
        OMConstants {

    private int lineNumber;

    public NSAwareElement(OMFactory factory) {
        super(factory);
    }
    
    public NSAwareElement(ParentNode parentNode, String localName, OMNamespace ns, OMXMLParserWrapper builder,
                       OMFactory factory, boolean generateNSDecl) {
        super(factory);
        coreSetBuilder(builder);
        if (parentNode != null) {
            // TODO: dirty hack to get the correct semantics (reordering) if the parent is a SOAP envelope
            if (parentNode instanceof AxiomContainer) {
                ((AxiomContainer)parentNode).addChild(this, builder != null);
            } else {
                parentNode.coreAppendChild(this, builder != null);
            }
        }
        initName(localName, ns, generateNSDecl);
    }

    // /
    // /OmElement methods
    // /

    public void setNamespace(OMNamespace namespace) {
        setNamespace(namespace, true);
    }

    public OMElement cloneOMElement() {
        return (OMElement)clone(new OMCloneOptions());
    }

    @Override
    final ElementImpl createClone(OMCloneOptions options, ParentNode targetParent, ClonePolicy policy) {
        return (ElementImpl)shallowCloneWithoutAttributes(options, targetParent, policy.repairNamespaces());
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Returns the namespace uri, given the prefix. If it is not found at this element, searches the
     * parent.
     *
     * @param prefix
     * @return Returns namespace.
     */
    public String getNamespaceURI(String prefix) {
        OMNamespace ns = this.findNamespaceURI(prefix);
        return (ns != null) ? ns.getNamespaceURI() : null;
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

    public void normalize(DOMConfigurationImpl config) {
        if (config.isEnabled(DOMConfigurationImpl.NAMESPACES)) {
            OMNamespace namespace = getNamespace();
            if (namespace == null) {
                if (getDefaultNamespace() != null) {
                    declareDefaultNamespace("");
                }
            } else {
                OMNamespace namespaceForPrefix = findNamespaceURI(namespace.getPrefix());
                if (namespaceForPrefix == null || !namespaceForPrefix.getNamespaceURI().equals(namespace.getNamespaceURI())) {
                    declareNamespace(namespace);
                }
            }
        }
        super.normalize(config);
    }

    public final void build() {
        defaultBuild();
    }

    public final void checkChild(OMNode child) {
    }
}
