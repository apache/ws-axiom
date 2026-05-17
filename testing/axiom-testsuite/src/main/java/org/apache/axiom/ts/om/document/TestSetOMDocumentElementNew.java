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
import java.util.Iterator;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.testutils.suite.MatrixTestCase;

/**
 * Tests the behavior of {@link OMDocument#setOMDocumentElement(OMElement)} if there is no existing
 * document element.
 */
public class TestSetOMDocumentElementNew implements MatrixTestCase {
    @Inject
    private OMFactory factory;

    @Override
    public void runTest() throws Throwable {
        OMDocument document = factory.createOMDocument();
        OMComment comment = factory.createOMComment(document, "some comment");
        OMElement documentElement = factory.createOMElement("root", null);
        document.setOMDocumentElement(documentElement);
        assertThat(document.getOMDocumentElement()).isSameAs(documentElement);
        assertThat(documentElement.getParent()).isSameAs(document);
        Iterator<OMNode> it = document.getChildren();
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isSameAs(comment);
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isSameAs(documentElement);
        assertThat(it.hasNext()).isFalse();
    }
}
