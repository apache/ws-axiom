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

import javax.xml.transform.sax.SAXSource;

import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.testutils.conformance.ConformanceTestFile;
import org.apache.axiom.ts.ConformanceTestCase;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.xml.sax.XMLReader;

public class TestGetSAXSource extends ConformanceTestCase {
    private final OMContainerFactory containerFactory;
    private final boolean cache;
    
    public TestGetSAXSource(OMMetaFactory metaFactory, ConformanceTestFile file, OMContainerFactory containerFactory, boolean cache) {
        super(metaFactory, file);
        this.containerFactory = containerFactory;
        this.cache = cache;
        containerFactory.addTestProperties(this);
        addTestProperty("cache", Boolean.toString(cache));
    }

    protected void runTest() throws Throwable {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = getFileAsStream();
        try {
            OMXMLParserWrapper builder = OMXMLBuilderFactory.createOMBuilder(metaFactory.getOMFactory(),
                    TEST_PARSER_CONFIGURATION, in);
            SAXSource source = containerFactory.getContainer(builder).getSAXSource(cache);
            XMLReader xmlReader = source.getXMLReader();
            SAXSerializer serializer = new SAXSerializer();
            serializer.setOutputStream(out);
            xmlReader.setContentHandler(serializer);
            xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", serializer);
            xmlReader.parse(source.getInputSource());
        } finally {
            in.close();
        }
        XMLAssert.assertXMLIdentical(XMLUnit.compareXML(
                AbstractTestCase.toDocumentWithoutDTD(getFileAsStream(), false),
                AbstractTestCase.toDocumentWithoutDTD(new ByteArrayInputStream(out.toByteArray()), false)), true);
    }
}
