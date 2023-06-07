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
package org.apache.axiom.ts.om.factory;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.ext.stax.BlobProvider;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMText;
import org.apache.axiom.testutils.blob.TextBlob;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.util.UIDGenerator;

public class TestCreateOMTextFromBlobProvider extends AxiomTestCase {
    static class TestBlobProvider implements BlobProvider {
        private Blob blob;

        @Override
        public Blob getBlob() {
            if (blob == null) {
                blob = new TextBlob("Data", StandardCharsets.UTF_8);
            }
            return blob;
        }

        public boolean isBlobCreated() {
            return blob != null;
        }
    }

    private final boolean nullContentID;

    public TestCreateOMTextFromBlobProvider(OMMetaFactory metaFactory, boolean nullContentID) {
        super(metaFactory);
        this.nullContentID = nullContentID;
        addTestParameter("nullContentId", nullContentID);
    }

    @Override
    protected void runTest() throws Throwable {
        TestBlobProvider prov = new TestBlobProvider();
        OMFactory factory = metaFactory.getOMFactory();
        String contentID = nullContentID ? null : UIDGenerator.generateContentId();
        OMText text = factory.createOMText(contentID, prov, true);
        assertFalse(prov.isBlobCreated());
        assertThat(text.getBlob().getInputStream())
                .asString(StandardCharsets.UTF_8)
                .isEqualTo("Data");
        assertTrue(prov.isBlobCreated());
        if (contentID == null) {
            assertThat(text.getContentID()).isNotNull();
        } else {
            assertThat(text.getContentID()).isEqualTo(contentID);
        }
    }
}
