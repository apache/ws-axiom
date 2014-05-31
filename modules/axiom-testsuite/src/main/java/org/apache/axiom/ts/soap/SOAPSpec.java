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
package org.apache.axiom.ts.soap;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP11Version;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAP12Version;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPVersion;

/**
 * Describes the characteristics of a given SOAP version. This is similar to {@link SOAPVersion},
 * but is designed specifically for the test suite.
 */
public abstract class SOAPSpec {
    public static final SOAPSpec SOAP11 = new SOAPSpec(SOAP11Version.getSingleton(),
            new BooleanLiteral[] { BooleanLiteral.ONE, BooleanLiteral.ZERO }, null) {
        public String getName() {
            return "soap11";
        }
        
        public SOAPSpec getAltSpec() {
            return SOAPSpec.SOAP12;
        }

        public SOAPFactory getFactory(OMMetaFactory metaFactory) {
            return metaFactory.getSOAP11Factory();
        }

        public String getEnvelopeNamespaceURI() {
            return SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI;
        }

        public String getCanonicalRepresentation(boolean value) {
            return value ? "1" : "0";
        }
    };

    public static final SOAPSpec SOAP12 = new SOAPSpec(SOAP12Version.getSingleton(),
            new BooleanLiteral[] { BooleanLiteral.TRUE, BooleanLiteral.FALSE, BooleanLiteral.ONE, BooleanLiteral.ZERO },
            new QName(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI, SOAP12Constants.SOAP_FAULT_TEXT_LOCAL_NAME)) {
        public String getName() {
            return "soap12";
        }
        
        public SOAPSpec getAltSpec() {
            return SOAPSpec.SOAP11;
        }

        public SOAPFactory getFactory(OMMetaFactory metaFactory) {
            return metaFactory.getSOAP12Factory();
        }

        public String getEnvelopeNamespaceURI() {
            return SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI;
        }

        public String getCanonicalRepresentation(boolean value) {
            return String.valueOf(value);
        }
    };

    private final SOAPVersion version;
    private final BooleanLiteral[] booleanLiterals;
    private final QName envelopeQName;
    private final QName faultTextQName;
    
    public SOAPSpec(SOAPVersion version, BooleanLiteral[] booleanLiterals, QName faultTextQName) {
        this.version = version;
        this.booleanLiterals = booleanLiterals;
        envelopeQName = new QName(getEnvelopeNamespaceURI(), SOAPConstants.SOAPENVELOPE_LOCAL_NAME);
        this.faultTextQName = faultTextQName;
    }
    
    public abstract String getName();
    
    /**
     * Get the {@link SOAPSpec} instance for the other SOAP version. This is useful when
     * constructing test cases that test SOAP version mismatches.
     * 
     * @return the {@link SOAPSpec} instance for the other SOAP version
     */
    public abstract SOAPSpec getAltSpec();
    
    public abstract SOAPFactory getFactory(OMMetaFactory metaFactory);
    
    public final SOAPFactory getAltFactory(OMMetaFactory metaFactory) {
        return getAltSpec().getFactory(metaFactory);
    }
    
    public abstract String getEnvelopeNamespaceURI();
    
    public final QName getEnvelopeQName() {
        return envelopeQName;
    }

    public final QName getFaultCodeQName() {
        return version.getFaultCodeQName();
    }
    
    public final QName getFaultReasonQName() {
        return version.getFaultReasonQName();
    }

    public final QName getFaultRoleQName() {
        return version.getFaultRoleQName();
    }

    public final QName getFaultDetailQName() {
        return version.getFaultDetailQName();
    }
    
    public final QName getFaultTextQName() {
        return faultTextQName;
    }

    /**
     * Get the boolean literals recognized by this SOAP version. While SOAP 1.2 refers to the
     * <tt>xs:boolean</tt> type and therefore recognizes <tt>true</tt>, <tt>false</tt>, <tt>1</tt>
     * and <tt>0</tt>, SOAP 1.1 only recognizes <tt>1</tt> and <tt>0</tt>.
     * 
     * @return an array with the recognized boolean literals
     */
    public final BooleanLiteral[] getBooleanLiterals() {
        return (BooleanLiteral[])booleanLiterals.clone();
    }

    private static List getReps(BooleanLiteral[] literals) {
        List result = new ArrayList(literals.length);
        for (int i=0; i<literals.length; i++) {
            result.add(literals[i].getLexicalRepresentation());
        }
        return result;
    }
    
    /**
     * Produce a representative list of strings that are not valid lexical representations of
     * booleans as defined by this SOAP version. The list in particular contains:
     * <ul>
     * <li>Boolean literals allowed by the other SOAP version, but that are not valid in this SOAP
     * version.
     * <li>A variant of a valid boolean literal that uses a different case and that is not valid,
     * unless no such variant exists.
     * <li>The string <code>"invalid"</code>.
     * </ul>
     * 
     * @return an array of invalid boolean literals
     */
    public final String[] getInvalidBooleanLiterals() {
        Set result = new LinkedHashSet();
        result.addAll(getReps(getAltSpec().booleanLiterals));
        List valid = getReps(booleanLiterals);
        result.removeAll(valid);
        result.add("invalid");
        if (valid.contains("true")) {
            result.add("TRUE");
        }
        return (String[])result.toArray(new String[result.size()]);
    }
    
    /**
     * Get the canonical representation for the given boolean value as specified by this SOAP
     * version.
     * 
     * @param value
     *            the boolean value
     * @return the canonical representation
     */
    public abstract String getCanonicalRepresentation(boolean value);
    
    public final String getNextRoleURI() {
        return version.getNextRoleURI();
    }

    public final SOAPVersion getVersion() {
        return version;
    }
}
