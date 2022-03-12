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

import java.util.Iterator;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.impl.intf.AxiomSOAPElement;
import org.apache.axiom.soap.impl.intf.AxiomSOAPFaultDetail;
import org.apache.axiom.weaver.annotation.Mixin;

@Mixin
public abstract class AxiomSOAPFaultDetailMixin implements AxiomSOAPFaultDetail {
    @Override
    public final boolean isChildElementAllowed(OMElement child) {
        return !(child instanceof AxiomSOAPElement);
    }

    @Override
    public final void addDetailEntry(OMElement detailElement) {
        addChild(detailElement);
    }

    @Override
    public final Iterator<OMElement> getAllDetailEntries() {
        return getChildElements();
    }
}
