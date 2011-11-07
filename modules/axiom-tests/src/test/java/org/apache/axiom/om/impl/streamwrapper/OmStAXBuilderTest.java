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

package org.apache.axiom.om.impl.streamwrapper;

import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.TestConstants;
import org.apache.axiom.soap.SOAPEnvelope;

import java.io.File;
import java.io.FileOutputStream;

public class OmStAXBuilderTest extends AbstractTestCase {
    private OMXMLParserWrapper builder;
    private File tempFile;

    public OmStAXBuilderTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        builder = OMXMLBuilderFactory.createSOAPModelBuilder(getTestResource(
                TestConstants.SOAP_SOAPMESSAGE), null);
        tempFile = File.createTempFile("temp", "xml");
    }

    public void testStaxBuilder() throws Exception {
        SOAPEnvelope envelope = (SOAPEnvelope) builder.getDocumentElement();
        assertNotNull(envelope);
        envelope.serialize(new FileOutputStream(tempFile));


    }

    protected void tearDown() throws Exception {
        tempFile.delete();
    }


}
