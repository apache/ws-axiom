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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.ts.ConformanceTestCase;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class TestSerializeToOutputStream extends ConformanceTestCase {
    public TestSerializeToOutputStream(OMMetaFactory metaFactory, String file) {
        super(metaFactory, file);
    }

    protected void runTest() throws Throwable {
        InputStream in = getFileAsStream();
        byte[] control;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder().parse(in);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            TransformerFactory.newInstance().newTransformer().transform(
                    new DOMSource(doc.getDocumentElement()), new StreamResult(baos));
            control = baos.toByteArray();
        } finally {
            in.close();
        }
        in = getFileAsStream();
        try {
            OMXMLParserWrapper builder = metaFactory.createOMBuilder(metaFactory.getOMFactory(),
                    StAXParserConfiguration.PRESERVE_CDATA_SECTIONS, in);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                builder.getDocumentElement().serialize(baos);
                assertXMLIdentical(compareXML(new InputSource(new ByteArrayInputStream(control)),
                        new InputSource(new ByteArrayInputStream(baos.toByteArray()))), true);
            } finally {
                builder.close();
            }
        } finally {
            in.close();
        }
    }
}
