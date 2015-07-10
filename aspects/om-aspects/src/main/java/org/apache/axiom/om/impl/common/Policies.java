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
package org.apache.axiom.om.impl.common;

import org.apache.axiom.core.AttributeMatcher;
import org.apache.axiom.core.NSAwareAttributeMatcher;
import org.apache.axiom.core.NodeMigrationPolicy;

public final class Policies {
    private Policies() {}
    
    public static final AttributeMatcher ATTRIBUTE_MATCHER = new NSAwareAttributeMatcher(
            false,  // Axiom doesn't support namespace unaware attributes
            false);

    public static final NodeMigrationPolicy ATTRIBUTE_MIGRATION_POLICY = new NodeMigrationPolicy() {
        public Action getAction(boolean hasParent, boolean isForeignDocument, boolean isForeignModel) {
            // TODO: doesn't look correct for foreign documents
            return Action.CLONE;
        }
    };
    
    public static final NodeMigrationPolicy NODE_MIGRATION_POLICY = new NodeMigrationPolicy() {
        public Action getAction(boolean hasParent, boolean isForeignDocument, boolean isForeignModel) {
            return isForeignModel ? Action.CLONE : Action.MOVE;
        }
    };
}
