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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.intf.AxiomSOAPElement;
import org.apache.axiom.soap.impl.intf.SOAPHelper;
import org.apache.axiom.weaver.annotation.Mixin;

@Mixin
public abstract class AxiomSOAPElementMixin implements AxiomSOAPElement {
    @Override
    public final OMFactory getOMFactory() {
        return getSOAPHelper().getSOAPFactory(getMetaFactory());
    }

    @Override
    public final void checkChild(OMNode child) {
        if (child instanceof OMElement element && !isChildElementAllowed(element)) {
            throw new SOAPProcessingException(
                    child.getClass().getName()
                            + " is not allowed as a child of "
                            + getClass().getName());
        }
        if (child instanceof AxiomSOAPElement soapElement) {
            SOAPHelper soapHelper = getSOAPHelper();
            SOAPHelper childSOAPHelper = soapElement.getSOAPHelper();
            if (childSOAPHelper != soapHelper) {
                throw new SOAPProcessingException(
                        "Cannot add a "
                                + childSOAPHelper.getSpecName()
                                + " element as a child of a "
                                + soapHelper.getSpecName()
                                + " element");
            }
        }
    }
}
