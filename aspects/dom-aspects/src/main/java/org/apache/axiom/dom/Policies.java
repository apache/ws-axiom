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

import org.apache.axiom.core.AttributeMatcher;
import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.core.CoreAttribute;
import org.apache.axiom.core.CoreDocument;
import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.CoreNSUnawareAttribute;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.core.DetachPolicy;
import org.apache.axiom.core.NSAwareAttributeMatcher;
import org.apache.axiom.core.NamespaceDeclarationMatcher;
import org.apache.axiom.core.NodeType;

public final class Policies {
    private Policies() {}
    
    public static final DetachPolicy DETACH_POLICY = new DetachPolicy() {
        public CoreDocument getNewOwnerDocument(CoreParentNode owner) {
            return owner.coreGetOwnerDocument(true);
        }
    };
    
    /**
     * {@link AttributeMatcher} implementation that matches attributes based on their name, i.e.
     * based on the prefix and local name for namespace aware attributes. Parameters are defined as
     * follows:
     * <dl>
     * <dt><code>namespaceURI</code>
     * <dd>Not used.
     * <dt><code>name</code>
     * <dd>The qualified name of the attribute. This value may be in the form
     * <code>prefix:localName</code>.
     * <dt><code>value</code>
     * <dd>The attribute value.
     * <dt><code>prefix</code>
     * <dd>Not used.
     * </dl>
     */
    public static final AttributeMatcher DOM1_ATTRIBUTE_MATCHER = new AttributeMatcher() {
        public boolean matches(CoreAttribute attr, String namespaceURI, String name) {
            // Note: a lookup using DOM 1 methods may return any kind of attribute, including
            // namespace declarations
            return name.equals(((DOMAttribute)attr).getName());
        }

        public String getNamespaceURI(CoreAttribute attr) {
            return null;
        }

        public String getName(CoreAttribute attr) {
            return ((DOMAttribute)attr).getName();
        }

        public CoreAttribute createAttribute(CoreElement element, String namespaceURI, String name, String prefix, String value) {
            CoreNSUnawareAttribute attr = element.coreCreateNode(CoreNSUnawareAttribute.class);
            attr.coreSetName(name);
            attr.coreSetCharacterData(value, null);
            // TODO: set type?
            return attr;
        }

        public void update(CoreAttribute attr, String prefix, String value) {
            attr.coreSetCharacterData(value, DETACH_POLICY);
        }
    };
    
    public static final AttributeMatcher DOM2_ATTRIBUTE_MATCHER = new NSAwareAttributeMatcher(DETACH_POLICY, true, true);

    public static final AttributeMatcher NAMESPACE_DECLARATION_MATCHER = new NamespaceDeclarationMatcher(DETACH_POLICY);
    
    public static final ClonePolicy<Void> DEEP_CLONE = new ClonePolicy<Void>() {
        public Class<? extends CoreNode> getTargetNodeClass(Void options, CoreNode node) {
            // This is not specified by the API, but it's compatible with versions before 1.2.14
            return node.coreGetNodeClass();
        }

        public boolean repairNamespaces(Void options) {
            return false;
        }

        public boolean cloneAttributes(Void options) {
            return true;
        }

        public boolean cloneChildren(Void options, NodeType nodeType) {
            return true;
        }

        public void postProcess(Void options, CoreNode clone) {
        }
    };

    public static final ClonePolicy<Void> SHALLOW_CLONE = new ClonePolicy<Void>() {
        public Class<? extends CoreNode> getTargetNodeClass(Void options, CoreNode node) {
            // This is not specified by the API, but it's compatible with versions before 1.2.14
            return node.coreGetNodeClass();
        }

        public boolean repairNamespaces(Void options) {
            return false;
        }

        public boolean cloneAttributes(Void options) {
            return true;
        }

        public boolean cloneChildren(Void options, NodeType nodeType) {
            return nodeType == NodeType.NS_UNAWARE_ATTRIBUTE || nodeType == NodeType.NS_AWARE_ATTRIBUTE;
        }

        public void postProcess(Void options, CoreNode clone) {
        }
    };
}
