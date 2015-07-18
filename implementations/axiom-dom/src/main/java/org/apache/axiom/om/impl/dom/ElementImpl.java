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

import static org.apache.axiom.dom.DOMExceptionUtil.newDOMException;

import org.apache.axiom.core.AttributeMatcher;
import org.apache.axiom.core.NodeMigrationException;
import org.apache.axiom.core.NodeMigrationPolicy;
import org.apache.axiom.dom.DOMAttribute;
import org.apache.axiom.dom.DOMConfigurationImpl;
import org.apache.axiom.dom.DOMExceptionUtil;
import org.apache.axiom.dom.DOMNSAwareElement;
import org.apache.axiom.dom.Policies;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.common.AxiomContainer;
import org.apache.axiom.om.impl.common.AxiomElement;
import org.apache.axiom.om.impl.util.OMSerializerUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;

import java.util.Iterator;

/** Implementation of the org.w3c.dom.Element and org.apache.axiom.om.Element interfaces. */
public class ElementImpl extends ParentNode implements DOMNSAwareElement, AxiomElement,
        OMConstants {

    private int lineNumber;

    public ElementImpl(ParentNode parentNode, String localName, OMNamespace ns, OMXMLParserWrapper builder,
                       OMFactory factory, boolean generateNSDecl) {
        super(factory);
        internalSetLocalName(localName);
        coreSetBuilder(builder);
        coreSetState(builder == null ? COMPLETE : INCOMPLETE);
        if (parentNode != null) {
            // TODO: dirty hack to get the correct semantics (reordering) if the parent is a SOAP envelope
            if (parentNode instanceof AxiomContainer) {
                ((AxiomContainer)parentNode).addChild(this, builder != null);
            } else {
                parentNode.coreAppendChild(this, builder != null);
            }
        }
        internalSetNamespace(generateNSDecl ? handleNamespace(this, ns, false, true) : ns);
    }

    private final String checkNamespaceIsDeclared(String prefix, String namespaceURI, boolean allowDefaultNamespace, boolean declare) {
        if (prefix == null) {
            if (namespaceURI.length() == 0) {
                prefix = "";
                declare = false;
            } else {
                prefix = coreLookupPrefix(namespaceURI, true);
                if (prefix != null && (allowDefaultNamespace || prefix.length() != 0)) {
                    declare = false;
                } else {
                    prefix = OMSerializerUtil.getNextNSPrefix();
                }
            }
        } else {
            String existingNamespaceURI = coreLookupNamespaceURI(prefix, true);
            declare = declare && !namespaceURI.equals(existingNamespaceURI);
        }
        if (declare) {
            coreSetAttribute(Policies.NAMESPACE_DECLARATION_MATCHER, null, prefix, null, namespaceURI);
        }
        return prefix;
    }

    // /
    // /org.w3c.dom.Node methods
    // /

    public String getTagName() {
        OMNamespace namespace = getNamespace();
        String localName = getLocalName();
        if (namespace != null) {
            if (namespace.getPrefix() == null
                    || "".equals(namespace.getPrefix())) {
                return localName;
            } else {
                return namespace.getPrefix() + ":" + localName;
            }
        } else {
            return localName;
        }
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

    final ParentNode shallowClone(OMCloneOptions options, ParentNode targetParent, boolean namespaceRepairing) {
        ElementImpl clone;
        if (options.isPreserveModel()) {
            clone = (ElementImpl)createClone(options, targetParent, namespaceRepairing);
        } else {
            clone = new ElementImpl(targetParent, getLocalName(), getNamespace(), null, getOMFactory(), namespaceRepairing);
        }
        NamedNodeMap attributes = getAttributes();
        for (int i=0, l=attributes.getLength(); i<l; i++) {
            AttrImpl attr = (AttrImpl)attributes.item(i);
            AttrImpl clonedAttr = (AttrImpl)attr.clone(options, null, true, false);
            clonedAttr.coreSetSpecified(attr.coreGetSpecified());
            if (namespaceRepairing && attr instanceof NSAwareAttribute) {
                NSAwareAttribute nsAwareAttr = (NSAwareAttribute)attr;
                String namespaceURI = nsAwareAttr.coreGetNamespaceURI();
                if (namespaceURI.length() != 0) {
                    clone.checkNamespaceIsDeclared(nsAwareAttr.coreGetPrefix(), namespaceURI, false, true);
                }
            }
            try {
                clone.coreAppendAttribute(clonedAttr, NodeMigrationPolicy.MOVE_ALWAYS);
            } catch (NodeMigrationException ex) {
                DOMExceptionUtil.translate(ex);
            }
        }
        return clone;
    }

    protected OMElement createClone(OMCloneOptions options, ParentNode targetParent, boolean generateNSDecl) {
        return new ElementImpl(targetParent, getLocalName(), getNamespace(), null, getOMFactory(), generateNSDecl);
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

    /*
     * DOM-Level 3 methods
     */

    public void setIdAttribute(String name, boolean isId) throws DOMException {
        //find the attr
        AttrImpl tempAttr = (AttrImpl) this.getAttributeNode(name);
        if (tempAttr == null) {
            throw newDOMException(DOMException.NOT_FOUND_ERR);
        }

        this.updateIsId(isId, tempAttr);
    }

    public void setIdAttributeNS(String namespaceURI, String localName, boolean isId)
            throws DOMException {
        //find the attr
        AttrImpl tempAttr = (AttrImpl) this.getAttributeNodeNS(namespaceURI, localName);
        if (tempAttr == null) {
            throw newDOMException(DOMException.NOT_FOUND_ERR);
        }

        this.updateIsId(isId, tempAttr);
    }

    public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
        //find the attr
        if (((DOMAttribute)idAttr).coreGetOwnerElement() != this) {
            throw newDOMException(DOMException.NOT_FOUND_ERR);
        }
        this.updateIsId(isId, (AttrImpl)idAttr);
    }

    /**
     * Updates the id state of the attr and notifies the document
     *
     * @param isId
     * @param tempAttr
     */
    private void updateIsId(boolean isId, AttrImpl tempAttr) {
        if (tempAttr.isId != isId) {
            tempAttr.isId = isId;
            if (isId) {
                ownerDocument().addIdAttr(tempAttr);
            } else {
                ownerDocument().removeIdAttr(tempAttr);
            }
        }
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

    public final void setComplete(boolean complete) {
        coreSetState(complete ? COMPLETE : INCOMPLETE);
        ParentNode parentNode = (ParentNode)coreGetParent();
        if (parentNode != null) {
            if (!complete) {
                ((DOMContainer)parentNode).setComplete(false);
            } else {
                ((DOMContainer)parentNode).notifyChildComplete();
            }
        }
    }

    public final void build() {
        defaultBuild();
    }

    public final void checkChild(OMNode child) {
    }
}
