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

package org.apache.axiom.om.impl.util;

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

            // Handling Default Namespaces First
            // Case 1 :
            //        here we are trying define a default namespace. But has this been defined in the current context.
            //        yes, there can be a default namespace, but it may have a different URI. If its a different URI
            //        then explicitly define the default namespace here.
            // Case 2 :
            //        The passed in namespace is a default ns, but there is a non-default ns declared
            //        in the current scope.
            if (("".equals(prefix) && "".equals(prefixFromWriter) && !uri.equals(writer.getNamespaceContext().getNamespaceURI(""))) ||
                    (prefix != null && "".equals(prefix) && (prefixFromWriter == null || !prefix.equals(prefixFromWriter))))
            {
                // this has not been declared earlier
                writer.writeDefaultNamespace(uri);
                writer.setDefaultNamespace(uri);
            } else {
                prefix = prefix == null ? getNextNSPrefix(writer) : prefix;
                if (prefix != null && !prefix.equals(prefixFromWriter) && !checkForPrefixInTheCurrentContext(writer, uri, prefix))
                {
                    writer.writeNamespace(prefix, uri);
                    writer.setPrefix(prefix, uri);
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
            if (nameSpaceName != null && !"".equals(nameSpaceName)) {
                writer_prefix = writer.getPrefix(nameSpaceName);

                // if the writer has no prefix registered for the given namespace, no matter what prefix the
                // ns contains, use that to handle the ns
                if (writer_prefix == null && !"".equals(prefix)) {
                    prefix = (prefix == null) ? getNextNSPrefix(writer) : prefix;
                    writer.writeStartElement(prefix, element.getLocalName(),
                            nameSpaceName);
                    writer.writeNamespace(prefix, nameSpaceName);
                    writer.setPrefix(prefix, nameSpaceName);
                } else if (prefix == null) {
                    // by this time prefix is null and writer_prefix is not null
                    writer.writeStartElement(nameSpaceName,
                            element.getLocalName());
                } else {
                    // now lets handle the case where (prefix != null && writer_prefix != null)
                    if ("".equals(prefix) && "".equals(writer_prefix)) {
                        // now this element is trying to use a default namespace and at the same point
                        // exists a default namespace with the given ns URI.
                        // but the problem here is that, what if the xml is like the following
                        // <One xmlns="one.org" >
                        //    <Two xmlns="two.org">
                        //         <Three xmlns="one.org" />
                        //    </Two>
                        // </One>
                        //
                        // if we ask the prefix registered with one.org, the parser will return ""
                        // which is the default ns. But if we do not declare a new default ns explicitly here
                        // then this causes problem as element Two has already overriden the default ns.
                        //
                        // Solution for this is to ask from the parser the nsURI attached to "" at this
                        // moment and compare the return uri with namespace name
                        if (nameSpaceName.equals(writer.getNamespaceContext().getNamespaceURI("")))
                        {
                            writer.writeStartElement(nameSpaceName, element.getLocalName());
                        } else {
                            writer.writeStartElement(prefix, element.getLocalName(),
                                    nameSpaceName);
                            writer.writeDefaultNamespace(nameSpaceName);
                            writer.setDefaultNamespace(nameSpaceName);
                        }
                    } else if (prefix.equals(writer_prefix)) {
                        writer.writeStartElement(nameSpaceName, element.getLocalName());
                    } else if ("".equals(prefix)) {
                        writer.writeStartElement(prefix, element.getLocalName(),
                                nameSpaceName);
                        writer.writeDefaultNamespace(nameSpaceName);
                        writer.setDefaultNamespace(nameSpaceName);
                    } else {
                        // now the left scenario is this
                        // 1. prefix != "" && writer_prefix != "" but writer_prefix != prefix
                        // 2. prefix != "" && writer_prefix == ""

                        // In both the above cases this xml may contain more than one prefix for the
                        // same URI. Check them all.

                        // this flag will remember whether this ns is declared in the scope with the
                        // given prefix or not
                        boolean found = checkForPrefixInTheCurrentContext(writer, nameSpaceName, prefix);

                        if (!found) {
                            // seems we haven't found one in the current scope. So declare it.
                            writer.writeStartElement(prefix, element.getLocalName(),
                                    nameSpaceName);
                            writer.writeNamespace(prefix, nameSpaceName);
                            writer.setPrefix(prefix, nameSpaceName);
                        } else {
                            writer.writeStartElement(prefix, element.getLocalName(), nameSpaceName);
                        }

                    }
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

    private static boolean checkForPrefixInTheCurrentContext(XMLStreamWriter writer, String nameSpaceName, String prefix) throws XMLStreamException {
        Iterator prefixesIter = writer.getNamespaceContext().getPrefixes(nameSpaceName);
        while (prefixesIter.hasNext()) {
            String prefix_w = (String) prefixesIter.next();
            if (prefix_w.equals(prefix)) {
                // if found do not declare the ns
                return true;
            }
        }
        return false;
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

    public static String getNextNSPrefix(XMLStreamWriter writer) {
        String prefix = getNextNSPrefix();
        while (writer.getNamespaceContext().getNamespaceURI(prefix) != null) {
            prefix = getNextNSPrefix();
        }

        return prefix;
    }
}
