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
package org.apache.axiom.ts.soap12.header;

import java.util.ArrayList;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

public class TestGetHeaderBlocksWithNSURIWithParser extends SOAPTestCase {
    public TestGetHeaderBlocksWithNSURIWithParser(OMMetaFactory metaFactory) {
        super(metaFactory, SOAPSpec.SOAP12);
    }

    protected void runTest() throws Throwable {
        ArrayList arrayList = getTestMessage(MESSAGE).getHeader().getHeaderBlocksWithNSURI(
                "http://example.org/ts-tests");
        assertTrue(
                "SOAP Header Test With Parser : - getHeaderBlocksWithNSURI returns an arrayList of incorrect size",
                arrayList.size() == 1);
        assertTrue(
                "SOAP Header Test With Parser : - headerBlock of given namespace uri, local name mismatch",
                ((SOAPHeaderBlock) arrayList.get(0)).getLocalName().equals(
                        "echoOk"));
        assertTrue(
                "SOAP Header Test With Parser : - headerBlock of given namespace uri, mismatch",
                ((SOAPHeaderBlock) arrayList.get(0)).getNamespace().getNamespaceURI()
                        .equals("http://example.org/ts-tests"));
    }
}
