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

import org.apache.axiom.testing.multiton.Multiton;

/** A set of two equivalent SOAP messages, one for SOAP 1.1 and one for SOAP 1.2. */
public final class SOAPSampleSet extends Multiton {
    /** A simple SOAP message without header. */
    public static final SOAPSampleSet NO_HEADER = new SOAPSampleSet("no-header");

    /** A SOAP request with WS-Addressing headers. */
    public static final SOAPSampleSet WSA = new SOAPSampleSet("wsa");

    /** A simple SOAP request with a comment in the XML prolog. */
    public static final SOAPSampleSet COMMENT_IN_PROLOG = new SOAPSampleSet("comment-in-prolog");

    /**
     * A SOAP response with a simple SOAP fault containing a fault code (without subcode), reason
     * (English only) and detail.
     */
    public static final SOAPSampleSet SIMPLE_FAULT = new SOAPSampleSet("simple-fault");

    /** A SOAP request having a header block with a custom role. */
    public static final SOAPSampleSet CUSTOM_ROLE_REQUEST =
            new SOAPSampleSet("custom-role-request");

    /** A SOAP fault response with a custom role (corresponding to {@link #CUSTOM_ROLE_REQUEST}). */
    public static final SOAPSampleSet CUSTOM_ROLE_FAULT = new SOAPSampleSet("custom-role-fault");

    /** A SOAP request with a single mustUnderstand header. */
    public static final SOAPSampleSet MUST_UNDERSTAND = new SOAPSampleSet("must-understand");

    /** A SOAP fault response with a detail entry that uses a default namespace. */
    public static final SOAPSampleSet FAULT_DETAIL_DEFAULT_NAMESPACE =
            new SOAPSampleSet("fault-detail-default-namespace");

    /** A SOAP request with header blocks with varying characteristics. */
    public static final SOAPSampleSet HEADERS = new SOAPSampleSet("headers");

    /**
     * A SOAP request with an {@code xsi:type} attribute in the payload and a corresponding
     * namespace declaration on the SOAP envelope (rather than in the payload).
     */
    public static final SOAPSampleSet XSI_TYPE = new SOAPSampleSet("xsi-type");

    private final SOAPSample soap11Message;
    private final SOAPSample soap12Message;

    private SOAPSampleSet(String name) {
        soap12Message =
                new SimpleSOAPSample(
                        SOAPSpec.SOAP12, "test-message/set/" + name + ".xml", "soap12/" + name);
        soap11Message =
                new SOAPSample(
                        SOAPSpec.SOAP11,
                        new ConvertedSOAPSampleContent(soap12Message),
                        "soap11/" + name);
    }

    /**
     * Get the test message for the given SOAP version.
     *
     * @param spec the SOAP specification version
     * @return the test message
     */
    public SOAPSample getMessage(SOAPSpec spec) {
        return spec == SOAPSpec.SOAP11 ? soap11Message : soap12Message;
    }
}
