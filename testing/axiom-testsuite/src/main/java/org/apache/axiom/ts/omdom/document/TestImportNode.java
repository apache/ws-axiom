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

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.ts.omdom.OMDOMTestCase;
import org.apache.axiom.ts.jaxp.dom.DOMImplementation;
import org.apache.axiom.ts.xml.XMLSample;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class TestImportNode extends OMDOMTestCase {
    @Inject
    @Named("file")
    private XMLSample file;

    @Override
    protected void runTest() throws Throwable {
        Document doc = DOMImplementation.XERCES.parse(new InputSource(file.getUrl().toString()));
        Document doc2 = metaFactory.newDocumentBuilderFactory().newDocumentBuilder().newDocument();
        Node n = doc2.importNode(doc.getDocumentElement(), true);
        assertAbout(xml())
                .that(xml(OMElement.class, (OMElement) n))
                // Import discards DTD information
                .treatingElementContentWhitespaceAsText()
                .hasSameContentAs(xml(Element.class, doc.getDocumentElement()));
    }
}
