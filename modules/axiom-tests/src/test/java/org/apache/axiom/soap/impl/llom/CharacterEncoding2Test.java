package org.apache.axiom.soap.impl.llom;

import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.apache.axiom.om.OMOutputFormat;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;

import javax.xml.stream.XMLInputFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.InputStreamReader;

public class CharacterEncoding2Test extends XMLTestCase {
    String xml = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>" +
            "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
            "<soap:Body>" +
            "<AgendaPesquisa>" +
            "<status>0</status>" +
            "<ListaContatosPesquisa>" +
            "<tipo>C</tipo>" +
            "<dono>lucia</dono>" +
            "<posicao>177</posicao>" +
            "<nome>Abricó Gimarães</nome>" +
            "<email></email>" +
            "</ListaContatosPesquisa>" +
            "</AgendaPesquisa>" +
            "</soap:Body>" +
            "</soap:Envelope>";

    public void testISO99591() throws Exception {
        ByteArrayInputStream byteInStr = new ByteArrayInputStream(xml.getBytes("iso-8859-1"));

        StAXSOAPModelBuilder builder = new StAXSOAPModelBuilder(
                XMLInputFactory.newInstance().createXMLStreamReader(byteInStr));

        SOAPEnvelope envelope = builder.getSOAPEnvelope();
        envelope.build();

        assertEquals("iso-8859-1", envelope.getXMLStreamReader().getCharacterEncodingScheme());

        ByteArrayOutputStream byteOutStr = new ByteArrayOutputStream();
        OMOutputFormat outputFormat = new OMOutputFormat();
        outputFormat.setCharSetEncoding("iso-8859-1");
        envelope.serialize(byteOutStr, outputFormat);

        assertXMLEqual(new InputStreamReader(new ByteArrayInputStream(xml.getBytes("iso-8859-1")),"iso-8859-1"),
                new InputStreamReader(new ByteArrayInputStream(byteOutStr.toByteArray()),"iso-8859-1"));
    }
}
