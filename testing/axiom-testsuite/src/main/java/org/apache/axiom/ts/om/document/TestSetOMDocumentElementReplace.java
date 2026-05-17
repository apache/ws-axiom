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
package org.apache.axiom.ts.om.document;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import java.io.StringReader;
import java.util.Iterator;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.testutils.suite.MatrixTestCase;

/**
 * Tests the behavior of {@link OMDocument#setOMDocumentElement(OMElement)} if the document already
 * has a document element.
 */
public class TestSetOMDocumentElementReplace implements MatrixTestCase {
    @Inject
    private OMFactory factory;

    @Override
    public void runTest() throws Throwable {
        OMDocument document = OMXMLBuilderFactory.createOMBuilder(
                        factory, new StringReader("<!--comment1--><root/><!--comment2-->"))
                .getDocument();
        OMElement documentElement = factory.createOMElement("new", null);
        document.setOMDocumentElement(documentElement);
        assertThat(document.getOMDocumentElement()).isSameAs(documentElement);
        Iterator<OMNode> it = document.getChildren();
        assertThat(it.hasNext()).isTrue();
        OMNode child = it.next();
        assertThat(child).isInstanceOf(OMComment.class);
        assertThat(((OMComment) child).getValue()).isEqualTo("comment1");
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isSameAs(documentElement);
        assertThat(it.hasNext()).isTrue();
        child = it.next();
        assertThat(child).isInstanceOf(OMComment.class);
        assertThat(((OMComment) child).getValue()).isEqualTo("comment2");
        assertThat(it.hasNext()).isFalse();
    }
}
