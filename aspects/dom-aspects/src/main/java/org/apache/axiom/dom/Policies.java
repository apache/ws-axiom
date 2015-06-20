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
import org.apache.axiom.core.CoreAttribute;
import org.apache.axiom.core.CoreDocument;
import org.apache.axiom.core.NSAwareAttributeMatcher;
import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.core.NodeMigrationPolicy;

public final class Policies {
    private Policies() {}
    
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

        public CoreAttribute createAttribute(NodeFactory nodeFactory, CoreDocument document, String namespaceURI, String name, String prefix, String value) {
            return nodeFactory.createAttribute(document, name, value, null);
        }

        public void update(CoreAttribute attr, String prefix, String value) {
            attr.coreSetValue(value);
        }
    };
    
    public static final AttributeMatcher DOM2_ATTRIBUTE_MATCHER = new NSAwareAttributeMatcher(true, true);

    public static final NodeMigrationPolicy ATTRIBUTE_MIGRATION_POLICY = new NodeMigrationPolicy() {
        public Action getAction(boolean hasParent, boolean isForeignDocument, boolean isForeignModel) {
            return Action.REJECT;
        }
    };
    
    public static final NodeMigrationPolicy NODE_MIGRATION_POLICY = new NodeMigrationPolicy() {
        public Action getAction(boolean hasParent, boolean isForeignDocument, boolean isForeignModel) {
            return isForeignDocument ? Action.REJECT : Action.MOVE;
        }
    };
}
