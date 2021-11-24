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
package org.apache.axiom.core.impl.mixin;

import java.util.Iterator;

import org.apache.axiom.core.AttributeMatcher;
import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.core.CoreAttribute;
import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.CoreNamespaceDeclaration;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.Mapper;
import org.apache.axiom.core.Semantics;
import org.apache.axiom.core.impl.AttributeIterator;
import org.apache.axiom.weaver.annotation.Mixin;

@Mixin
public abstract class CoreElementMixin implements CoreElement {
    private CoreAttribute firstAttribute;

    @Override
    public final CoreAttribute coreGetFirstAttribute() {
        forceExpand();
        return firstAttribute;
    }

    @Override
    public final void internalSetFirstAttribute(CoreAttribute firstAttribute) {
        this.firstAttribute = firstAttribute;
    }

    @Override
    public final CoreAttribute coreGetLastAttribute() {
        CoreAttribute previousAttribute = null;
        CoreAttribute attribute = firstAttribute;
        while (attribute != null) {
            previousAttribute = attribute;
            attribute = attribute.coreGetNextAttribute();
        }
        return previousAttribute;
    }

    @Override
    public final CoreAttribute coreGetAttribute(AttributeMatcher matcher, String namespaceURI, String name) {
        CoreAttribute attr = coreGetFirstAttribute();
        while (attr != null && !matcher.matches(attr, namespaceURI, name)) {
            attr = attr.coreGetNextAttribute();
        }
        return attr;
    }

    @Override
    public final void coreAppendAttribute(CoreAttribute attr) {
        // TODO: we should probably check if the attribute is already owned by the element
        attr.internalRemove(null, this);
        CoreAttribute lastAttribute = coreGetLastAttribute();
        if (lastAttribute == null) {
            firstAttribute = attr;
        } else {
            lastAttribute.internalSetNextAttribute(attr);
        }
    }

    @Override
    public final void coreSetAttribute(AttributeMatcher matcher, String namespaceURI, String name, String prefix, String value) throws CoreModelException {
        CoreAttribute attr = firstAttribute;
        CoreAttribute previousAttr = null;
        while (attr != null && !matcher.matches(attr, namespaceURI, name)) {
            previousAttr = attr;
            attr = attr.coreGetNextAttribute();
        }
        if (attr == null) {
            CoreAttribute newAttr = matcher.createAttribute(this, namespaceURI, name, prefix, value);
            if (previousAttr == null) {
                coreAppendAttribute(newAttr);
            } else {
                previousAttr.internalInsertAttributeAfter(newAttr);
            }
        } else {
            matcher.update(attr, prefix, value);
        }
    }
    
    @Override
    public final CoreAttribute coreSetAttribute(AttributeMatcher matcher, CoreAttribute attr, Semantics semantics) {
        if (attr.coreGetOwnerElement() == this) {
            // TODO: document this and add assertion
            return attr;
        }
        attr.internalRemove(null, this);
        String namespaceURI = matcher.getNamespaceURI(attr);
        String name = matcher.getName(attr); 
        CoreAttribute existingAttr = coreGetFirstAttribute();
        CoreAttribute previousAttr = null;
        while (existingAttr != null && !matcher.matches(existingAttr, namespaceURI, name)) {
            previousAttr = existingAttr;
            existingAttr = existingAttr.coreGetNextAttribute();
        }
        if (existingAttr == null) {
            if (previousAttr == null) {
                firstAttribute = attr;
            } else {
                previousAttr.internalSetNextAttribute(attr);
            }
        } else {
            if (previousAttr == null) {
                firstAttribute = attr;
            } else {
                previousAttr.internalSetNextAttribute(attr);
            }
            existingAttr.internalUnsetOwnerElement(semantics.getDetachPolicy().getNewOwnerDocument(this));
            attr.internalSetNextAttribute(existingAttr.coreGetNextAttribute());
            existingAttr.internalSetNextAttribute(null);
        }
        return existingAttr;
    }

