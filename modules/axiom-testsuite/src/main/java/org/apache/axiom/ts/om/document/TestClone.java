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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.testutils.conformance.ConformanceTestFile;
import org.apache.axiom.ts.ConformanceTestCase;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.xml.sax.InputSource;

public class TestClone extends ConformanceTestCase {
    public TestClone(OMMetaFactory metaFactory, ConformanceTestFile file) {
        super(metaFactory, file);
    }

    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        InputStream in = getFileAsStream();
        try {
            OMDocument original = OMXMLBuilderFactory.createOMBuilder(factory,
                    StAXParserConfiguration.PRESERVE_CDATA_SECTIONS, in).getDocument();
            OMDocument clone = (OMDocument)original.clone(new OMCloneOptions());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            clone.serialize(baos);
            XMLAssert.assertXMLIdentical(XMLUnit.compareXML(
                    new InputSource(getFileAsStream()),
                    new InputSource(new ByteArrayInputStream(baos.toByteArray()))), true);
        } finally {
            in.close();
        }
    }
}
