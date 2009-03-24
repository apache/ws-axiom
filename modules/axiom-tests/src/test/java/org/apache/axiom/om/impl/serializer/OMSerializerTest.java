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
        reader =
                XMLInputFactory.newInstance().
                        createXMLStreamReader(getTestResource(TestConstants.SOAP_SOAPMESSAGE));
        tempFile = File.createTempFile("temp", "xml");
//        writer =
//                XMLOutputFactory.newInstance().
//                        createXMLStreamWriter(new FileOutputStream(tempFile));


    }

    public void testRawSerializer() throws Exception {
        StreamingOMSerializer serializer = new StreamingOMSerializer();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        writer =
                XMLOutputFactory.newInstance().
                        createXMLStreamWriter(byteArrayOutputStream);
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
        writer =
                XMLOutputFactory.newInstance().
                        createXMLStreamWriter(byteArrayOutputStream);

        serializer.serialize(env.getXMLStreamReaderWithoutCaching(), writer);
        writer.flush();

        String outputString = new String(byteArrayOutputStream.toByteArray());
        assertTrue(outputString != null && !"".equals(outputString) && outputString.length() > 1);
    }

    public void testElementPullStream1WithCacheOff() throws Exception {

        StAXSOAPModelBuilder soapBuilder = new StAXSOAPModelBuilder(reader, null);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        writer =
                XMLOutputFactory.newInstance().
                        createXMLStreamWriter(byteArrayOutputStream, OMConstants.DEFAULT_CHAR_SET_ENCODING);

        SOAPEnvelope env = (SOAPEnvelope) soapBuilder.getDocumentElement();
        env.serializeAndConsume(writer);
        writer.flush();

        String outputString = new String(byteArrayOutputStream.toByteArray());
        assertTrue(outputString != null && !"".equals(outputString) && outputString.length() > 1);

        writer =
                XMLOutputFactory.newInstance().
                        createXMLStreamWriter(byteArrayOutputStream, OMConstants.DEFAULT_CHAR_SET_ENCODING);

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
        writer =
                XMLOutputFactory.newInstance().
                        createXMLStreamWriter(byteArrayOutputStream);

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
            StAXOMBuilder builder = new StAXOMBuilder(getTestResource("xml/original.xml"));
            String xml = builder.getDocumentElement().toString();
            assertEquals("There shouldn't be any xmlns=\"\"", -1, xml.indexOf("xmlns=\"\""));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    protected void tearDown() throws Exception {
        tempFile.delete();
    }
}
