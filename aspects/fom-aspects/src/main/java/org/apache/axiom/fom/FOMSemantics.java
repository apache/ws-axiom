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
package org.apache.axiom.fom;

import org.apache.axiom.core.AttributeMatcher;
import org.apache.axiom.core.DetachPolicy;
import org.apache.axiom.core.NSAwareAttributeMatcher;
import org.apache.axiom.core.NodeType;
import org.apache.axiom.core.Semantics;

public final class FOMSemantics implements Semantics {
    public static final FOMSemantics INSTANCE = new FOMSemantics();
    
    private FOMSemantics() {}
    
    public DetachPolicy getDetachPolicy() {
        return DetachPolicy.NEW_DOCUMENT;
    }
    
    public boolean isUseStrictNamespaceLookup() {
        return true;
    }

    public boolean isParentNode(NodeType nodeType) {
        // We don't use Axis.DESCENDANTS(_OR_SELF) anywhere
        throw new UnsupportedOperationException();
    }

    public static final AttributeMatcher ATTRIBUTE_MATCHER = new NSAwareAttributeMatcher(INSTANCE, false, false);
}
