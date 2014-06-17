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
package org.apache.axiom.ts.soap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

final class ConvertedTestMessage extends TestMessage {
    private final TestMessage soap12Message;
    private byte[] content;

    ConvertedTestMessage(TestMessage soap12Message, String name) {
        super(SOAPSpec.SOAP11, name);
        this.soap12Message = soap12Message;
    }

    @Override
    public synchronized InputStream getInputStream() {
        if (content == null) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(true);
                Document document;
                InputStream in = soap12Message.getInputStream();
                try {
                    document = factory.newDocumentBuilder().parse(in);
                } finally {
                    in.close();
                }
                processEnvelope(document.getDocumentElement());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                TransformerFactory.newInstance().newTransformer().transform(
                        new DOMSource(document),
                        new StreamResult(baos));
                content = baos.toByteArray();
            } catch (Exception ex) {
                throw new Error("Error converting SOAP message");
            }
        }
        return new ByteArrayInputStream(content);
    }
    
    private static void processEnvelope(Element envelope) {
        NodeList children = changeSOAPNamespace(envelope).getChildNodes();
        for (int i=0; i<children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element)child;
                String localName = childElement.getLocalName();
                if (localName.equals("Header")) {
                    processHeader(childElement);
                } else if (localName.equals("Body")) {
                    processBody(childElement);
                }
            }
        }
    }
    
    private static void processHeader(Element header) {
        changeSOAPNamespace(header);
        // TODO: need to transform mustUnderstand attributes
    }
    
    private static void processBody(Element body) {
        changeSOAPNamespace(body);
    }
    
    private static Element changeSOAPNamespace(Element element) {
        if (!SOAPSpec.SOAP12.getEnvelopeNamespaceURI().equals(element.getNamespaceURI())) {
            throw new Error("Unexpected namespace");
        }
        return (Element)element.getOwnerDocument().renameNode(element, SOAPSpec.SOAP11.getEnvelopeNamespaceURI(), element.getTagName());
    }
}
