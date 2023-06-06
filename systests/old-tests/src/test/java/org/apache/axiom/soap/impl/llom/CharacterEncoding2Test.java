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

package org.apache.axiom.soap.impl.llom;

import static com.google.common.truth.Truth.assertAbout;
import static org.apache.axiom.truth.xml.XMLTruth.xml;

import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMXMLBuilderFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import junit.framework.TestCase;

public class CharacterEncoding2Test extends TestCase {
    String xml = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>" +
            "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
            "<soap:Body>" +
            "<AgendaPesquisa>" +
            "<status>0</status>" +
            "<ListaContatosPesquisa>" +
            "<tipo>C</tipo>" +
            "<dono>lucia</dono>" +
            "<posicao>177</posicao>" +
            "<nome>Abric� Gimar�es</nome>" +
            "<email></email>" +
            "</ListaContatosPesquisa>" +
            "</AgendaPesquisa>" +
            "</soap:Body>" +
            "</soap:Envelope>";

    public void testISO99591() throws Exception {
        ByteArrayInputStream byteInStr = new ByteArrayInputStream(xml.getBytes(StandardCharsets.ISO_8859_1));

        SOAPModelBuilder builder = OMXMLBuilderFactory.createSOAPModelBuilder(
                byteInStr, null);

        SOAPEnvelope envelope = builder.getSOAPEnvelope();
        envelope.build();

        assertEquals("iso-8859-1", builder.getDocument().getXMLStreamReader().getCharacterEncodingScheme());

        ByteArrayOutputStream byteOutStr = new ByteArrayOutputStream();
        OMOutputFormat outputFormat = new OMOutputFormat();
        outputFormat.setCharSetEncoding("iso-8859-1");
        envelope.serialize(byteOutStr, outputFormat);

        assertAbout(xml())
                .that(new InputStreamReader(new ByteArrayInputStream(byteOutStr.toByteArray()), StandardCharsets.ISO_8859_1))
                .hasSameContentAs(new InputStreamReader(new ByteArrayInputStream(xml.getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.ISO_8859_1));
        
        builder.close();
    }
}
