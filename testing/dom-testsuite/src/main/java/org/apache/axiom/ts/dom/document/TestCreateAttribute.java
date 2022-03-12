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
package org.apache.axiom.ts.dom.document;

import static com.google.common.truth.Truth.assertThat;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.ts.dom.DOMTestCase;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;

public class TestCreateAttribute extends DOMTestCase {
    public TestCreateAttribute(DocumentBuilderFactory dbf) {
        super(dbf);
    }

    @Override
    protected void runTest() throws Throwable {
        String attrName = "attrIdentifier";
        Document doc = dbf.newDocumentBuilder().newDocument();
        Attr attr = doc.createAttribute(attrName);
        assertThat(attr.getNodeName()).isEqualTo(attrName);
        assertThat(attr.getName()).isEqualTo(attrName);
        assertThat(attr.getPrefix()).isNull();
        assertThat(attr.getLocalName()).isNull();
        assertThat(attr.getNamespaceURI()).isNull();
    }
}
