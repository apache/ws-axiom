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
package org.apache.axiom.ts.om.builder;

import static org.apache.axiom.truth.AxiomTruth.assertThat;

import javax.xml.transform.dom.DOMSource;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.jaxp.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TestDetachWithDOM extends AxiomTestCase {
    private final boolean useDOMSource;
    
    public TestDetachWithDOM(OMMetaFactory metaFactory, boolean useDOMSource) {
        super(metaFactory);
        this.useDOMSource = useDOMSource;
        addTestParameter("useDOMSource", useDOMSource);
    }

    @Override
    protected void runTest() throws Throwable {
        Document document = DOMImplementation.XERCES.newDocument();
        Element root = document.createElementNS("", "root");
        root.appendChild(document.createElementNS("", "a"));
        OMXMLParserWrapper builder;
        if (useDOMSource) {
            builder = OMXMLBuilderFactory.createOMBuilder(metaFactory.getOMFactory(), new DOMSource(root));
        } else {
            builder = OMXMLBuilderFactory.createOMBuilder(metaFactory.getOMFactory(), root, false);
        }
        OMElement omRoot = builder.getDocumentElement();
        builder.detach();
        root.appendChild(document.createElementNS("", "b"));
        assertThat(omRoot).hasNumberOfChildren(1);
    }
}
