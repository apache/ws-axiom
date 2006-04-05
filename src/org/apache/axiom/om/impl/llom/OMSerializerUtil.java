/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.serialize.StreamingOMSerializer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.util.Iterator;

public class OMSerializerUtil {

    static long nsCounter = 0;

    /**
     * Method serializeEndpart.
     *
     * @param writer
     * @throws javax.xml.stream.XMLStreamException
     *
     */
    public static void serializeEndpart(XMLStreamWriter writer)
            throws XMLStreamException {
        writer.writeEndElement();
    }

    /**
     * Method serializeAttribute.
     *
     * @param attr
     * @param writer
     * @throws XMLStreamException
     */
    public static void serializeAttribute(OMAttribute attr, XMLStreamWriter writer)
            throws XMLStreamException {

        // first check whether the attribute is associated with a namespace
        OMNamespace ns = attr.getNamespace();
        String prefix = null;
        String namespaceName = null;
        if (ns != null) {

            // add the prefix if it's availble
            prefix = ns.getPrefix();
            namespaceName = ns.getName();
            if (prefix != null) {
                writer.writeAttribute(prefix, namespaceName,
                        attr.getLocalName(), attr.getAttributeValue());
            } else {
                writer.writeAttribute(namespaceName, attr.getLocalName(),
                        attr.getAttributeValue());
            }
        } else {
            String localName = attr.getLocalName();
            String attributeValue = attr.getAttributeValue();
            writer.writeAttribute(localName, attributeValue);
        }
    }

    /**
     * Method serializeNamespace.
     *
     * @param namespace
     * @param writer
     * @throws XMLStreamException
     */
    public static void serializeNamespace(OMNamespace namespace, XMLStreamWriter writer)
            throws XMLStreamException {
        if (namespace == null) {
            return;
        }
        String uri = namespace.getName();
        String prefix = namespace.getPrefix();

        if (uri != null && !"".equals(uri)) {
            String prefixFromWriter = writer.getPrefix(uri);

            // lets see whether we have default namespace now
            if (prefix != null && "".equals(prefix) && prefixFromWriter == null) {
                // this has not been declared earlier
                writer.writeDefaultNamespace(uri);
                writer.setDefaultNamespace(uri);
            } else {
                prefix = prefix == null ? getNextNSPrefix() : prefix;
                if (prefix != null && !prefix.equals(prefixFromWriter)) {
                    writer.writeNamespace(prefix, uri);
                }
            }
        }
    }

    /**
     * Method serializeStartpart.
     *
     * @param writer
     * @throws XMLStreamException
     */
    public static void serializeStartpart
            (OMElement
                    element, XMLStreamWriter writer)
            throws XMLStreamException {
        String nameSpaceName;
        String writer_prefix;
        String prefix;
        if (element.getNamespace() != null) {
            nameSpaceName = element.getNamespace().getName();
            prefix = element.getNamespace().getPrefix();
            if (nameSpaceName != null) {
                writer_prefix = writer.getPrefix(nameSpaceName);
                if (writer_prefix != null) {
                    writer.writeStartElement(nameSpaceName,
                            element.getLocalName());
                } else {
                    prefix = (prefix == null) ? getNextNSPrefix() : prefix;
                    writer.writeStartElement(prefix, element.getLocalName(),
                            nameSpaceName);
                    writer.writeNamespace(prefix, nameSpaceName);
                    writer.setPrefix(prefix, nameSpaceName);

                }
            } else {
                writer.writeStartElement(element.getLocalName());
            }
        } else {
            writer.writeStartElement(element.getLocalName());

            /** // we need to check whether there's a default namespace visible at this point because
             // otherwise this element will go into that namespace unintentionally. So we check
             // whether there is a default NS visible and if so turn it off.
             if (writer.getNamespaceContext().getNamespaceURI("") != null) {
             writer.writeDefaultNamespace("");
             }   */

        }

        // add the namespaces
        serializeNamespaces(element, writer);

        // add the elements attributes
        serializeAttributes(element, writer);
    }

    public static void serializeNamespaces
            (OMElement
                    element,
             XMLStreamWriter writer) throws XMLStreamException {
        Iterator namespaces = element.getAllDeclaredNamespaces();
        if (namespaces != null) {
            while (namespaces.hasNext()) {
                serializeNamespace((OMNamespace) namespaces.next(), writer);
            }
        }
    }

    public static void serializeAttributes
            (OMElement
                    element,
             XMLStreamWriter writer) throws XMLStreamException {
        Iterator attributes = element.getAllAttributes();
        if (attributes != null && attributes.hasNext()) {
            while (attributes.hasNext()) {
                serializeAttribute((OMAttribute) attributes.next(),
                        writer);
            }
        }
    }

    /**
     * Method serializeNormal.
     *
     * @param writer
     * @param cache
     * @throws XMLStreamException
     */
    public static void serializeNormal
            (OMElement
                    element, XMLStreamWriter writer, boolean cache)
            throws XMLStreamException {

        if (cache) {
            element.build();
        }

        serializeStartpart(element, writer);
        OMNode firstChild = element.getFirstOMChild();
        if (firstChild != null) {
            if (cache) {
                ((OMNodeEx) firstChild).serialize(writer);
            } else {
                ((OMNodeEx) firstChild).serializeAndConsume(writer);
            }
        }
        serializeEndpart(writer);
    }

    public static void serializeByPullStream
            (OMElement
                    element, XMLStreamWriter writer) throws XMLStreamException {
        serializeByPullStream(element, writer, false);
    }

    public static void serializeByPullStream
            (OMElement
                    element, XMLStreamWriter writer, boolean cache) throws XMLStreamException {
        StreamingOMSerializer streamingOMSerializer = new StreamingOMSerializer();
        if (cache) {
            streamingOMSerializer.serialize(element.getXMLStreamReader(),
                    writer);
        } else {
            XMLStreamReader xmlStreamReaderWithoutCaching = element.getXMLStreamReaderWithoutCaching();
            streamingOMSerializer.serialize(xmlStreamReaderWithoutCaching,
                    writer);
        }
    }

    public static String getNextNSPrefix() {
        return "axis2ns" + ++nsCounter % Long.MAX_VALUE;
    }
}
