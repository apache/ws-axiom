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
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.axiom.testing.multiton.Instances;
import org.apache.axiom.testing.multiton.Multiton;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * A SOAP test message.
 */
public abstract class TestMessage extends Multiton {
    private final SOAPSpec spec;
    private final String name;
    
    TestMessage(SOAPSpec spec, String name) {
        this.spec = spec;
        this.name = name;
    }
    
    @Instances
    private static TestMessage[] instances() {
        List<TestMessage> instances = new ArrayList<TestMessage>();
        for (TestMessageSet set : getInstances(TestMessageSet.class)) {
            for (SOAPSpec spec : getInstances(SOAPSpec.class)) {
                instances.add(set.getMessage(spec));
            }
        }
        return instances.toArray(new TestMessage[instances.size()]);
    }
    
    /**
     * Get the SOAP version of this message.
     * 
     * @return the SOAP specification version
     */
    public final SOAPSpec getSOAPSpec() {
        return spec;
    }
    
    /**
     * Get the name of this message (for use in test case naming e.g.).
     * 
     * @return the name of this test message
     */
    public final String getName() {
        return name;
    }
    
    /**
     * Get the content of this message.
     * 
     * @return an input stream with the content of this message
     */
    public abstract InputStream getInputStream();
    
    public final Element getEnvelope() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            return factory.newDocumentBuilder().parse(getInputStream()).getDocumentElement();
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }
    
    private Element getBody() {
        NodeList children = getEnvelope().getChildNodes();
        for (int i=0; i<children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE && child.getLocalName().equals("Body")) {
                return (Element)child;
            }
        }
        throw new Error("SOAP message has no body");
    }
    
    public final Element getPayload() {
        NodeList children = getBody().getChildNodes();
        for (int i=0; i<children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                return (Element)child;
            }
        }
        return null;
    }
    
    public final InputSource getPayloadInputSource() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            TransformerFactory.newInstance().newTransformer().transform(
                    new DOMSource(getPayload()), new StreamResult(baos));
        } catch (Exception ex) {
            throw new Error(ex);
        }
        return new InputSource(new ByteArrayInputStream(baos.toByteArray()));
    }
}
