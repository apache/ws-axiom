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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringReader;

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMXMLBuilderFactory;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.apache.axiom.ts.omdom.OMDOMTestCase;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;

public class TestInsertBeforeForbidden extends OMDOMTestCase {
    @Inject
    @Named("build")
    private boolean build;

    @Override
    protected void runTest() throws Throwable {
        OMDocument omDocument =
                OMXMLBuilderFactory.createOMBuilder(
                                metaFactory.getOMFactory(), new StringReader("<!--test--><test/>"))
                        .getDocument();
        if (build) {
            omDocument.build();
        }
        Document document = (Document) omDocument;
        Comment comment = (Comment) document.getFirstChild();
        try {
            document.insertBefore(document.createElementNS(null, "test"), comment);
            fail("Expected DOMException");
        } catch (DOMException ex) {
            assertThat(ex.code).isEqualTo(DOMException.HIERARCHY_REQUEST_ERR);
        }
    }
}
