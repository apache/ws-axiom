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
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.TestConstants;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.llom.factory.OMXMLBuilderFactory;
import org.apache.axiom.om.impl.serialize.StreamingOMSerializer;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;

public class OMSerializerTest extends AbstractTestCase {
    private XMLStreamReader reader;
    private XMLStreamWriter writer;
    private File tempFile;

    public OMSerializerTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        reader = StAXUtils.createXMLStreamReader(getTestResource(TestConstants.SOAP_SOAPMESSAGE));
        tempFile = File.createTempFile("temp", "xml");
//        writer =
//                XMLOutputFactory.newInstance().
//                        createXMLStreamWriter(new FileOutputStream(tempFile));


    }

    public void testRawSerializer() throws Exception {
        StreamingOMSerializer serializer = new StreamingOMSerializer();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        writer = StAXUtils.createXMLStreamWriter(byteArrayOutputStream);
        //serializer.setNamespacePrefixStack(new Stack());
        serializer.serialize(reader, writer);
        writer.flush();

        String outputString = new String(byteArrayOutputStream.toByteArray());
        assertTrue(outputString != null && !"".equals(outputString) && outputString.length() > 1);

    }

    public void testElementPullStream1() throws Exception {
        OMXMLParserWrapper builder = OMXMLBuilderFactory.createStAXSOAPModelBuilder(
                OMAbstractFactory.getSOAP11Factory(),
                reader);
        SOAPEnvelope env = (SOAPEnvelope) builder.getDocumentElement();
        StreamingOMSerializer serializer = new StreamingOMSerializer();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        writer = StAXUtils.createXMLStreamWriter(byteArrayOutputStream);

        serializer.serialize(env.getXMLStreamReaderWithoutCaching(), writer);
        writer.flush();

        String outputString = new String(byteArrayOutputStream.toByteArray());
        assertTrue(outputString != null && !"".equals(outputString) && outputString.length() > 1);
    }

    public void testElementPullStream1WithCacheOff() throws Exception {

        StAXSOAPModelBuilder soapBuilder = new StAXSOAPModelBuilder(reader, null);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        writer = StAXUtils.createXMLStreamWriter(byteArrayOutputStream,
                OMConstants.DEFAULT_CHAR_SET_ENCODING);

        SOAPEnvelope env = (SOAPEnvelope) soapBuilder.getDocumentElement();
        env.serializeAndConsume(writer);
        writer.flush();

        String outputString = new String(byteArrayOutputStream.toByteArray());
        assertTrue(outputString != null && !"".equals(outputString) && outputString.length() > 1);

        writer = StAXUtils.createXMLStreamWriter(byteArrayOutputStream,
                OMConstants.DEFAULT_CHAR_SET_ENCODING);

        StringWriter stringWriter = new StringWriter();

        //now we should not be able to serilaize anything ! this should throw
        //an error
        try {
            env.serializeAndConsume(writer);
            fail();
        } catch (UnsupportedOperationException e) {
            e.printStackTrace(new PrintWriter(stringWriter, true));
            assertTrue(stringWriter.toString()
                                   .indexOf("The parser is already consumed!") > -1);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Expecting an XMLStreamException, but got instead: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void testElementPullStream2() throws Exception {
        OMXMLParserWrapper builder = OMXMLBuilderFactory.createStAXSOAPModelBuilder(
                OMAbstractFactory.getSOAP11Factory(),
                reader);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        writer = StAXUtils.createXMLStreamWriter(byteArrayOutputStream);

        SOAPEnvelope env = (SOAPEnvelope) builder.getDocumentElement();
        SOAPBody body = env.getBody();
        StreamingOMSerializer serializer = new StreamingOMSerializer();
        serializer.serialize(body.getXMLStreamReaderWithoutCaching(),
                             writer);
        writer.flush();

        String outputString = new String(byteArrayOutputStream.toByteArray());
        assertTrue(outputString != null && !"".equals(outputString) && outputString.length() > 1);
    }

    public void testDefaultNsSerialization() {
        try {
            StAXOMBuilder builder = new StAXOMBuilder(getTestResource("original.xml"));
            String xml = builder.getDocumentElement().toString();
            assertEquals("There shouldn't be any xmlns=\"\"", -1, xml.indexOf("xmlns=\"\""));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    public void testXSITypePullStream() throws Exception {
        
        // Read the SOAP Message that defines prefix "usr" on the envelope and only uses it within an xsi:type
        // within a payload element.
        final String USR_URI = "http://ws.apache.org/axis2/user";
        final String USR_DEF = "xmlns:usr";
        
        reader =
            XMLInputFactory.newInstance()
                           .createXMLStreamReader(getTestResource("soap/soapmessageWithXSI.xml"));
        OMXMLParserWrapper builder =
            OMXMLBuilderFactory.createStAXSOAPModelBuilder(OMAbstractFactory.getSOAP11Factory(),
                                                           reader);
        
        // Get the envelope and then get the body
        SOAPEnvelope env = (SOAPEnvelope) builder.getDocumentElement();
        SOAPBody body = env.getBody();
        
        StreamingOMSerializer serializer = new StreamingOMSerializer();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        writer = StAXUtils.createXMLStreamWriter(byteArrayOutputStream);

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
        
        reader =
            XMLInputFactory.newInstance()
                           .createXMLStreamReader(getTestResource("soap/soapmessageWithXSI.xml"));
        OMXMLParserWrapper builder =
            OMXMLBuilderFactory.createStAXSOAPModelBuilder(OMAbstractFactory.getSOAP11Factory(),
                                                           reader);
        
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

    protected void tearDown() throws Exception {
        reader.close();
        tempFile.delete();
    }
}
