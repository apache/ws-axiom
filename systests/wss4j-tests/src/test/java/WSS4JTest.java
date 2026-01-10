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
import static com.google.common.truth.Truth.assertThat;

import java.util.Vector;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSEncryptionPart;
import org.apache.ws.security.WSSecurityEngine;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.message.WSSecEncrypt;
import org.apache.ws.security.message.WSSecHeader;
import org.apache.ws.security.message.WSSecSignature;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

public class WSS4JTest {
    private Crypto crypto;

    @Before
    public void setUp() throws WSSecurityException {
        crypto = CryptoFactory.getInstance();
    }

    private static SOAPMessage load(String file) {
        OMMetaFactory metaFactory = OMAbstractFactory.getMetaFactory(OMAbstractFactory.FEATURE_DOM);
        return OMXMLBuilderFactory.createSOAPModelBuilder(
                        metaFactory, WSS4JTest.class.getResourceAsStream(file), null)
                .getSOAPMessage();
    }

    private void testSignature(String file, Vector<WSEncryptionPart> parts) throws Exception {
        WSSecSignature sign = new WSSecSignature();
        sign.setUserInfo("key1", "password");
        sign.setKeyIdentifierType(WSConstants.BST_DIRECT_REFERENCE);
        sign.setParts(parts);

        SOAPMessage message = load(file);
        Document doc = (Document) message;

        WSSecHeader secHeader = new WSSecHeader();
        secHeader.insertSecurityHeader(doc);

        Document signedDoc = sign.build(doc, crypto, secHeader);

        WSSecurityEngine secEngine = new WSSecurityEngine();
        assertThat(secEngine.processSecurityHeader(signedDoc, null, null, crypto)).hasSize(2);
    }

    @Test
    public void testSignHeaderAndBody() throws Exception {
        Vector<WSEncryptionPart> parts = new Vector<WSEncryptionPart>();
        parts.add(new WSEncryptionPart("header", "urn:ns1", ""));
        parts.add(new WSEncryptionPart("Body", "http://schemas.xmlsoap.org/soap/envelope/", ""));
        testSignature("envelope1.xml", parts);
    }

    @Test
    public void testSignPartById() throws Exception {
        Vector<WSEncryptionPart> parts = new Vector<WSEncryptionPart>();
        parts.add(new WSEncryptionPart("my-id"));
        testSignature("envelope2.xml", parts);
    }

    @Test
    public void testEncryptHeader() throws Exception {
        Vector<WSEncryptionPart> parts = new Vector<WSEncryptionPart>();
        parts.add(new WSEncryptionPart("header", "urn:ns1", "Header"));
        WSSecEncrypt encrypt = new WSSecEncrypt();
        encrypt.setUserInfo("key2", "password");
        encrypt.setEncryptSymmKey(false);
        encrypt.setParts(parts);
        SOAPMessage message = load("envelope1.xml");
        Document doc = (Document) message;
        WSSecHeader secHeader = new WSSecHeader();
        secHeader.insertSecurityHeader(doc);
        encrypt.build(doc, crypto, secHeader);
    }
}
