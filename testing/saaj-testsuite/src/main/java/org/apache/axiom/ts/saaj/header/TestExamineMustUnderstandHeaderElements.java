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
package org.apache.axiom.ts.saaj.header;

import java.io.InputStream;
import java.util.Iterator;

import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import org.apache.axiom.ts.saaj.SAAJImplementation;
import org.apache.axiom.ts.saaj.SAAJTestCase;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPSampleSet;

public class TestExamineMustUnderstandHeaderElements extends SAAJTestCase {
    public TestExamineMustUnderstandHeaderElements(
            SAAJImplementation saajImplementation, SOAPSpec spec) {
        super(saajImplementation, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        MimeHeaders mimeHeaders = new MimeHeaders();
        mimeHeaders.addHeader("Content-Type", spec.getContentType());
        InputStream in = SOAPSampleSet.MUST_UNDERSTAND.getMessage(spec).getInputStream();
        try {
            SOAPMessage message = newMessageFactory().createMessage(mimeHeaders, in);
            SOAPHeader header = message.getSOAPHeader();
            Iterator<?> it = header.examineMustUnderstandHeaderElements(null);
            assertTrue(it.hasNext());
            assertTrue(it.next() instanceof SOAPHeaderElement);
            assertFalse(it.hasNext());
        } finally {
            in.close();
        }
    }
}
