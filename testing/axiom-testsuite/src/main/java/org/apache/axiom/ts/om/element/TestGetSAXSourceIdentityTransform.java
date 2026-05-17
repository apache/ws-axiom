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

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.io.InputStream;
import javax.xml.transform.Transformer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.testutils.suite.MatrixTestCase;
import org.apache.axiom.ts.jaxp.xslt.XSLTImplementation;

public class TestGetSAXSourceIdentityTransform implements MatrixTestCase {
    @Inject
    private OMFactory factory;

    @Inject
    private XSLTImplementation xsltImplementation;

    @Inject
    @Named("cache")
    private boolean cache;

    private InputStream getInput() {
        return TestGetSAXSourceIdentityTransform.class.getResourceAsStream("test.xml");
    }

    @Override
    public void runTest() throws Throwable {
        Transformer transformer = xsltImplementation.newTransformerFactory().newTransformer();

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
