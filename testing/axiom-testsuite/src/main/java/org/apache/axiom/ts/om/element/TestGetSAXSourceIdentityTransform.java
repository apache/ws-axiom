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
package org.apache.axiom.ts.om.element;

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import java.io.InputStream;

import javax.xml.transform.Transformer;

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.jaxp.xslt.XSLTImplementation;

public class TestGetSAXSourceIdentityTransform extends AxiomTestCase {
    private final XSLTImplementation xsltImplementation;
    private final boolean cache;

    public TestGetSAXSourceIdentityTransform(
            OMMetaFactory metaFactory, XSLTImplementation xsltImplementation, boolean cache) {
        super(metaFactory);
        this.xsltImplementation = xsltImplementation;
        this.cache = cache;
        addTestParameter("xslt", xsltImplementation.getName());
        addTestParameter("cache", cache);
    }

    private InputStream getInput() {
        return TestGetSAXSourceIdentityTransform.class.getResourceAsStream("test.xml");
    }

    @Override
    protected void runTest() throws Throwable {
        Transformer transformer = xsltImplementation.newTransformerFactory().newTransformer();

        OMFactory factory = metaFactory.getOMFactory();
        OMElement element =
                OMXMLBuilderFactory.createOMBuilder(factory, getInput()).getDocumentElement();
        OMDocument outputDocument = factory.createOMDocument();
        transformer.transform(element.getSAXSource(cache), outputDocument.getSAXResult());

        assertAbout(xml())
                .that(xml(OMDocument.class, outputDocument))
                .ignoringWhitespaceInPrologAndEpilog()
                .hasSameContentAs(getInput());

        element.close(false);
    }
}
