package org.apache.axiom.om.impl.serializer;

import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.impl.serialize.StreamingOMSerializer;
import org.apache.axiom.om.impl.llom.factory.OMXMLBuilderFactory;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.custommonkey.xmlunit.XMLTestCase;

import javax.xml.stream.*;
import java.io.*;

public class OMSerializerTest2 extends XMLTestCase {
    private XMLStreamReader reader;
    private XMLStreamWriter writer;
    private static final String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
            "<soapenv:Header/>" +
            "<soapenv:Body>" +
            "<ReportService_GetOlapServers_1_0_Response xmlns=\"http://reportservice.type\">" +
            "<status>" +
            "<returnCode xmlns=\"\">" +
            "<returnCode>0</returnCode>" +
            "</returnCode>" +
            "</status>" +
            "<olapServerId xmlns=\"http://type.ws.analyzer.jrisk.appl.net\">jnp://192.168.111.66:1234</olapServerId>" +
            "</ReportService_GetOlapServers_1_0_Response>" +
            "</soapenv:Body>" +
            "</soapenv:Envelope>";

    public OMSerializerTest2(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        reader =
                XMLInputFactory.newInstance().
                        createXMLStreamReader(
                                new StringReader(xml));
    }

    public void testRawSerializer() throws Exception {
        StreamingOMSerializer serializer = new StreamingOMSerializer();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        writer =
                XMLOutputFactory.newInstance().
                        createXMLStreamWriter(byteArrayOutputStream);
        serializer.serialize(reader, writer);
        writer.flush();

        String outputString = new String(byteArrayOutputStream.toByteArray());
        assertXMLEqual(xml, outputString);

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
        assertXMLEqual(xml, outputString);
    }

    public void testElementPullStream1WithCacheOff() throws Exception {

        StAXSOAPModelBuilder soapBuilder = new StAXSOAPModelBuilder(reader, null);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        writer =
                XMLOutputFactory.newInstance().
                        createXMLStreamWriter(byteArrayOutputStream);

        SOAPEnvelope env = (SOAPEnvelope) soapBuilder.getDocumentElement();
        env.serializeAndConsume(writer);
        writer.flush();

        String outputString = new String(byteArrayOutputStream.toByteArray());
        assertTrue(outputString != null && !"".equals(outputString) && outputString.length() > 1);

        writer =
                XMLOutputFactory.newInstance().
                        createXMLStreamWriter(byteArrayOutputStream);

        StringWriter stringWriter = new StringWriter();

        //now we should not be able to serilaize anything ! this should throw
        //an error
        try {
            env.serializeAndConsume(writer);
            fail();
        } catch (XMLStreamException e) {
            e.printStackTrace(new PrintWriter(stringWriter, true));
            assertTrue(stringWriter.toString()
                    .indexOf("problem accessing the parser. Parser already accessed!") > -1);
        } catch (Exception e) {
            fail("Expecting an XMLStreamException " + e.getMessage());
        }
    }
}
