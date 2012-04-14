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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.DocumentElementExtractor;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.xalan.processor.TransformerFactoryImpl;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class OMElementFactory implements OMContainerFactory {
    private final boolean detached;
    
    public OMElementFactory(boolean detached) {
        this.detached = detached;
    }

    public void addTestProperties(AxiomTestCase testCase) {
        testCase.addTestProperty("container", "element");
        testCase.addTestProperty("detached", Boolean.toString(detached));
    }

    public InputSource getControl(InputStream testFileContent) throws Exception {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder().parse(testFileContent);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // Use Xalan's factory directly to avoid issues if Saxon is selected as default
            new TransformerFactoryImpl().newTransformer().transform(
                    new DOMSource(doc.getDocumentElement()), new StreamResult(baos));
            return new InputSource(new ByteArrayInputStream(baos.toByteArray()));
        } finally {
            testFileContent.close();
        }
    }

    public OMContainer getContainer(OMXMLParserWrapper builder) {
        return builder.getDocumentElement(detached);
    }

    public XMLStreamReader filter(XMLStreamReader reader) {
        return new DocumentElementExtractor(reader);
    }
};
