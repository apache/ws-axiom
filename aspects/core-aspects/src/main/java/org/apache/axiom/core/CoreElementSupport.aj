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
package org.apache.axiom.core;

import java.util.Iterator;

public aspect CoreElementSupport {
    private CoreAttribute CoreElement.firstAttribute;

    final void CoreElement.beforeDetach() {
        if (getState() == CoreParentNode.INCOMPLETE && getBuilder() == coreGetParent().getBuilder()) {
            build();
        }
    }
    
    public final CoreAttribute CoreElement.coreGetFirstAttribute() {
        forceExpand();
        return firstAttribute;
    }

    final void CoreElement.internalSetFirstAttribute(CoreAttribute firstAttribute) {
        this.firstAttribute = firstAttribute;
    }

    public final CoreAttribute CoreElement.coreGetLastAttribute() {
        CoreAttribute previousAttribute = null;
        CoreAttribute attribute = firstAttribute;
        while (attribute != null) {
            previousAttribute = attribute;
            attribute = attribute.coreGetNextAttribute();
        }
        return previousAttribute;
    }

    public final CoreAttribute CoreElement.coreGetAttribute(AttributeMatcher matcher, String namespaceURI, String name) {
        CoreAttribute attr = coreGetFirstAttribute();
        while (attr != null && !matcher.matches(attr, namespaceURI, name)) {
            attr = attr.coreGetNextAttribute();
        }
        return attr;
    }

    public final void CoreElement.coreAppendAttribute(CoreAttribute attr) {
        attr.coreRemove(DetachPolicy.NEW_DOCUMENT);
        internalAppendAttribute(attr);
    }

    private void CoreElement.internalAppendAttribute(CoreAttribute attr) {
        // TODO: we should probably check if the attribute is already owned by the element
        attr.internalSetOwnerElement(this);
        CoreAttribute lastAttribute = coreGetLastAttribute();
        if (lastAttribute == null) {
            firstAttribute = attr;
        } else {
            lastAttribute.insertAttributeAfter(attr);
        }
    }

    public final void CoreElement.coreSetAttribute(AttributeMatcher matcher, String namespaceURI, String name, String prefix, String value) {
        CoreAttribute attr = firstAttribute;
        CoreAttribute previousAttr = null;
        while (attr != null && !matcher.matches(attr, namespaceURI, name)) {
            previousAttr = attr;
            attr = attr.coreGetNextAttribute();
        }
        if (attr == null) {
            CoreAttribute newAttr = matcher.createAttribute(this, namespaceURI, name, prefix, value);
            if (previousAttr == null) {
                internalAppendAttribute(newAttr);
            } else {
                previousAttr.insertAttributeAfter(newAttr);
            }
        } else {
            matcher.update(attr, prefix, value);
        }
    }
    
    public final CoreAttribute CoreElement.coreSetAttribute(AttributeMatcher matcher, CoreAttribute attr, boolean changeDocumentOfReplacedAttribute, CoreDocument newDocument) {
        if (attr.coreGetOwnerElement() == this) {
            // TODO: document this and add assertion
            return attr;
        }
        attr.coreRemove(DetachPolicy.NEW_DOCUMENT);
        String namespaceURI = matcher.getNamespaceURI(attr);
        String name = matcher.getName(attr); 
        CoreAttribute existingAttr = coreGetFirstAttribute();
        CoreAttribute previousAttr = null;
        while (existingAttr != null && !matcher.matches(existingAttr, namespaceURI, name)) {
            previousAttr = existingAttr;
            existingAttr = existingAttr.coreGetNextAttribute();
        }
        attr.internalSetOwnerElement(this);
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
            existingAttr.internalUnsetOwnerElement(changeDocumentOfReplacedAttribute ? newDocument : coreGetOwnerDocument(true));
            attr.internalSetNextAttribute(existingAttr.coreGetNextAttribute());
            existingAttr.internalSetNextAttribute(null);
        }
        return existingAttr;
    }

    public final boolean CoreElement.coreRemoveAttribute(AttributeMatcher matcher, String namespaceURI, String name, DetachPolicy detachPolicy) {
        CoreAttribute att = coreGetAttribute(matcher, namespaceURI, name);
        if (att != null) {
            att.coreRemove(detachPolicy);
            return true;
        } else {
            return false;
        }
    }

    public final <T extends CoreAttribute,S> Iterator<S> CoreElement.coreGetAttributesByType(Class<T> type, Mapper<T,S> mapper, DetachPolicy detachPolicy) {
        return AttributeIterator.create(this, type, mapper, detachPolicy);
    }

    public abstract String CoreElement.getImplicitNamespaceURI(String prefix);
    
    public final String CoreElement.coreLookupNamespaceURI(String prefix, boolean strict) {
        if (!strict) {
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
            return parentElement.coreLookupNamespaceURI(prefix, strict);
        } else if (prefix.length() == 0) {
            return "";
        } else {
            return null;
        }
    }

    public abstract String CoreElement.getImplicitPrefix(String namespaceURI);
    
    public final String CoreElement.coreLookupPrefix(String namespaceURI, boolean strict) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("namespaceURI must not be null");
        }
        if (!strict) {
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
            String prefix = parentElement.coreLookupPrefix(namespaceURI, strict);
            // The prefix declared on one of the ancestors may be masked by another
            // namespace declaration on this element (or one of its descendants).
            if (!strict && getImplicitNamespaceURI(prefix) != null) {
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

    public final <T> void CoreElement.init(ClonePolicy<T> policy, T options, CoreNode other) {
        CoreElement o = (CoreElement)other;
        initSource(policy, options, o);
        initName(o);
        if (isExpanded()) {
            CoreAttribute attr = o.coreGetFirstAttribute();
            while (attr != null) {
                internalAppendAttribute((CoreAttribute)attr.coreClone(policy, options));
                // TODO: needed?
//                clonedAttr.coreSetSpecified(attr.coreGetSpecified());
                attr = attr.coreGetNextAttribute();
            }
        }
    }

    // This is basically a hook for OMSourcedElement
    public <T> void CoreElement.initSource(ClonePolicy<T> policy, T options, CoreElement other) {
    }
}
