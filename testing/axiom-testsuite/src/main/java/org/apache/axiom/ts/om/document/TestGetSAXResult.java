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

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.ts.ConformanceTestCase;
import org.apache.axiom.ts.jaxp.xslt.XSLTImplementation;
import org.apache.axiom.ts.xml.XMLSample;

public class TestGetSAXResult extends ConformanceTestCase {
    private final XSLTImplementation xsltImplementation;

    public TestGetSAXResult(
            OMMetaFactory metaFactory, XSLTImplementation xsltImplementation, XMLSample file) {
        super(metaFactory, file);
        this.xsltImplementation = xsltImplementation;
        addTestParameter("xslt", xsltImplementation.getName());
    }

    @Override
    protected void runTest() throws Throwable {
        TransformerFactory transformerFactory = xsltImplementation.newTransformerFactory();
        StreamSource source = new StreamSource(file.getUrl().toString());
        OMDocument document = metaFactory.getOMFactory().createOMDocument();
        SAXResult result = document.getSAXResult();
        transformerFactory.newTransformer().transform(source, result);
        assertAbout(xml())
                .that(xml(OMDocument.class, document))
                .ignoringWhitespaceInPrologAndEpilog()
                .ignoringRedundantNamespaceDeclarations()
                .expandingEntityReferences()
                .hasSameContentAs(file.getUrl());
    }
}
