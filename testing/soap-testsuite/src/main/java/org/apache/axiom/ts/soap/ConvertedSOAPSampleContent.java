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

import static org.apache.axiom.util.xml.XMLChar.isWhitespace;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.axiom.testing.multiton.Multiton;
import org.apache.axiom.ts.xml.ComputedMessageContent;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

final class ConvertedSOAPSampleContent extends ComputedMessageContent {
    private static Map<String, String> faultCodeMap = new HashMap<>();

    static {
        faultCodeMap.put(
                SOAPSpec.SOAP12.getSenderFaultCode().getLocalPart(),
                SOAPSpec.SOAP11.getSenderFaultCode().getLocalPart());
        faultCodeMap.put(
                SOAPSpec.SOAP12.getReceiverFaultCode().getLocalPart(),
                SOAPSpec.SOAP11.getReceiverFaultCode().getLocalPart());
    }

    private final SOAPSample soap12Message;

    ConvertedSOAPSampleContent(SOAPSample soap12Message) {
        this.soap12Message = soap12Message;
    }

    @Override
    protected void buildContent(OutputStream out) throws Exception {
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
        TransformerFactory.newInstance()
                .newTransformer()
                .transform(new DOMSource(document), new StreamResult(out));
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
        element =
                (Element)
                        element.getOwnerDocument()
                                .renameNode(
                                        element,
                                        newName.getNamespaceURI(),
                                        prefix == null
                                                ? newName.getLocalPart()
                                                : prefix + ":" + newName.getLocalPart());
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Attr attr = (Attr) attributes.item(i);
            if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(attr.getNamespaceURI())
                    && attr.getValue().equals(SOAPSpec.SOAP12.getEnvelopeNamespaceURI())) {
                attr.setValue(SOAPSpec.SOAP11.getEnvelopeNamespaceURI());
            }
        }
        if (type == SOAPElementType.HEADER) {
            Node child = element.getFirstChild();
            while (child != null) {
                Node nextChild = child.getNextSibling();
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    Element headerBlock = (Element) child;
                    Attr roleAttr =
                            headerBlock.getAttributeNodeNS(
                                    SOAPSpec.SOAP12.getEnvelopeNamespaceURI(),
                                    HeaderBlockAttribute.ROLE.getName(SOAPSpec.SOAP12));
                    if (roleAttr != null
                            && roleAttr.getValue()
                                    .equals("http://www.w3.org/2003/05/soap-envelope/role/none")) {
                        element.removeChild(headerBlock);
                    } else {
                        for (HeaderBlockAttribute attribute :
                                Multiton.getInstances(HeaderBlockAttribute.class)) {
                            processAttribute(headerBlock, attribute);
                        }
                    }
                }
                child = nextChild;
            }
        } else if (type == SOAPFaultChild.CODE) {
            final Element value = getChild(element, SOAPFaultChild.VALUE);
            element.setTextContent(
                    transform(
                            value.getTextContent(),
                            new TextTransformer() {
                                @Override
                                public String transform(String in) {
                                    int idx = in.indexOf(':');
                                    if (idx == -1) {
                                        return in;
                                    }
                                    String prefix = in.substring(0, idx);
                                    if (!SOAPSpec.SOAP12
                                            .getEnvelopeNamespaceURI()
                                            .equals(value.lookupNamespaceURI(prefix))) {
                                        return in;
                                    }
                                    String newCode = faultCodeMap.get(in.substring(idx + 1));
                                    if (newCode == null) {
                                        return in;
                                    }
                                    return prefix + ":" + newCode;
                                }
                            }));
        } else if (type == SOAPFaultChild.REASON) {
            Element text = getChild(element, SOAPFaultChild.TEXT);
            element.setTextContent(text.getTextContent());
        } else {
            SOAPElementType[] childTypes = type.getChildTypes();
            if (childTypes.length != 0) {
                NodeList children = element.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    Node child = children.item(i);
                    if (child.getNodeType() == Node.ELEMENT_NODE) {
                        Element childElement = (Element) child;
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

    private static void processAttribute(Element headerBlock, HeaderBlockAttribute attribute) {
        Attr attr =
                headerBlock.getAttributeNodeNS(
                        SOAPSpec.SOAP12.getEnvelopeNamespaceURI(),
                        attribute.getName(SOAPSpec.SOAP12));
        if (attr != null) {
            if (attribute.isSupported(SOAPSpec.SOAP11)) {
                String prefix = attr.getPrefix();
                attr =
                        (Attr)
                                attr.getOwnerDocument()
                                        .renameNode(
                                                attr,
                                                SOAPSpec.SOAP11.getEnvelopeNamespaceURI(),
                                                prefix + ":" + attribute.getName(SOAPSpec.SOAP11));
                if (attribute.isBoolean()) {
                    String stringValue = attr.getValue();
                    boolean value = false;
                    for (BooleanLiteral booleanLiteral : SOAPSpec.SOAP12.getBooleanLiterals()) {
                        if (stringValue.equals(booleanLiteral.getLexicalRepresentation())) {
                            value = booleanLiteral.getValue();
                            break;
                        }
                    }
                    attr.setValue(SOAPSpec.SOAP11.getCanonicalRepresentation(value));
                } else if (attribute == HeaderBlockAttribute.ROLE) {
                    String value = attr.getValue();
                    if (value.equals(SOAPSpec.SOAP12.getNextRoleURI())) {
                        attr.setValue(SOAPSpec.SOAP11.getNextRoleURI());
                    } else if (value.equals(
                            "http://www.w3.org/2003/05/soap-envelope/role/ultimateReceiver")) {
                        headerBlock.removeAttributeNode(attr);
                    }
                }
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
        return namespaceURI.equals(name.getNamespaceURI())
                && element.getLocalName().equals(name.getLocalPart());
    }

    private static Element getChild(Element element, SOAPElementType type) {
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) child;
                if (hasName(childElement, type.getQName(SOAPSpec.SOAP12))) {
                    return childElement;
                }
            }
        }
        return null;
    }

    private static String transform(String text, TextTransformer transformer) {
        int start = 0;
        while (isWhitespace(text.charAt(start))) {
            if (++start == text.length()) {
                return text;
            }
        }
        int end = text.length();
        while (isWhitespace(text.charAt(end - 1))) {
            end--;
        }
        return text.substring(0, start)
                + transformer.transform(text.substring(start, end))
                + text.substring(end);
    }
}
