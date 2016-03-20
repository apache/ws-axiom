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

import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPCloneOptions;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.SOAPVersion;
import org.apache.axiom.soap.impl.common.SOAPHeaderBlockHelper;
import org.apache.axiom.soap.impl.intf.AxiomSOAPHeaderBlock;
import org.apache.axiom.soap.impl.intf.SOAPHelper;

public aspect AxiomSOAPHeaderBlockSupport {
    private boolean AxiomSOAPHeaderBlock.processed;

    public final boolean AxiomSOAPHeaderBlock.isChildElementAllowed(OMElement child) {
        return true;
    }

    public final SOAPVersion AxiomSOAPHeaderBlock.getVersion() {
        return getSOAPHelper().getVersion();
    }

    public final boolean AxiomSOAPHeaderBlock.isProcessed() {
        return processed;
    }

    public final void AxiomSOAPHeaderBlock.setProcessed() {
        processed = true;
    }

    public final boolean AxiomSOAPHeaderBlock.getMustUnderstand() throws SOAPProcessingException {
        return SOAPHeaderBlockHelper.getMustUnderstand(this, getSOAPHelper());
    }
    
    public final void AxiomSOAPHeaderBlock.setMustUnderstand(String mustUnderstand) throws SOAPProcessingException {
        SOAPHelper helper = getSOAPHelper();
        Boolean value = helper.parseBoolean(mustUnderstand);
        if (value != null) {
            _setAttributeValue(helper.getMustUnderstandAttributeQName(), mustUnderstand);
        } else {
            throw new SOAPProcessingException("Invalid value for mustUnderstand attribute");
        }
    }

    public final void AxiomSOAPHeaderBlock.setMustUnderstand(boolean mustUnderstand) {
        SOAPHelper helper = getSOAPHelper();
        _setAttributeValue(helper.getMustUnderstandAttributeQName(), helper.formatBoolean(mustUnderstand));
    }

    public final String AxiomSOAPHeaderBlock.getRole() {
        return SOAPHeaderBlockHelper.getRole(this, getSOAPHelper());
    }
    
    public final void AxiomSOAPHeaderBlock.setRole(String role) {
        _setAttributeValue(getSOAPHelper().getRoleAttributeQName(), role);
    }
    
    public final boolean AxiomSOAPHeaderBlock.getRelay() {
        SOAPHelper helper = getSOAPHelper();
        QName attributeQName = helper.getRelayAttributeQName();
        if (attributeQName == null) {
            throw new UnsupportedOperationException("Not supported for " + helper.getSpecName());
        } else {
            return SOAPHeaderBlockHelper.getBooleanAttributeValue(this, helper, RELAY_PROPERTY, attributeQName);
        }
    }
    
    public final void AxiomSOAPHeaderBlock.setRelay(boolean relay) {
        SOAPHelper helper = getSOAPHelper();
        QName attributeQName = helper.getRelayAttributeQName();
        if (attributeQName == null) {
            throw new UnsupportedOperationException("Not supported for " + helper.getSpecName());
        } else {
            _setAttributeValue(attributeQName, helper.formatBoolean(relay));
        }
    }

    public final <T> void AxiomSOAPHeaderBlock.initAncillaryData(ClonePolicy<T> policy, T options, CoreNode other) {
        // Copy the processed flag.  The other SOAPHeaderBlock information 
        // (e.g. role, mustUnderstand) are attributes on the tag and are copied elsewhere.
        Boolean processedFlag = options instanceof SOAPCloneOptions ? ((SOAPCloneOptions)options).getProcessedFlag() : null;
        if ((processedFlag == null && ((SOAPHeaderBlock)other).isProcessed()) || (processedFlag != null && processedFlag.booleanValue())) {
            setProcessed();
        }
    }
}
