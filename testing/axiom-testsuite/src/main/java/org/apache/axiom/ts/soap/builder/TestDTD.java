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
package org.apache.axiom.ts.soap.builder;

import java.io.StringReader;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

/**
 * Tests that the SOAP model builder rejects attempts to create a DTD. Note that this test is
 * implemented using {@link OMXMLBuilderFactory#createStAXSOAPModelBuilder(XMLStreamReader)} because
 * the methods taking a stream as input will generally reject DTDs at a much lower level.
 */
public class TestDTD extends SOAPTestCase {
    public TestDTD(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        String message = "<!DOCTYPE test []>" + soapFactory.getDefaultEnvelope();
        XMLStreamReader parser = StAXUtils.createXMLStreamReader(new StringReader(message));
        ;
        try {
            SOAPModelBuilder builder =
                    OMXMLBuilderFactory.createStAXSOAPModelBuilder(metaFactory, parser);
            // The processing must fail before we can get the SOAPEnvelope
            builder.getSOAPEnvelope();
            fail("Expected SOAPProcessingException");
        } catch (SOAPProcessingException ex) {
            // Expected
        }
    }
}
