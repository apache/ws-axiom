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

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.intf.SOAPHelper;

public final class SOAPHeaderBlockHelper {
    private SOAPHeaderBlockHelper() {}

    public static String getAttributeValue(AxiomElement block, String key, QName qname) {
        // First, try getting the information from the property.
        // Fallback to getting the information from the attribute.
        if (block instanceof OMSourcedElement sourcedElement && !block.isExpanded()) {
            OMDataSource ds = sourcedElement.getDataSource();
            if (ds instanceof OMDataSourceExt dsExt && dsExt.hasProperty(key)) {
                return (String) dsExt.getProperty(key);
            }
        }
        return block.getAttributeValue(qname);
    }

    public static boolean getBooleanAttributeValue(
            AxiomElement block, SOAPHelper soapHelper, String key, QName qname) {
        String literal = getAttributeValue(block, key, qname);
        if (literal != null) {
            Boolean value = soapHelper.parseBoolean(literal);
            if (value != null) {
                return value.booleanValue();
            } else {
                throw new SOAPProcessingException(
                        "Invalid value for attribute "
                                + qname.getLocalPart()
                                + " in header block "
                                + block.getQName());
            }
        } else {
            return false;
        }
    }

    public static String getRole(AxiomElement block, SOAPHelper soapHelper) {
        return getAttributeValue(
                block, SOAPHeaderBlock.ROLE_PROPERTY, soapHelper.getRoleAttributeQName());
    }

    public static boolean getMustUnderstand(AxiomElement block, SOAPHelper soapHelper) {
        return getBooleanAttributeValue(
                block,
                soapHelper,
                SOAPHeaderBlock.MUST_UNDERSTAND_PROPERTY,
                soapHelper.getMustUnderstandAttributeQName());
    }
}
