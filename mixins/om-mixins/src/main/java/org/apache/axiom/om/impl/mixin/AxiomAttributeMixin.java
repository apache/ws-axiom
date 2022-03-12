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
package org.apache.axiom.om.impl.mixin;

import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.common.AxiomExceptionTranslator;
import org.apache.axiom.om.impl.common.AxiomSemantics;
import org.apache.axiom.om.impl.common.NSUtil;
import org.apache.axiom.om.impl.intf.AxiomAttribute;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.weaver.annotation.Mixin;

@Mixin
public abstract class AxiomAttributeMixin implements AxiomAttribute {
    @Override
    public final OMElement getOwner() {
        return (OMElement) coreGetOwnerElement();
    }

    @Override
    public void beforeSetLocalName() {}

    @Override
    public final void setNamespace(OMNamespace namespace, boolean decl) {
        internalSetNamespace(
                NSUtil.handleNamespace((AxiomElement) getOwner(), namespace, true, decl));
    }

    @Override
    public final void setOMNamespace(OMNamespace omNamespace) {
        internalSetNamespace(omNamespace);
    }

    @Override
    public final String getAttributeValue() {
        try {
            return coreGetCharacterData().toString();
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }

    @Override
    public final void setAttributeValue(String value) {
        try {
            coreSetCharacterData(value, AxiomSemantics.INSTANCE);
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }

    @Override
    public final String getAttributeType() {
        return coreGetType();
    }

    @Override
    public final void setAttributeType(String type) {
        coreSetType(type);
    }

    public final void build() {
        // TODO
    }
}
