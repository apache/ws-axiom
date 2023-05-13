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

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.apache.axiom.ts.jaxp.xslt.XSLTImplementation;
import org.w3c.dom.Document;

public class TestTransformerWithStylesheet extends TransformerTestCase {
    public TestTransformerWithStylesheet(
            DocumentBuilderFactory dbf, XSLTImplementation xsltImplementation) {
        super(dbf, xsltImplementation);
    }

    @Override
    protected void runTest() throws Throwable {
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document input =
                builder.parse(TestTransformerWithStylesheet.class.getResourceAsStream("input.xml"));
        Document stylesheet =
                builder.parse(
                        TestTransformerWithStylesheet.class.getResourceAsStream("stylesheet.xslt"));
        Document expected =
                builder.parse(
                        TestTransformerWithStylesheet.class.getResourceAsStream("output.xml"));
        Document actual = builder.newDocument();
        Transformer transformer =
                xsltImplementation
                        .newTransformerFactory()
                        .newTransformer(new DOMSource(stylesheet));
        transformer.transform(new DOMSource(input), new DOMResult(actual));
        assertAbout(xml())
                .that(xml(Document.class, actual))
                .ignoringWhitespace()
                .hasSameContentAs(xml(Document.class, expected));
    }
}
