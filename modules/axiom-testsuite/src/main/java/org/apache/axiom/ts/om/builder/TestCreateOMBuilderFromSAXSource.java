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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.testutils.conformance.ConformanceTestFile;
import org.apache.axiom.ts.ConformanceTestCase;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.xml.sax.InputSource;

public class TestCreateOMBuilderFromSAXSource extends ConformanceTestCase {
    public TestCreateOMBuilderFromSAXSource(OMMetaFactory metaFactory, ConformanceTestFile file) {
        super(metaFactory, file);
    }

    protected void runTest() throws Throwable {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        SAXParser parser = factory.newSAXParser();
        InputStream in = getFileAsStream();
        try {
            SAXSource source = new SAXSource(parser.getXMLReader(), new InputSource(in));
            OMXMLParserWrapper builder = OMXMLBuilderFactory.createOMBuilder(metaFactory.getOMFactory(), source);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            builder.getDocument().serialize(baos);
            XMLAssert.assertXMLIdentical(XMLUnit.compareXML(
                    AbstractTestCase.toDocumentWithoutDTD(getFileAsStream()),
                    AbstractTestCase.toDocumentWithoutDTD(new ByteArrayInputStream(baos.toByteArray()))), true);
        } finally {
            in.close();
        }
    }
}