    @Override
    public final boolean coreRemoveAttribute(AttributeMatcher matcher, String namespaceURI, String name, Semantics semantics) {
        CoreAttribute att = coreGetAttribute(matcher, namespaceURI, name);
        if (att != null) {
            att.coreRemove(semantics);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public final <T extends CoreAttribute,S> Iterator<S> coreGetAttributesByType(Class<T> type, Mapper<S,? super T> mapper, Semantics semantics) {
        return AttributeIterator.create(this, type, mapper, semantics);
    }

    public abstract String getImplicitNamespaceURI(String prefix);
    
    @Override
    public final String coreLookupNamespaceURI(String prefix, Semantics semantics) throws CoreModelException {
        if (!semantics.isUseStrictNamespaceLookup()) {
            String namespaceURI = getImplicitNamespaceURI(prefix);
            if (namespaceURI != null) {
                return namespaceURI;
            }
        }
        for (CoreAttribute attr = coreGetFirstAttribute(); attr != null; attr = attr.coreGetNextAttribute()) {
            if (attr instanceof CoreNamespaceDeclaration) {
                CoreNamespaceDeclaration decl = (CoreNamespaceDeclaration)attr;
                if (prefix.equals(decl.coreGetDeclaredPrefix())) {
                    return decl.coreGetCharacterData().toString();
                }
            }
        }
        CoreElement parentElement = coreGetParentElement();
        if (parentElement != null) {
            return parentElement.coreLookupNamespaceURI(prefix, semantics);
        } else if (prefix.length() == 0) {
            return "";
        } else {
            return null;
        }
    }

    public abstract String getImplicitPrefix(String namespaceURI);
    
    @Override
    public final String coreLookupPrefix(String namespaceURI, Semantics semantics) throws CoreModelException {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("namespaceURI must not be null");
        }
        if (!semantics.isUseStrictNamespaceLookup()) {
            String prefix = getImplicitPrefix(namespaceURI);
            if (prefix != null) {
                return prefix;
            }
        }
        for (CoreAttribute attr = coreGetFirstAttribute(); attr != null; attr = attr.coreGetNextAttribute()) {
            if (attr instanceof CoreNamespaceDeclaration) {
                CoreNamespaceDeclaration decl = (CoreNamespaceDeclaration)attr;
                if (decl.coreGetCharacterData().toString().equals(namespaceURI)) {
                    return decl.coreGetDeclaredPrefix();
                }
            }
        }
        CoreElement parentElement = coreGetParentElement();
        if (parentElement != null) {
            String prefix = parentElement.coreLookupPrefix(namespaceURI, semantics);
            // The prefix declared on one of the ancestors may be masked by another
            // namespace declaration on this element (or one of its descendants).
            if (!semantics.isUseStrictNamespaceLookup() && getImplicitNamespaceURI(prefix) != null) {
                return null;
            }
            for (CoreAttribute attr = coreGetFirstAttribute(); attr != null; attr = attr.coreGetNextAttribute()) {
                if (attr instanceof CoreNamespaceDeclaration) {
                    CoreNamespaceDeclaration decl = (CoreNamespaceDeclaration)attr;
                    if (decl.coreGetDeclaredPrefix().equals(prefix)) {
                        return null;
                    }
                }
            }
            return prefix;
        } else {
            return null;
        }
    }

    @Override
    public final <T> void init(ClonePolicy<T> policy, T options, CoreNode other) throws CoreModelException {
        CoreElement o = (CoreElement)other;
        initSource(policy, options, o);
        initName(o);
        if (isExpanded()) {
            CoreAttribute attr = o.coreGetFirstAttribute();
            while (attr != null) {
                coreAppendAttribute((CoreAttribute)attr.coreClone(policy, options));
                // TODO: needed?
//                clonedAttr.coreSetSpecified(attr.coreGetSpecified());
                attr = attr.coreGetNextAttribute();
            }
        }
    }

    // This is basically a hook for OMSourcedElement
    public <T> void initSource(ClonePolicy<T> policy, T options, CoreElement other) {
    }
    
    @Override
    public final <T extends CoreElement> T corePromote(Class<T> type, Semantics semantics) throws CoreModelException {
        T newElement = coreCreateNode(type);
        newElement.initName(this);
        newElement.internalSetFirstAttribute(firstAttribute);
        CoreAttribute attr = firstAttribute;
        while (attr != null) {
            attr.internalSetOwnerElement(newElement);
            attr = attr.coreGetNextAttribute();
        }
        firstAttribute = null;
        newElement.coreMoveChildrenFrom(this, semantics);
        coreReplaceWith(newElement, semantics);
        return newElement;
    }
}
