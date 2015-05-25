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

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

final class ConvertedSOAPSample extends SOAPSample {
    private final SOAPSample soap12Message;
    private byte[] content;

    ConvertedSOAPSample(SOAPSample soap12Message, String name) {
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
                processSOAPElement(document.getDocumentElement(), SOAPElementType.ENVELOPE);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                TransformerFactory.newInstance().newTransformer().transform(
                        new DOMSource(document),
                        new StreamResult(baos));
                content = baos.toByteArray();
            } catch (Exception ex) {
                throw new Error("Error converting SOAP message", ex);
            }
        }
        return new ByteArrayInputStream(content);
    }
    
    private static void processSOAPElement(Element element, SOAPElementType type) {
        if (type == SOAPFaultChild.NODE) {
            element.getParentNode().removeChild(element);
            return;
        }
        QName newName = type.getQName(SOAPSpec.SOAP11);
        String prefix = element.getPrefix();
        if (newName.getNamespaceURI().isEmpty()) {
            prefix = null;
        }
        element = (Element)element.getOwnerDocument().renameNode(element, newName.getNamespaceURI(),
                prefix == null ? newName.getLocalPart() : prefix + ":" + newName.getLocalPart());
        NamedNodeMap attributes = element.getAttributes();
        for (int i=0; i<attributes.getLength(); i++) {
            Attr attr = (Attr)attributes.item(i);
            if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attr.getNamespaceURI())
                    && attr.getValue().equals(SOAPSpec.SOAP12.getEnvelopeNamespaceURI())) {
                attr.setValue(SOAPSpec.SOAP11.getEnvelopeNamespaceURI());
            }
        }
        if (type == SOAPElementType.HEADER) {
            NodeList children = element.getChildNodes();
            for (int i=0; i<children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    Element headerBlock = (Element)child;
                    processBooleanAttribute(headerBlock, BooleanAttribute.MUST_UNDERSTAND);
                    processBooleanAttribute(headerBlock, BooleanAttribute.RELAY);
                }
            }
        } else if (type == SOAPFaultChild.CODE) {
            Element value = getChild(element, SOAPFaultChild.VALUE);
            // TODO: should translate fault code as well
            element.setTextContent(value.getTextContent());
        } else if (type == SOAPFaultChild.REASON) {
            Element text = getChild(element, SOAPFaultChild.TEXT);
            element.setTextContent(text.getTextContent());
        } else {
            SOAPElementType[] childTypes = type.getChildTypes();
            if (childTypes.length != 0) {
                NodeList children = element.getChildNodes();
                for (int i=0; i<children.getLength(); i++) {
                    Node child = children.item(i);
                    if (child.getNodeType() == Node.ELEMENT_NODE) {
                        Element childElement = (Element)child;
                        for (SOAPElementType childType : childTypes) {
                            if (hasName(childElement, childType.getQName(SOAPSpec.SOAP12))) {
                                processSOAPElement(childElement, childType);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    
    private static void processBooleanAttribute(Element headerBlock, BooleanAttribute booleanAttribute) {
        String localName = booleanAttribute.getName();
        Attr attr = headerBlock.getAttributeNodeNS(SOAPSpec.SOAP12.getEnvelopeNamespaceURI(), localName);
        if (attr != null) {
            if (booleanAttribute.isSupported(SOAPSpec.SOAP11)) {
                String prefix = attr.getPrefix();
                attr = (Attr)attr.getOwnerDocument().renameNode(attr, SOAPSpec.SOAP11.getEnvelopeNamespaceURI(), prefix + ":" + localName);
                String stringValue = attr.getValue();
                boolean value = false;
                for (BooleanLiteral booleanLiteral : SOAPSpec.SOAP12.getBooleanLiterals()) {
                    if (stringValue.equals(booleanLiteral.getLexicalRepresentation())) {
                        value = booleanLiteral.getValue();
                        break;
                    }
                }
                attr.setValue(SOAPSpec.SOAP11.getCanonicalRepresentation(value));
            } else {
                headerBlock.removeAttributeNode(attr);
            }
        }
    }
    
    private static boolean hasName(Element element, QName name) {
        String namespaceURI = element.getNamespaceURI();
        if (namespaceURI == null) {
            namespaceURI = "";
        }
        return namespaceURI.equals(name.getNamespaceURI()) && element.getLocalName().equals(name.getLocalPart());
    }
    
    private static Element getChild(Element element, SOAPElementType type) {
        NodeList children = element.getChildNodes();
        for (int i=0; i<children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element)child;
                if (hasName(childElement, type.getQName(SOAPSpec.SOAP12))) {
                    return childElement;
                }
            }
        }
        return null;
    }
}
