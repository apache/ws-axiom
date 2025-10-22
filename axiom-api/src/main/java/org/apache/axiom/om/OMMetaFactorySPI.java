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

package org.apache.axiom.om;

import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.apache.axiom.mime.MultipartBody;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.ext.LexicalHandler;

/** For internal use only. */
public interface OMMetaFactorySPI extends OMMetaFactory {
    /**
     * Create an object model builder for plain XML that pulls events from a StAX stream reader.
     *
     * <p>The implementation must perform namespace repairing, i.e. it must add appropriate
     * namespace declarations if undeclared namespaces appear in the StAX stream.
     *
     * @param parser the stream reader to read the XML data from
     * @return the builder
     */
    OMXMLParserWrapper createStAXOMBuilder(XMLStreamReader parser);

    /**
     * Create an object model builder for plain XML that reads a document from the provided input
     * source.
     *
     * @param configuration the parser configuration to use
     * @param is the source of the XML document
     * @return the builder
     */
    OMXMLParserWrapper createOMBuilder(StAXParserConfiguration configuration, InputSource is);

    /**
     * Create an object model builder for plain XML that gets its input from a {@link Source}.
     *
     * @param source the source of the XML document
     * @return the builder
     */
    OMXMLParserWrapper createOMBuilder(Source source);

    /**
     * Create an object model builder for plain XML that gets its input from a DOM tree.
     *
     * @param node the DOM node; must be a {@link Node#DOCUMENT_NODE} or {@link Node#ELEMENT_NODE}
     * @param expandEntityReferences Determines how {@link EntityReference} nodes are handled:
     *     <ul>
     *       <li>If the parameter is <code>false</code> then a single {@link OMEntityReference} will
     *           be created for each {@link EntityReference}. The child nodes of {@link
     *           EntityReference} nodes are not taken into account.
     *       <li>If the parameter is <code>true</code> then no {@link OMEntityReference} nodes are
     *           created and the children of {@link EntityReference} nodes are converted and
     *           inserted into the Axiom tree.
     *     </ul>
     *
     * @return the builder
     */
    OMXMLParserWrapper createOMBuilder(Node node, boolean expandEntityReferences);

    /**
     * Create an object model builder for plain XML that gets its input from a {@link SAXSource}.
     *
     * @param source the source of the XML document
     * @param expandEntityReferences Determines how entity references (i.e. {@link
     *     LexicalHandler#startEntity(String)} and {@link LexicalHandler#endEntity(String)} events)
     *     are handled:
     *     <ul>
     *       <li>If the parameter is <code>false</code> then a single {@link OMEntityReference} will
     *           be created for each pair of {@link LexicalHandler#startEntity(String)} and {@link
     *           LexicalHandler#endEntity(String)} events. Other events reported between these two
     *           events are not taken into account.
     *       <li>If the parameter is <code>true</code> then no {@link OMEntityReference} nodes are
     *           created and {@link LexicalHandler#startEntity(String)} and {@link
     *           LexicalHandler#endEntity(String)} events are ignored. However, events between
     *           {@link LexicalHandler#startEntity(String)} and {@link
     *           LexicalHandler#endEntity(String)} are processed normally.
     *     </ul>
     *
     * @return the builder
     */
    OMXMLParserWrapper createOMBuilder(SAXSource source, boolean expandEntityReferences);

    /**
     * Create an XOP aware object model builder.
     *
     * @param configuration the parser configuration to use
     * @param message the MIME message
     * @return the builder
     */
    OMXMLParserWrapper createOMBuilder(
            StAXParserConfiguration configuration, MultipartBody message);

    OMXMLParserWrapper createOMBuilder(Source rootPart, OMAttachmentAccessor attachmentAccessor);

    /**
     * Create an object model builder for SOAP that pulls events from a StAX stream reader. The
     * implementation will select the appropriate {@link SOAPFactory} based on the namespace URI of
     * the SOAP envelope.
     *
     * @param parser the stream reader to read the SOAP message from
     * @return the builder
     */
    SOAPModelBuilder createStAXSOAPModelBuilder(XMLStreamReader parser);

    /**
     * Create an object model builder for SOAP that reads a message from the provided input source.
     * The implementation will select the appropriate {@link SOAPFactory} based on the namespace URI
     * of the SOAP envelope.
     *
     * @param is the source of the SOAP message
     * @return the builder
     */
    SOAPModelBuilder createSOAPModelBuilder(InputSource is);

    /**
     * Create an object model builder for SOAP that reads a message from the provided {@link
     * Source}. The implementation will select the appropriate {@link SOAPFactory} based on the
     * namespace URI of the SOAP envelope.
     *
     * @param source the source of the SOAP message
     * @return the builder
     */
    SOAPModelBuilder createSOAPModelBuilder(Source source);

    /**
     * Create an MTOM aware object model builder.
     *
     * @param message the MIME message
     * @return the builder
     */
    SOAPModelBuilder createSOAPModelBuilder(MultipartBody message);

    SOAPModelBuilder createSOAPModelBuilder(
            Source rootPart, OMAttachmentAccessor attachmentAccessor);
}
