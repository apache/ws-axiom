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

package org.apache.axiom.om;

/** All the various files created 03-Nov-2005 12:02:12 */

public class TestConstants {
    public static final String SOAP_SOAPMESSAGE = "soap/soapmessage.xml";
    public static final String SOAP_SOAPMESSAGE1 = "soap/soapmessage1.xml";
    public static final String SAMPLE1 = "soap/sample1.xml";
    public static final String TEST = "soap/test.xml";
    public static final String WHITESPACE_MESSAGE = "soap/whitespacedMessage.xml";
    public static final String MINIMAL_MESSAGE = "soap/minimalMessage.xml";
    public static final String REALLY_BIG_MESSAGE = "soap/reallyReallyBigMessage.xml";
    public static final String EMPTY_BODY_MESSAGE = "soap/emtyBodymessage.xml";

    public static final MIMEResource MTOM_MESSAGE = new MIMEResource("mtom/MTOMAttachmentStream.bin",
            "multipart/related; " +
            "boundary=\"MIMEBoundaryurn:uuid:A3ADBAEE51A1A87B2A11443668160701\"; " +
            "type=\"application/xop+xml\"; " +
            "start=\"<0.urn:uuid:A3ADBAEE51A1A87B2A11443668160702@apache.org>\"; " +
            "start-info=\"application/soap+xml\"; " +
            "charset=UTF-8;" +
            "action=\"mtomSample\"");
    public static final String MTOM_MESSAGE_INLINED = "mtom/MTOMAttachmentStream_inlined.xml";
    public static final String MTOM_MESSAGE_IMAGE1 = "mtom/img/test.jpg";
    public static final String MTOM_MESSAGE_IMAGE2 = "mtom/img/test2.jpg";
    
    public static final MIMEResource MTOM_MESSAGE_2 = new MIMEResource("mtom/MTOMBuilderTestIn.txt",
            "multipart/Related; charset=\"UTF-8\"; type=\"application/xop+xml\"; " +
            "boundary=\"----=_AxIs2_Def_boundary_=42214532\"; start=\"SOAPPart\"");
    
    public static final MIMEResource SWA_MESSAGE = new MIMEResource(
            "soap/soap11/SWAAttachmentStream.txt",
            "multipart/related; " +
            "boundary=\"MIMEBoundaryurn:uuid:A3ADBAEE51A1A87B2A11443668160701\"; " +
            "type=\"text/xml\"; " +
            "start=\"<0.urn:uuid:A3ADBAEE51A1A87B2A11443668160702@apache.org>\"");
    
    private TestConstants() {
    }


}
