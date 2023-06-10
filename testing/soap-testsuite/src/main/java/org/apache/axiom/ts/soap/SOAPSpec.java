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
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.axiom.testing.multiton.Multiton;

/** Describes the characteristics of a given SOAP version. */
public abstract class SOAPSpec extends Multiton {
    public static final SOAPSpec SOAP11 =
            new SOAPSpec(
                    "soap11",
                    "text/xml",
                    "http://schemas.xmlsoap.org/soap/envelope/",
                    new BooleanLiteral[] {BooleanLiteral.ONE, BooleanLiteral.ZERO},
                    new QName("faultcode"),
                    null,
                    null,
                    new QName("faultstring"),
                    null,
                    null,
                    new QName("faultactor"),
                    new QName("detail"),
                    "http://schemas.xmlsoap.org/soap/actor/next",
                    new QName("http://schemas.xmlsoap.org/soap/envelope/", "Client"),
                    new QName("http://schemas.xmlsoap.org/soap/envelope/", "Server"),
                    new String[] {"soap-1.1.xsd"},
                    true) {
                @Override
                public SOAPSpec getAltSpec() {
                    return SOAPSpec.SOAP12;
                }

                @Override
                public String getCanonicalRepresentation(boolean value) {
                    return value ? "1" : "0";
                }
            };

    public static final SOAPSpec SOAP12 =
            new SOAPSpec(
                    "soap12",
                    "application/soap+xml",
                    "http://www.w3.org/2003/05/soap-envelope",
                    new BooleanLiteral[] {
                        BooleanLiteral.TRUE,
                        BooleanLiteral.FALSE,
                        BooleanLiteral.ONE,
                        BooleanLiteral.ZERO
                    },
                    new QName("http://www.w3.org/2003/05/soap-envelope", "Code"),
                    new QName("http://www.w3.org/2003/05/soap-envelope", "Value"),
                    new QName("http://www.w3.org/2003/05/soap-envelope", "Subcode"),
                    new QName("http://www.w3.org/2003/05/soap-envelope", "Reason"),
                    new QName("http://www.w3.org/2003/05/soap-envelope", "Text"),
                    new QName("http://www.w3.org/2003/05/soap-envelope", "Node"),
                    new QName("http://www.w3.org/2003/05/soap-envelope", "Role"),
                    new QName("http://www.w3.org/2003/05/soap-envelope", "Detail"),
                    "http://www.w3.org/2003/05/soap-envelope/role/next",
                    new QName("http://www.w3.org/2003/05/soap-envelope", "Sender"),
                    new QName("http://www.w3.org/2003/05/soap-envelope", "Receiver"),
                    new String[] {"xml.xsd", "soap-1.2.xsd"},
                    false) {
                @Override
                public SOAPSpec getAltSpec() {
                    return SOAPSpec.SOAP11;
                }

                @Override
                public String getCanonicalRepresentation(boolean value) {
                    return String.valueOf(value);
                }
            };

    private final String name;
    private final String contentType;
    private final String envelopeNamespaceURI;
    private final BooleanLiteral[] booleanLiterals;
    private final QName envelopeQName;
    private final QName headerQName;
    private final QName bodyQName;
    private final QName faultQName;
    private final QName faultCodeQName;
    private final QName faultValueQName;
    private final QName faultSubCodeQName;
    private final QName faultReasonQName;
    private final QName faultTextQName;
    private final QName faultNodeQName;
    private final QName faultRoleQName;
    private final QName faultDetailQName;
    private final String nextRoleURI;
    private final QName senderFaultCode;
    private final QName receiverFaultCode;
    private final String[] schemaResources;
    private Schema schema;
    private final boolean allowsElementsAfterBody;

    private SOAPSpec(
            String name,
            String contentType,
            String envelopeNamespaceURI,
            BooleanLiteral[] booleanLiterals,
            QName faultCodeQName,
            QName faultValueQName,
            QName faultSubCodeQName,
            QName faultReasonQName,
            QName faultTextQName,
            QName faultNodeQName,
            QName faultRoleQName,
            QName faultDetailQName,
            String nextRoleURI,
            QName senderFaultCode,
            QName receiverFaultCode,
            String[] schemaResources,
            boolean allowsElementsAfterBody) {
        this.name = name;
        this.contentType = contentType;
        this.envelopeNamespaceURI = envelopeNamespaceURI;
        this.booleanLiterals = booleanLiterals;
        envelopeQName = new QName(envelopeNamespaceURI, "Envelope");
        headerQName = new QName(envelopeNamespaceURI, "Header");
        bodyQName = new QName(envelopeNamespaceURI, "Body");
        faultQName = new QName(envelopeNamespaceURI, "Fault");
        this.faultCodeQName = faultCodeQName;
        this.faultValueQName = faultValueQName;
        this.faultSubCodeQName = faultSubCodeQName;
        this.faultReasonQName = faultReasonQName;
        this.faultTextQName = faultTextQName;
        this.faultNodeQName = faultNodeQName;
        this.faultRoleQName = faultRoleQName;
        this.faultDetailQName = faultDetailQName;
        this.nextRoleURI = nextRoleURI;
        this.senderFaultCode = senderFaultCode;
        this.receiverFaultCode = receiverFaultCode;
        this.schemaResources = schemaResources;
        this.allowsElementsAfterBody = allowsElementsAfterBody;
    }

