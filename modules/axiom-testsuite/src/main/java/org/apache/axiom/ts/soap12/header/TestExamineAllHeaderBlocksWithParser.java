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

import java.util.Iterator;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

public class TestExamineAllHeaderBlocksWithParser extends SOAPTestCase {
    public TestExamineAllHeaderBlocksWithParser(OMMetaFactory metaFactory) {
        super(metaFactory, SOAPSpec.SOAP12);
    }

    protected void runTest() throws Throwable {
        Iterator iterator = getTestMessage(MESSAGE).getHeader().examineAllHeaderBlocks();
        assertTrue(iterator.hasNext());
        SOAPHeaderBlock headerBlock1 = (SOAPHeaderBlock) iterator.next();
        assertTrue(
                "SOAP Header Test With Parser : - headerBlock1 localname mmismatch",
                headerBlock1.getLocalName().equals("echoOk"));
        assertTrue(iterator.hasNext());
        SOAPHeaderBlock headerBlock2 = (SOAPHeaderBlock) iterator.next();
        assertTrue(
                "SOAP Header Test With Parser : - headerBlock1 localname mmismatch",
                headerBlock2.getLocalName().equals("echoOk1"));
        assertTrue(iterator.hasNext());
        SOAPHeaderBlock headerBlock3 = (SOAPHeaderBlock) iterator.next();
        assertTrue(
                "SOAP Header Test With Parser : - headerBlock1 localname mmismatch",
                headerBlock3.getLocalName().equals("echoOk2"));

        assertFalse(
                "SOAP Header Test With Parser : - examineAllHeaderBlocks method returns an iterator with more than three objects",
                iterator.hasNext());
    }
}
