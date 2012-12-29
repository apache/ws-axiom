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
package org.apache.axiom.ts.om.misc;

import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.ds.custombuilder.ByteArrayCustomBuilder;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.axiom.om.impl.serialize.StreamingOMSerializer;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.ts.AxiomTestCase;

/**
 * Scenario:
 *    A) Builder reads a soap message.
 *    B) The payload of the message is created by a customer builder
 *    C) The resulting OM is serialized (pulled) prior to completion of the intial read.
 *    D) The payload of the message should not be expanded into OM.
 *    
 *    Expansion of the message results in both a time and space penalty.
 */
public class TestElementPullStreamAndOMExpansion extends AxiomTestCase {
    public TestElementPullStreamAndOMExpansion(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    protected void runTest() throws Throwable {
        // Create a builder from a message containing an interesting payload
        StAXBuilder builder = (StAXBuilder)OMXMLBuilderFactory.createSOAPModelBuilder(metaFactory,
                AbstractTestCase.getTestResource("soap/OMElementTest.xml"), null);
        
        // Create a custom builder to store the sub trees as a byte array instead of a full tree
        ByteArrayCustomBuilder customBuilder = new ByteArrayCustomBuilder("utf-8");
        
        // Register the custom builder on the builder so that they body payload is stored as bytes
        builder.registerCustomBuilderForPayload(customBuilder);
        
        
        // Create an output stream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        XMLStreamWriter writer = StAXUtils.createXMLStreamWriter(byteArrayOutputStream);

        // Now use StreamingOMSerializer to write the input stream to the output stream
        SOAPEnvelope env = (SOAPEnvelope) builder.getDocumentElement();
        SOAPBody body = env.getBody();
        OMSourcedElement omse = (OMSourcedElement) body.getFirstElement();
        
        StreamingOMSerializer serializer = new StreamingOMSerializer();
        serializer.serialize(env.getXMLStreamReaderWithoutCaching(),
                             writer);
        writer.flush();

        String outputString = new String(byteArrayOutputStream.toByteArray());
        assertTrue("Expected output was incorrect.  Received:" + outputString,
                outputString != null && !"".equals(outputString) && outputString.length() > 1);
        assertTrue("Expected output was incorrect.  Received:" + outputString,
                outputString.contains("axis2:input"));
        assertTrue("Expected output was incorrect.  Received:" + outputString,
                outputString.contains("This is some text"));
        assertTrue("Expected output was incorrect.  Received:" + outputString,
                outputString.contains("Some Other Text"));
        
        assertTrue("Expectation is that an OMSourcedElement was created for the payload", 
                omse != null);
        assertTrue("Expectation is that the OMSourcedElement was not expanded by serialization ", 
                !omse.isExpanded());
    }
}
