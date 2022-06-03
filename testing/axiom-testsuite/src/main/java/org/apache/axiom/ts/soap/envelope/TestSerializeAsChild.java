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
package org.apache.axiom.ts.soap.envelope;

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import java.io.OutputStream;

import org.apache.axiom.blob.Blobs;
import org.apache.axiom.blob.MemoryBlob;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

/** Regression test for <a href="https://issues.apache.org/jira/browse/AXIOM-474">AXIOM-474</a>. */
public class TestSerializeAsChild extends SOAPTestCase {
    public TestSerializeAsChild(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPEnvelope envelope = soapFactory.createDefaultSOAPMessage().getSOAPEnvelope();
        soapFactory.createOMElement(
                "echo", soapFactory.createOMNamespace("urn:test", "p"), envelope.getBody());
        OMElement log = soapFactory.createOMElement("log", null);
        OMElement entry = soapFactory.createOMElement("entry", null, log);
        entry.addChild(envelope);
        MemoryBlob blob = Blobs.createMemoryBlob();
        OutputStream out = blob.getOutputStream();
        envelope.serialize(out);
        out.close();
        assertAbout(xml())
                .that(blob.getInputStream())
                .hasSameContentAs(xml(OMElement.class, envelope));
    }
}
