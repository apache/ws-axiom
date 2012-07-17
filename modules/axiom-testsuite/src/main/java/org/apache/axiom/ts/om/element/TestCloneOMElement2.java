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

import java.io.InputStream;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.testutils.conformance.ConformanceTestFile;
import org.apache.axiom.ts.ConformanceTestCase;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;

public class TestCloneOMElement2 extends ConformanceTestCase {
    public TestCloneOMElement2(OMMetaFactory metaFactory, ConformanceTestFile file) {
        super(metaFactory, file);
    }

    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        InputStream in = getFileAsStream();
        try {
            OMElement original = OMXMLBuilderFactory.createOMBuilder(factory, in).getDocumentElement();
            OMElement clone = original.cloneOMElement();
            XMLAssert.assertXMLIdentical(XMLUnit.compareXML(original.toString(), clone.toString()), true);
        } finally {
            in.close();
        }
    }
}
