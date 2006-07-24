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

package org.apache.axiom.om.impl.serialize;

import org.apache.axiom.om.OMSerializer;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * Class StreamingOMSerializer
 */
public class StreamingOMSerializer implements XMLStreamConstants, OMSerializer {

    private static int namespaceSuffix = 0;
    public static final String NAMESPACE_PREFIX = "ns";

    /*
    * The behavior of the serializer is such that it returns when it encounters the
    * starting element for the second time. The depth variable tracks the depth of the
    * serilizer and tells it when to return.
    * Note that it is assumed that this serialization starts on an Element.
    */

    /**
     * Field depth
     */
    private int depth = 0;

    /**
     * Method serialize.
     *
     * @param node
     * @param writer
     * @throws XMLStreamException
     */
    public void serialize(XMLStreamReader node, XMLStreamWriter writer)
            throws XMLStreamException {
        serializeNode(node, writer);
    }

    /**
     * Method serializeNode.
     *
     * @param reader
     * @param writer
     * @throws XMLStreamException
     */
    protected void serializeNode(XMLStreamReader reader, XMLStreamWriter writer)
            throws XMLStreamException {
        //TODO We get the StAXWriter at this point and uses it hereafter assuming that this is the only entry point to this class.
        // If there can be other classes calling methodes of this we might need to change methode signatures to OMOutputer
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == START_ELEMENT) {
                serializeElement(reader, writer);
                depth++;
            } else if (event == ATTRIBUTE) {
                serializeAttributes(reader, writer);
            } else if (event == CHARACTERS) {
                serializeText(reader, writer);
            } else if (event == COMMENT) {
                serializeComment(reader, writer);
            } else if (event == CDATA) {
                serializeCData(reader, writer);
            } else if (event == END_ELEMENT) {
                serializeEndElement(writer);
                depth--;
            }else if (event == START_DOCUMENT) {
                depth++; //if a start document is found then increment the depth
            } else if (event == END_DOCUMENT) {
                if (depth!=0) depth--;  //for the end document - reduce the depth
                try {
                    serializeEndElement(writer);
                } catch (Exception e) {
                    //TODO: log exceptions
                }
            }
            if (depth == 0) {
                break;
            }
        }
    }

    /**
     * @param reader
     * @param writer
     * @throws XMLStreamException
     */
    protected void serializeElement(XMLStreamReader reader,
                                    XMLStreamWriter writer)
            throws XMLStreamException {
        String prefix = reader.getPrefix();
        String nameSpaceName = reader.getNamespaceURI();
        if (nameSpaceName != null) {
            String writer_prefix = writer.getPrefix(nameSpaceName);
            if (writer_prefix != null) {
                writer.writeStartElement(nameSpaceName, reader.getLocalName());
            } else {
                if (prefix != null) {
                    writer.writeStartElement(prefix, reader.getLocalName(),
                            nameSpaceName);
                    writer.writeNamespace(prefix, nameSpaceName);
                    writer.setPrefix(prefix, nameSpaceName);
                } else {
                    writer.writeStartElement(nameSpaceName,
                            reader.getLocalName());
                    writer.writeDefaultNamespace(nameSpaceName);
                    writer.setDefaultNamespace(nameSpaceName);
                }
            }
        } else {
            writer.writeStartElement(reader.getLocalName());
        }


        // add the namespaces
        int count = reader.getNamespaceCount();
        String namespacePrefix;
        for (int i = 0; i < count; i++) {
            namespacePrefix = reader.getNamespacePrefix(i);
            if(namespacePrefix != null && namespacePrefix.length()==0)
                continue;

            serializeNamespace(namespacePrefix,
                    reader.getNamespaceURI(i), writer);
        }

        // add attributes
        serializeAttributes(reader, writer);

    }

    /**
     * Method serializeEndElement.
     *
     * @param writer
     * @throws XMLStreamException
     */
    protected void serializeEndElement(XMLStreamWriter writer)
            throws XMLStreamException {
        writer.writeEndElement();
    }

    /**
     * @param reader
     * @param writer
     * @throws XMLStreamException
     */
    protected void serializeText(XMLStreamReader reader,
                                 XMLStreamWriter writer)
            throws XMLStreamException {
        writer.writeCharacters(reader.getText());
    }

    /**
     * Method serializeCData.
     *
     * @param reader
     * @param writer
     * @throws XMLStreamException
     */
    protected void serializeCData(XMLStreamReader reader,
                                  XMLStreamWriter writer)
            throws XMLStreamException {
        writer.writeCData(reader.getText());
    }

    /**
     * Method serializeComment.
     *
     * @param reader
     * @param writer
     * @throws XMLStreamException
     */
    protected void serializeComment(XMLStreamReader reader,
                                    XMLStreamWriter writer)
            throws XMLStreamException {
        writer.writeComment(reader.getText());
    }

    /**
     * @param reader
     * @param writer
     * @throws XMLStreamException
     */
    protected void serializeAttributes(XMLStreamReader reader,
                                       XMLStreamWriter writer)
            throws XMLStreamException {
        int count = reader.getAttributeCount();
        String prefix = null;
        String namespaceName = null;
        String writerPrefix=null;
        for (int i = 0; i < count; i++) {
            prefix = reader.getAttributePrefix(i);
            namespaceName = reader.getAttributeNamespace(i);
            /*
               Due to parser implementations returning null as the namespace URI
              (for the empty namespace) we need to make sure that we deal with
              a namespace name that is not null. The best way to work around this
              issue is to set the namespace uri to "" if it is null
            */
            if (namespaceName==null) namespaceName="";

            writerPrefix =writer.getNamespaceContext().getPrefix(namespaceName);

            if (!"".equals(namespaceName)){
                //prefix has already being declared but this particular attrib has a
                //no prefix attached. So use the prefix provided by the writer
                if (writerPrefix!=null && (prefix==null || prefix.equals(""))){
                    writer.writeAttribute(writerPrefix, namespaceName,
                            reader.getAttributeLocalName(i),
                            reader.getAttributeValue(i));

                    //writer prefix is available but different from the current
                    //prefix of the attrib. We should be decalring the new prefix
                    //as a namespace declaration
                }else if (prefix!=null && !"".equals(prefix)&& !prefix.equals(writerPrefix)){
                    writer.writeNamespace(prefix,namespaceName);
                    writer.writeAttribute(prefix, namespaceName,
                            reader.getAttributeLocalName(i),
                            reader.getAttributeValue(i));

                    //prefix is null (or empty), but the namespace name is valid! it has not
                    //being written previously also. So we need to generate a prefix
                    //here
                }else{
                    prefix = generateUniquePrefix(writer.getNamespaceContext());
                    writer.writeNamespace(prefix,namespaceName);
                    writer.writeAttribute(prefix, namespaceName,
                            reader.getAttributeLocalName(i),
                            reader.getAttributeValue(i));
                }
            }else{
                //empty namespace is equal to no namespace!
                writer.writeAttribute(reader.getAttributeLocalName(i),
                        reader.getAttributeValue(i));
            }


        }
    }

    /**
     * Generates a unique namespace prefix that is not in the
     * scope of the NamespaceContext
     * @param nsCtxt
     * @return string
     */
    private String generateUniquePrefix(NamespaceContext nsCtxt){
        String prefix = NAMESPACE_PREFIX + namespaceSuffix++;
        //null should be returned if the prefix is not bound!
        while(nsCtxt.getNamespaceURI(prefix)!=null){
            prefix = NAMESPACE_PREFIX + namespaceSuffix++;
        }

        return prefix;
    }
    /**
     * Method serializeNamespace.
     * @param prefix
     * @param URI
     * @param writer
     * @throws XMLStreamException
     */
    private void serializeNamespace(String prefix,
                                    String URI,
                                    XMLStreamWriter writer)
            throws XMLStreamException {
        String prefix1 = writer.getPrefix(URI);
        if (prefix1 == null) {
            writer.writeNamespace(prefix, URI);
            writer.setPrefix(prefix, URI);
        }
    }
}
