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

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.testutils.conformance.ConformanceTestFile;
import org.apache.axiom.ts.dom.DOMTestCase;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;

public class TestCloneNode extends DOMTestCase {
    private final ConformanceTestFile file;

    public TestCloneNode(DocumentBuilderFactory dbf, ConformanceTestFile file) {
        super(dbf);
        this.file = file;
        addTestProperty("file", file.getShortName());
    }

    protected void runTest() throws Throwable {
        Document document = dbf.newDocumentBuilder().parse(file.getAsStream());
        Document document2 = (Document)document.cloneNode(true);
        XMLAssert.assertXMLIdentical(XMLUnit.compareXML(document, document2), true);
    }
}
