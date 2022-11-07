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
package org.apache.axiom.ts.om.text;

import java.io.StringReader;

import javax.xml.transform.stream.StreamSource;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.testutils.blob.RandomBlob;
import org.apache.axiom.ts.AxiomTestCase;

public class TestCloneBinary extends AxiomTestCase {
    private boolean fetch;

    public TestCloneBinary(OMMetaFactory metaFactory, boolean fetch) {
        super(metaFactory);
        this.fetch = fetch;
        addTestParameter("fetch", fetch);
    }

    @Override
    protected void runTest() throws Throwable {
        Blob blob = new RandomBlob(600613L, 4096);
        StringReader rootPart =
                new StringReader(
                        "<root><xop:Include xmlns:xop='http://www.w3.org/2004/08/xop/include' href='cid:123456@example.org'/></root>");
        DummyAttachmentAccessor attachmentAccessor =
                new DummyAttachmentAccessor("123456@example.org", blob);
        OMElement root =
                OMXMLBuilderFactory.createOMBuilder(
                                metaFactory.getOMFactory(),
                                new StreamSource(rootPart),
                                attachmentAccessor)
                        .getDocumentElement();
        OMText text = (OMText) root.getFirstOMChild();
        OMCloneOptions options = new OMCloneOptions();
        options.setFetchDataHandlers(fetch);
        OMText clone = (OMText) text.clone(options);
        assertTrue(clone.isBinary());
        assertEquals(fetch, attachmentAccessor.isLoaded());
        assertSame(blob, clone.getBlob());
    }
}
