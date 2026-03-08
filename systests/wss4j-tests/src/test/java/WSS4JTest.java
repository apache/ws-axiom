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
import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.wss4j.common.WSEncryptionPart;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.CryptoFactory;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.engine.WSSecurityEngine;
import org.apache.wss4j.dom.handler.WSHandlerResult;
import org.apache.wss4j.dom.message.WSSecEncrypt;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.apache.wss4j.dom.message.WSSecSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

public class WSS4JTest {
    private Crypto crypto;

    @BeforeEach
    public void setUp() throws WSSecurityException {
        crypto = CryptoFactory.getInstance();
    }

    private static SOAPMessage load(String file) {
        OMMetaFactory metaFactory = OMAbstractFactory.getMetaFactory(OMAbstractFactory.FEATURE_DOM);
        return OMXMLBuilderFactory.createSOAPModelBuilder(
                        metaFactory, WSS4JTest.class.getResourceAsStream(file), null)
                .getSOAPMessage();
    }

    private void testSignature(String file, List<WSEncryptionPart> parts) throws Exception {
        SOAPMessage message = load(file);
        Document doc = (Document) message;

        WSSecHeader secHeader = new WSSecHeader(doc);
        secHeader.insertSecurityHeader();

        WSSecSignature sign = new WSSecSignature(secHeader);
        sign.setUserInfo("key1", "password");
        sign.setKeyIdentifierType(WSConstants.BST_DIRECT_REFERENCE);
        sign.getParts().addAll(parts);

        Document signedDoc = sign.build(crypto);

        WSSecurityEngine secEngine = new WSSecurityEngine();
        WSHandlerResult results = secEngine.processSecurityHeader(signedDoc, null, null, crypto);
        assertThat(results.getResults()).hasSize(2);
    }

    @Test
    public void testSignHeaderAndBody() throws Exception {
        List<WSEncryptionPart> parts = new ArrayList<>();
        parts.add(new WSEncryptionPart("header", "urn:ns1", ""));
        parts.add(new WSEncryptionPart("Body", "http://schemas.xmlsoap.org/soap/envelope/", ""));
        testSignature("envelope1.xml", parts);
    }

    @Test
    public void testSignPartById() throws Exception {
        List<WSEncryptionPart> parts = new ArrayList<>();
        parts.add(new WSEncryptionPart("my-id"));
        testSignature("envelope2.xml", parts);
    }

    @Test
    public void testEncryptHeader() throws Exception {
        SOAPMessage message = load("envelope1.xml");
        Document doc = (Document) message;

        WSSecHeader secHeader = new WSSecHeader(doc);
        secHeader.insertSecurityHeader();

        WSSecEncrypt encrypt = new WSSecEncrypt(secHeader);
        encrypt.setEncryptSymmKey(false);
        encrypt.getParts().add(new WSEncryptionPart("header", "urn:ns1", "Header"));

        KeyStore ks = KeyStore.getInstance("JCEKS");
        try (FileInputStream fis = new FileInputStream("target/keystore")) {
            ks.load(fis, "password".toCharArray());
        }
        SecretKey secretKey = (SecretKey) ks.getKey("key2", "password".toCharArray());

        encrypt.build(crypto, secretKey);
    }
}
