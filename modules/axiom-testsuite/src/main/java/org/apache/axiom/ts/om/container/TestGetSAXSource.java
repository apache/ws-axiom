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
import java.net.URL;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.sax.SAXSource;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.testutils.XMLAssertEx;
import org.apache.axiom.testutils.conformance.ConformanceTestFile;
import org.apache.axiom.ts.ConformanceTestCase;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;

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
        OMXMLParserWrapper builder = metaFactory.createOMBuilder(metaFactory.getOMFactory(),
                TEST_PARSER_CONFIGURATION, new InputSource(file.getUrl().toString()));
        SAXSource source = containerFactory.getContainer(builder).getSAXSource(cache);
        XMLReader xmlReader = source.getXMLReader();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SAXSerializer serializer = new SAXSerializer();
        // A SAXSource has no way to tell its consumer about the encoding of the document.
        // Just set it to UTF-8 to have a well defined encoding.
        serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        serializer.setOutputStream(out);
        xmlReader.setContentHandler(serializer);
        xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", serializer);
        xmlReader.parse(source.getInputSource());
        InputSource control = containerFactory.getControl(file.getAsStream());
        InputSource test = new InputSource(new ByteArrayInputStream(out.toByteArray()));
        // Configure the InputSources such that external entities can be resolved
        String systemId = new URL(file.getUrl(), "dummy.xml").toString();
        control.setSystemId(systemId);
        test.setSystemId(systemId);
        XMLAssertEx.assertXMLIdentical(control, test, false);
    }
}
