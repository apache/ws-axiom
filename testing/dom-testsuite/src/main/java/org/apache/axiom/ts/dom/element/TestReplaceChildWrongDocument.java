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
package org.apache.axiom.ts.dom.element;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.ts.dom.DOMTestCase;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TestReplaceChildWrongDocument extends DOMTestCase {
    public TestReplaceChildWrongDocument(DocumentBuilderFactory dbf) {
        super(dbf);
    }

    @Override
    protected void runTest() throws Throwable {
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document1 = db.newDocument();
        Document document2 = db.newDocument();
        Element parent = document1.createElementNS(null, "parent");
        Element child1 = document1.createElementNS(null, "child1");
        Element child2 = document1.createElementNS(null, "child2");
        Element child3 = document1.createElementNS(null, "child3");
        parent.appendChild(child1);
        parent.appendChild(child2);
        parent.appendChild(child3);
        Element replacementChild = document2.createElementNS(null, "newchild");
        assertThatThrownBy(() -> parent.replaceChild(replacementChild, child2))
                .isInstanceOfSatisfying(
                        DOMException.class,
                        ex -> assertThat(ex.code).isEqualTo(DOMException.WRONG_DOCUMENT_ERR));
    }
}
