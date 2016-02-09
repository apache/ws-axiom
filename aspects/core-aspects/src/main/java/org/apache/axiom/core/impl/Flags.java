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
package org.apache.axiom.core.impl;

import org.apache.axiom.core.CoreCharacterDataNode;
import org.apache.axiom.core.CoreChildNode;
import org.apache.axiom.core.CoreParentNode;

public final class Flags {
    private Flags() {}
    
    /**
     * Defines the bit mask for the part of the flags that indicate the state of a
     * {@link CoreParentNode}.
     */
    public static final int STATE_MASK = 7;

    /**
     * Used by {@link CoreChildNode} instances to indicate whether the node has a parent or not.
     * This is necessary to interpret the meaning of the <code>owner</code> attribute if it refers
     * to a document node (which may be the parent or simply the owner document).
     */
    public static final int HAS_PARENT = 8;
    
    public static final int DEFAULT_ATTR = 16;
    
    /**
     * Used to store the information returned by {@link CoreCharacterDataNode#coreIsIgnorable()}.
     */
    public static final int IGNORABLE = 32;
}
