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
package org.apache.axiom.ts.om.container;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.testutils.suite.MatrixTestCase;
import org.apache.axiom.ts.jaxp.dom.DOMImplementation;
import org.apache.axiom.ts.jaxp.xslt.XSLTImplementation;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

final class OMElementExtractor extends OMContainerExtractor {
    private final boolean detached;
    
    OMElementExtractor(boolean detached) {
        this.detached = detached;
    }

    @Override
    public void addTestParameters(MatrixTestCase testCase) {
        testCase.addTestParameter("container", "element");
        testCase.addTestParameter("detached", detached);
    }

    @Override
    public InputSource getControl(InputStream testFileContent) throws Exception {
        try {
            Document doc = DOMImplementation.XERCES.parse(testFileContent);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // Use Xalan's factory directly to avoid issues if Saxon is selected as default
            XSLTImplementation.XALAN.newTransformerFactory().newTransformer().transform(
                    new DOMSource(doc.getDocumentElement()), new StreamResult(baos));
            return new InputSource(new ByteArrayInputStream(baos.toByteArray()));
        } finally {
            testFileContent.close();
        }
    }

    @Override
    public OMContainer getContainer(OMXMLParserWrapper builder) {
        return builder.getDocumentElement(detached);
    }

    @Override
    public XMLStreamReader filter(XMLStreamReader reader) {
        return new DocumentElementExtractor(reader);
    }
};
