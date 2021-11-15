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

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.transform.dom.DOMSource;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.ts.ConformanceTestCase;
import org.apache.axiom.ts.jaxp.dom.DOMImplementation;
import org.apache.axiom.ts.xml.XMLSample;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class TestCreateOMBuilderFromDOM extends ConformanceTestCase {
    private final DOMImplementation implementation;
    private final Boolean expandEntityReferences;
    
    public TestCreateOMBuilderFromDOM(OMMetaFactory metaFactory, XMLSample file,
            DOMImplementation implementation, Boolean expandEntityReferences) {
        super(metaFactory, file);
        this.implementation = implementation;
        addTestParameter("implementation", implementation.getName());
        this.expandEntityReferences = expandEntityReferences;
        if (expandEntityReferences != null) {
            addTestParameter("expandEntityReferences", expandEntityReferences.booleanValue());
        }
    }

    private Document loadDocument(boolean expandEntityReferences) throws Exception {
        return implementation.parse(new InputSource(file.getUrl().toString()), expandEntityReferences);
    }
    
    @Override
    protected void runTest() throws Throwable {
        // We never expand entity references during parsing, but we may do this later when
        // converting DOM to OM.
        Document document = loadDocument(false);
        OMXMLParserWrapper builder;
        if (expandEntityReferences == null) {
            builder = OMXMLBuilderFactory.createOMBuilder(metaFactory.getOMFactory(), new DOMSource(document));
        } else {
            builder = OMXMLBuilderFactory.createOMBuilder(metaFactory.getOMFactory(), document,
                    expandEntityReferences.booleanValue());
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        builder.getDocument().serialize(baos);
        InputSource actual = new InputSource();
        actual.setByteStream(new ByteArrayInputStream(baos.toByteArray()));
        actual.setSystemId(file.getUrl().toString());
        assertAbout(xml())
                .that(actual)
                .ignoringWhitespaceInPrologAndEpilog()
                .treatingElementContentWhitespaceAsText(!implementation.isDOM3())
                .hasSameContentAs(loadDocument(expandEntityReferences == null || expandEntityReferences));
    }
}