    public final String getName() {
        return name;
    }

    /**
     * Get the {@link SOAPSpec} instance for the other SOAP version. This is useful when
     * constructing test cases that test SOAP version mismatches.
     *
     * @return the {@link SOAPSpec} instance for the other SOAP version
     */
    public abstract SOAPSpec getAltSpec();

    public final String getContentType() {
        return contentType;
    }

    public final String getEnvelopeNamespaceURI() {
        return envelopeNamespaceURI;
    }

    public final QName getEnvelopeQName() {
        return envelopeQName;
    }

    public final QName getHeaderQName() {
        return headerQName;
    }

    public final QName getBodyQName() {
        return bodyQName;
    }

    public final QName getFaultQName() {
        return faultQName;
    }

    public final QName getFaultCodeQName() {
        return faultCodeQName;
    }

    public final QName getFaultValueQName() {
        return faultValueQName;
    }

    public final QName getFaultSubCodeQName() {
        return faultSubCodeQName;
    }

    public final QName getFaultReasonQName() {
        return faultReasonQName;
    }

    public final QName getFaultTextQName() {
        return faultTextQName;
    }

    public final QName getFaultNodeQName() {
        return faultNodeQName;
    }

    public final QName getFaultRoleQName() {
        return faultRoleQName;
    }

    public final QName getFaultDetailQName() {
        return faultDetailQName;
    }

    /**
     * Get the boolean literals recognized by this SOAP version. While SOAP 1.2 refers to the {@code
     * xs:boolean} type and therefore recognizes {@code true}, {@code false}, {@code 1} and {@code
     * 0}, SOAP 1.1 only recognizes {@code 1} and {@code 0}.
     *
     * @return an array with the recognized boolean literals
     */
    public final BooleanLiteral[] getBooleanLiterals() {
        return booleanLiterals.clone();
    }

    private static List<String> getReps(BooleanLiteral[] literals) {
        List<String> result = new ArrayList<>(literals.length);
        for (BooleanLiteral literal : literals) {
            result.add(literal.getLexicalRepresentation());
        }
        return result;
    }

    /**
     * Produce a representative list of strings that are not valid lexical representations of
     * booleans as defined by this SOAP version. The list in particular contains:
     *
     * <ul>
     *   <li>Boolean literals allowed by the other SOAP version, but that are not valid in this SOAP
     *       version.
     *   <li>A variant of a valid boolean literal that uses a different case and that is not valid,
     *       unless no such variant exists.
     *   <li>The string <code>"invalid"</code>.
     * </ul>
     *
     * @return an array of invalid boolean literals
     */
    public final String[] getInvalidBooleanLiterals() {
        Set<String> result = new LinkedHashSet<>();
        result.addAll(getReps(getAltSpec().booleanLiterals));
        List<String> valid = getReps(booleanLiterals);
        result.removeAll(valid);
        result.add("invalid");
        if (valid.contains("true")) {
            result.add("TRUE");
        }
        return result.toArray(new String[result.size()]);
    }

    /**
     * Get the canonical representation for the given boolean value as specified by this SOAP
     * version.
     *
     * @param value the boolean value
     * @return the canonical representation
     */
    public abstract String getCanonicalRepresentation(boolean value);

    public final String getNextRoleURI() {
        return nextRoleURI;
    }

    public final QName getSenderFaultCode() {
        return senderFaultCode;
    }

    public final QName getReceiverFaultCode() {
        return receiverFaultCode;
    }

    public final synchronized Schema getSchema() {
        if (schema == null) {
            Source[] sources = new Source[schemaResources.length];
            for (int i = 0; i < schemaResources.length; i++) {
                sources[i] =
                        new StreamSource(
                                SOAPSpec.class.getResource("xsd/" + schemaResources[i]).toString());
            }
            try {
                schema =
                        SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema")
                                .newSchema(sources);
            } catch (Exception ex) {
                throw new Error(ex);
            }
        }
        return schema;
    }

    public final boolean isAllowsElementsAfterBody() {
        return allowsElementsAfterBody;
    }
}
