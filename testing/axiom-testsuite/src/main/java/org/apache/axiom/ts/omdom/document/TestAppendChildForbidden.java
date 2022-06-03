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
package org.apache.axiom.ts.omdom.document;

import static com.google.common.truth.Truth.assertThat;

import java.io.StringReader;

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;

public class TestAppendChildForbidden extends AxiomTestCase {
    private final boolean build;

    public TestAppendChildForbidden(OMMetaFactory metaFactory, boolean build) {
        super(metaFactory);
        this.build = build;
        addTestParameter("build", build);
    }

    @Override
    protected void runTest() throws Throwable {
        OMDocument omDocument =
                OMXMLBuilderFactory.createOMBuilder(
                                metaFactory.getOMFactory(), new StringReader("<test/>"))
                        .getDocument();
        if (build) {
            omDocument.build();
        }
        Document document = (Document) omDocument;
        try {
            document.appendChild(document.createElementNS(null, "test"));
            fail("Expected DOMException");
        } catch (DOMException ex) {
            assertThat(ex.code).isEqualTo(DOMException.HIERARCHY_REQUEST_ERR);
        }
    }
}
