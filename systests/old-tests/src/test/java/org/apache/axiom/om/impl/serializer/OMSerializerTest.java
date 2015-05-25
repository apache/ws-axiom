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

package org.apache.axiom.om.impl.serializer;

import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.om.NodeUnavailableException;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.impl.serialize.StreamingOMSerializer;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPSampleSet;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import java.io.ByteArrayOutputStream;

public class OMSerializerTest extends AbstractTestCase {
    public void testElementPullStream1() throws Exception {
        OMXMLParserWrapper builder = OMXMLBuilderFactory.createSOAPModelBuilder(
                SOAPSampleSet.WSA.getMessage(SOAPSpec.SOAP11).getInputStream(), null);
        SOAPEnvelope env = (SOAPEnvelope) builder.getDocumentElement();
        StreamingOMSerializer serializer = new StreamingOMSerializer();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        XMLStreamWriter writer = StAXUtils.createXMLStreamWriter(byteArrayOutputStream);

        serializer.serialize(env.getXMLStreamReaderWithoutCaching(), writer);
        writer.flush();

        String outputString = new String(byteArrayOutputStream.toByteArray());
        assertTrue(outputString != null && !"".equals(outputString) && outputString.length() > 1);
    }

    public void testElementPullStream1WithCacheOff() throws Exception {

        OMXMLParserWrapper builder = OMXMLBuilderFactory.createSOAPModelBuilder(
                SOAPSampleSet.WSA.getMessage(SOAPSpec.SOAP11).getInputStream(), null);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        XMLStreamWriter writer = StAXUtils.createXMLStreamWriter(byteArrayOutputStream,
                OMConstants.DEFAULT_CHAR_SET_ENCODING);

        SOAPEnvelope env = (SOAPEnvelope) builder.getDocumentElement();
        env.serializeAndConsume(writer);
        writer.flush();

        String outputString = new String(byteArrayOutputStream.toByteArray());
        assertTrue(outputString != null && !"".equals(outputString) && outputString.length() > 1);

        writer = StAXUtils.createXMLStreamWriter(byteArrayOutputStream,
                OMConstants.DEFAULT_CHAR_SET_ENCODING);

        //now we should not be able to serilaize anything ! this should throw
        //an error
        try {
            env.serializeAndConsume(writer);
            fail("Expected NodeUnavailableException");
        } catch (NodeUnavailableException ex) {
            // Expected
        }
    }

    public void testElementPullStream2() throws Exception {
        OMXMLParserWrapper builder = OMXMLBuilderFactory.createSOAPModelBuilder(
                SOAPSampleSet.WSA.getMessage(SOAPSpec.SOAP11).getInputStream(), null);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        XMLStreamWriter writer = StAXUtils.createXMLStreamWriter(byteArrayOutputStream);

        SOAPEnvelope env = (SOAPEnvelope) builder.getDocumentElement();
        SOAPBody body = env.getBody();
        StreamingOMSerializer serializer = new StreamingOMSerializer();
        serializer.serialize(body.getXMLStreamReaderWithoutCaching(),
                             writer);
        writer.flush();

        String outputString = new String(byteArrayOutputStream.toByteArray());
        assertTrue(outputString != null && !"".equals(outputString) && outputString.length() > 1);
    }
    
    public void testXSITypePullStream() throws Exception {
        
        // Read the SOAP Message that defines prefix "usr" on the envelope and only uses it within an xsi:type
        // within a payload element.
        final String USR_URI = "http://ws.apache.org/axis2/user";
        final String USR_DEF = "xmlns:usr";
        
        XMLStreamReader reader =
            XMLInputFactory.newInstance()
                           .createXMLStreamReader(getTestResource("soap/soapmessageWithXSI.xml"));
        OMXMLParserWrapper builder =
            OMXMLBuilderFactory.createStAXSOAPModelBuilder(reader);
        
        // Get the envelope and then get the body
        SOAPEnvelope env = (SOAPEnvelope) builder.getDocumentElement();
        SOAPBody body = env.getBody();
        
        StreamingOMSerializer serializer = new StreamingOMSerializer();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        XMLStreamWriter writer = StAXUtils.createXMLStreamWriter(byteArrayOutputStream);

        // Serializing the body should cause the usr prefix to be pulled down from the
        // envelope and written in the message.
        serializer.serialize(body.getXMLStreamReaderWithoutCaching(), writer);
        writer.flush();
        String outputString = new String(byteArrayOutputStream.toByteArray());
        
        assertTrue(outputString != null && !"".equals(outputString) && outputString.length() > 1);
        assertTrue(outputString.indexOf(USR_DEF) > 0);
        assertTrue(outputString.indexOf(USR_URI) > 0);
    }
    
    public void testXSITypeNoPullStream() throws Exception {
        
        // Read the SOAP Message that defines prefix "usr" on the envelope and only uses it within an xsi:type
        // within a payload element.
        final String USR_URI = "http://ws.apache.org/axis2/user";
        final String USR_DEF = "xmlns:usr";
        
        XMLStreamReader reader =
            XMLInputFactory.newInstance()
                           .createXMLStreamReader(getTestResource("soap/soapmessageWithXSI.xml"));
        OMXMLParserWrapper builder =
            OMXMLBuilderFactory.createStAXSOAPModelBuilder(reader);
        
        // Get and build the whole tree...this will cause no streaming when doing the write
        SOAPEnvelope env = (SOAPEnvelope) builder.getDocumentElement();
        env.build();
        
        // Get the body
        SOAPBody body = env.getBody();
        
        // Serialize the body
        String outputString = body.toString();
       
        // Serializing the body should cause the usr prefix to be pulled down from the
        // envelope and written in the message.
        
        assertTrue(outputString != null && !"".equals(outputString) && outputString.length() > 1);
        assertTrue(outputString.indexOf(USR_DEF) > 0);
        assertTrue(outputString.indexOf(USR_URI) > 0);
    }
}
