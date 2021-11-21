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
package org.apache.axiom.soap.impl.mixin;

import org.apache.axiom.core.CoreNode;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.impl.intf.soap12.AxiomSOAP12FaultNode;
import org.apache.axiom.weaver.annotation.Mixin;

@Mixin(AxiomSOAP12FaultNode.class)
public abstract class AxiomSOAP12FaultNodeMixin implements AxiomSOAP12FaultNode {
    @Override
    public final Class<? extends CoreNode> coreGetNodeClass() {
        return AxiomSOAP12FaultNode.class;
    }

    public final boolean isChildElementAllowed(OMElement child) {
        return false;
    }

    @Override
    public final void setFaultNodeValue(String uri) {
        setText(uri);
    }

    @Override
    public final String getFaultNodeValue() {
        return getText();
    }
}
