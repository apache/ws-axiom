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

import javax.xml.namespace.QName;

import org.apache.axiom.core.CoreNode;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.impl.intf.soap12.AxiomSOAP12FaultText;
import org.apache.axiom.weaver.annotation.Mixin;

@Mixin(AxiomSOAP12FaultText.class)
public abstract class AxiomSOAP12FaultTextMixin implements AxiomSOAP12FaultText {
    private static final OMNamespace LANG_NAMESPACE = new OMNamespaceImpl(
            SOAP12Constants.SOAP_FAULT_TEXT_LANG_ATTR_NS_URI,
            SOAP12Constants.SOAP_FAULT_TEXT_LANG_ATTR_NS_PREFIX);
    private static final QName LANG_QNAME = new QName(
            SOAP12Constants.SOAP_FAULT_TEXT_LANG_ATTR_NS_URI,
            SOAP12Constants.SOAP_FAULT_TEXT_LANG_ATTR_LOCAL_NAME,
            SOAP12Constants.SOAP_FAULT_TEXT_LANG_ATTR_NS_PREFIX);
    
    @Override
    public final Class<? extends CoreNode> coreGetNodeClass() {
        return AxiomSOAP12FaultText.class;
    }

    public final boolean isChildElementAllowed(OMElement child) {
        return false;
    }

    @Override
    public final void setLang(String lang) {
        addAttribute(SOAP12Constants.SOAP_FAULT_TEXT_LANG_ATTR_LOCAL_NAME, lang, LANG_NAMESPACE);
    }
    
    @Override
    public final String getLang() {
        return getAttributeValue(LANG_QNAME);
    }
}
