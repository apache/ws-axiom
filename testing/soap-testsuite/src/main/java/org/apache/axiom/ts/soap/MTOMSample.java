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

import org.apache.axiom.ts.xml.MessageContent;
import org.apache.axiom.ts.xml.XOPSample;

public class MTOMSample extends XOPSample {
    public static final MTOMSample SAMPLE1 =
            new MTOMSample(
                    "sample1.msg",
                    "multipart/related; "
                            + "boundary=\"MIMEBoundaryurn:uuid:A3ADBAEE51A1A87B2A11443668160701\"; "
                            + "type=\"application/xop+xml\"; "
                            + "start=\"<0.urn:uuid:A3ADBAEE51A1A87B2A11443668160702@apache.org>\"; "
                            + "start-info=\"application/soap+xml\"; "
                            + "charset=UTF-8;"
                            + "action=\"mtomSample\"");

    public static final MTOMSample SAMPLE2 =
            new MTOMSample(
                    "sample2.msg",
                    "multipart/Related; charset=\"UTF-8\"; type=\"application/xop+xml\"; "
                            + "boundary=\"----=_AxIs2_Def_boundary_=42214532\"; start=\"SOAPPart\"");

    /**
     * MTOM message with a MIME part encoded as quoted-printable. That content transfer encoding is
     * unusual for MTOM messages, but it is used by SOAPUI. This message is used in regression tests
     * for <a href="https://issues.apache.org/jira/browse/AXIOM-467">AXIOM-467</a>.
     */
    public static final MTOMSample QUOTED_PRINTABLE =
            new MTOMSample(
                    "quoted-printable.msg",
                    "multipart/related; "
                            + "type=\"application/xop+xml\"; "
                            + "start=\"<rootpart@soapui.org>\"; "
                            + "start-info=\"application/soap+xml\"; "
                            + "action=\"urn:receive\"; "
                            + "boundary=\"----=_Part_542_1447667749.1430736561148\"");

    private MTOMSample(String name, String contentType) {
        super(MessageContent.fromClasspath(MTOMSample.class, "mtom/" + name), name, contentType);
    }
}
