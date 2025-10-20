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
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.axiom.testing.multiton.Instances;
import org.apache.axiom.ts.xml.MessageContent;
import org.apache.axiom.ts.xml.XMLSample;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/** A SOAP test message. */
public class SOAPSample extends XMLSample {
    /** A SOAP fault response that uses some SOAP 1.2 specific features. */
    public static final SOAPSample SOAP12_FAULT =
            new SOAPSample(SOAPSpec.SOAP12, "test-message/soap12/fault.xml", "soap12/fault.xml");

    /** A SOAP 1.2 message that uses the relay attribute. */
    public static final SOAPSample SOAP12_RELAY =
            new SOAPSample(SOAPSpec.SOAP12, "test-message/soap12/relay.xml", "soap12/relay.xml");

    private final SOAPSpec spec;

    SOAPSample(SOAPSpec spec, MessageContent content, String name) {
        super(content, name);
        this.spec = spec;
    }

    SOAPSample(SOAPSpec spec, String resourceName, String name) {
        this(
                spec,
                MessageContent.fromClasspath(SOAPSample.class.getClassLoader(), resourceName),
                name);
    }

    SOAPSample(SOAPSpec spec, String resourceName) {
        this(spec, resourceName, resourceName);
    }

    @Instances
    private static SOAPSample[] instances() {
        List<SOAPSample> instances = new ArrayList<>();
        for (SOAPSampleSet set : getInstances(SOAPSampleSet.class)) {
            for (SOAPSpec spec : getInstances(SOAPSpec.class)) {
                instances.add(set.getMessage(spec));
            }
        }
        return instances.toArray(new SOAPSample[instances.size()]);
    }

    /**
     * Get the SOAP version of this message.
     *
     * @return the SOAP specification version
     */
    public final SOAPSpec getSOAPSpec() {
        return spec;
    }

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
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE && child.getLocalName().equals("Body")) {
                return (Element) child;
            }
        }
        throw new Error("SOAP message has no body");
    }

    public final Element getPayload() {
        NodeList children = getBody().getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                return (Element) child;
            }
        }
        return null;
    }

    public final InputSource getPayloadInputSource() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            TransformerFactory.newInstance()
                    .newTransformer()
                    .transform(new DOMSource(getPayload()), new StreamResult(baos));
        } catch (Exception ex) {
            throw new Error(ex);
        }
        return new InputSource(new ByteArrayInputStream(baos.toByteArray()));
    }

    @Override
    protected String getMediaType() {
        return spec.getContentType();
    }
}
