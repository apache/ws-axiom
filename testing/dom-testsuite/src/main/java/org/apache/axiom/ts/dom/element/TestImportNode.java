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

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.ts.dom.DOMTestCase;
import org.apache.axiom.ts.jaxp.dom.DOMImplementation;
import org.apache.axiom.ts.xml.XMLSample;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class TestImportNode extends DOMTestCase {
    private final XMLSample file;
    private final DOMImplementation from;

    public TestImportNode(DocumentBuilderFactory dbf, XMLSample file, DOMImplementation from) {
        super(dbf);
        this.file = file;
        addTestParameter("file", file.getName());
        this.from = from;
        addTestParameter("from", from.getName());
    }

    @Override
    protected void runTest() throws Throwable {
        Element orgElement =
                from.parse(new InputSource(file.getUrl().toString())).getDocumentElement();
        Document doc = dbf.newDocumentBuilder().newDocument();
        assertAbout(xml())
                .that(xml(Element.class, (Element) doc.importNode(orgElement, true)))
                // Import discards DTD information
                .treatingElementContentWhitespaceAsText()
                .hasSameContentAs(orgElement);
    }
}
