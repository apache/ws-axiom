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
package org.apache.axiom.soap.impl.common;

import javax.xml.namespace.QName;

import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.soap.SOAPCloneOptions;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.SOAPVersion;

public aspect AxiomSOAPHeaderBlockSupport {
    private boolean AxiomSOAPHeaderBlock.processed;

    public final SOAPVersion AxiomSOAPHeaderBlock.getVersion() {
        return getSOAPHelper().getVersion();
    }

    public final boolean AxiomSOAPHeaderBlock.isProcessed() {
        return processed;
    }

    public final void AxiomSOAPHeaderBlock.setProcessed() {
        processed = true;
    }

    private String AxiomSOAPHeaderBlock.getAttributeValue(String key, QName qname) {
        // First, try getting the information from the property.
        // Fallback to getting the information from the attribute.
        if (!isExpanded()) {
            OMDataSource ds = getDataSource();
            if (ds instanceof OMDataSourceExt) {
                OMDataSourceExt dsExt = (OMDataSourceExt)ds;
                if (dsExt.hasProperty(key)) {
                    return (String)dsExt.getProperty(key);
                }
            }
        }
        return getAttributeValue(qname);
    }
    
    private boolean AxiomSOAPHeaderBlock.getBooleanAttributeValue(String key, QName qname) {
        String literal = getAttributeValue(key, qname);
        if (literal != null) {
            Boolean value = getSOAPHelper().parseBoolean(literal);
            if (value != null) {
                return value.booleanValue();
            } else {
                throw new SOAPProcessingException(
                        "Invalid value for attribute " + qname.getLocalPart() + " in header block " + getQName());
            }
        } else {
            return false;
        }
    }
    
    public final boolean AxiomSOAPHeaderBlock.getMustUnderstand() throws SOAPProcessingException {
        return getBooleanAttributeValue(MUST_UNDERSTAND_PROPERTY, getSOAPHelper().getMustUnderstandAttributeQName());
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
        return getAttributeValue(ROLE_PROPERTY, getSOAPHelper().getRoleAttributeQName());
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
            return getBooleanAttributeValue(RELAY_PROPERTY, attributeQName);
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
