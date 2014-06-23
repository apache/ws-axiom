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

/**
 * A set of two equivalent SOAP messages, one for SOAP 1.1 and one for SOAP 1.2.
 */
public final class TestMessageSet extends Adaptable {
    /**
     * A simple SOAP message without header.
     */
    public static final TestMessageSet NO_HEADER = new TestMessageSet("no-header");
    
    /**
     * A SOAP request with WS-Addressing headers.
     */
    public static final TestMessageSet WSA = new TestMessageSet("wsa");
    
    /**
     * A simple SOAP request with a comment in the XML prolog.
     */
    public static final TestMessageSet COMMENT_IN_PROLOG = new TestMessageSet("comment-in-prolog");
    
    /**
     * A SOAP response with a simple SOAP fault containing a fault code (without subcode), reason
     * (English only) and detail.
     */
    public static final TestMessageSet SIMPLE_FAULT = new TestMessageSet("simple-fault");
    
    private final TestMessage soap11Message;
    private final TestMessage soap12Message;
    
    private TestMessageSet(String name) {
        soap12Message = new SimpleTestMessage(SOAPSpec.SOAP12, "test-message/set/" + name + ".xml", "soap12/" + name);
        soap11Message = new ConvertedTestMessage(soap12Message, "soap11/" + name);
    }

    /**
     * Get the test message for the given SOAP version.
     * 
     * @param spec
     *            the SOAP specification version
     * @return the test message
     */
    public TestMessage getMessage(SOAPSpec spec) {
        return spec == SOAPSpec.SOAP11 ? soap11Message : soap12Message;
    }
}
