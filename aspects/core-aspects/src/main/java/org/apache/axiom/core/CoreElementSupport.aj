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

    private CoreAttribute CoreElement.accept(CoreAttribute attr, NodeMigrationPolicy policy) throws NodeMigrationException {
        boolean hasParent = attr.coreHasOwnerElement();
        boolean isForeignDocument = !coreHasSameOwnerDocument(attr);
        boolean isForeignModel = attr.coreGetNodeFactory() != coreGetNodeFactory();
        if (hasParent || isForeignDocument || isForeignModel) {
            switch (policy.getAction(hasParent, isForeignDocument, isForeignModel)) {
                case REJECT:
                    if (isForeignDocument) {
                        // Note that since isForeignModel implies isForeignDocument, we also get here
                        // if isForeignModel is true.
                        throw new WrongDocumentException();
                    } else {
                        // We get here if isForeignDocument and isForeignModel are false. Since at least
                        // one of the three booleans must be true, this implies that hasParent is true.
                        throw new NodeInUseException();
                    }
                case MOVE:
                    if (isForeignDocument || isForeignModel) {
                        // TODO
//                        throw new UnsupportedOperationException();
                        return attr;
                    } else {
                        attr.coreRemove();
                        return attr;
                    }
                case CLONE:
                    // TODO: probably we need to distinguish between cloning an attribute from the same model and importing it from another model (does that actually ever occur?)
                    throw new UnsupportedOperationException();
//                    return cloneAttribute(attr);
                default:
                    // Should never get here unless new values are added to the enum
                    throw new IllegalStateException();
            }
        } else {
            return attr;
        }
    }

    public final void CoreElement.coreAppendAttribute(CoreAttribute attr, NodeMigrationPolicy policy) throws NodeMigrationException {
        // TODO: we should probably check if the attribute is already owned by the element
        internalAppendAttribute(accept(attr, policy));
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
            CoreAttribute newAttr = matcher.createAttribute(coreGetNodeFactory(), namespaceURI, name, prefix, value);
            if (previousAttr == null) {
                internalAppendAttribute(newAttr);
            } else {
                previousAttr.insertAttributeAfter(newAttr);
            }
        } else {
            matcher.update(attr, prefix, value);
        }
    }
    
    public final CoreAttribute CoreElement.coreSetAttribute(AttributeMatcher matcher, CoreAttribute coreAttr, NodeMigrationPolicy policy, boolean changeDocumentOfReplacedAttribute, CoreDocument newDocument, ReturnValue returnValue) throws NodeMigrationException {
        if (coreAttr.coreGetOwnerElement() == this) {
            // TODO: document this and add assertion
            // TODO: take returnValue into account
            return coreAttr;
        }
        CoreAttribute attr = accept(coreAttr, policy);
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
        switch (returnValue) {
            case ADDED_ATTRIBUTE: return attr;
            case REPLACED_ATTRIBUTE: return existingAttr;
            default: return null;
        }
    }

    public final boolean CoreElement.coreRemoveAttribute(AttributeMatcher matcher, String namespaceURI, String name) {
        CoreAttribute att = coreGetAttribute(matcher, namespaceURI, name);
        if (att != null) {
            att.coreRemove();
            return true;
        } else {
            return false;
        }
    }

    public final <T extends CoreAttribute,S> Iterator<S> CoreElement.coreGetAttributesByType(Class<T> type, Mapper<T,S> mapper) {
        return AttributeIterator.create(this, type, mapper);
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
}
