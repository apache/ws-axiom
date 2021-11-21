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

import org.apache.axiom.core.stream.XmlHandler;

public interface InputContext {
    Builder getBuilder();

    /**
     * Enables pass-through mode for this context. In this mode, events for the parent information
     * item linked to this context (and its descendants) are passed directly to the specified
     * handler instead of building nodes for them.
     * 
     * @param handler
     *            the handler to send events to
     * @throws IllegalStateException
     *             if a pass-through handler has already been set for this context
     */
    void setPassThroughHandler(XmlHandler passThroughHandler);

    void setTarget(CoreParentNode target);

    void discard();
}
