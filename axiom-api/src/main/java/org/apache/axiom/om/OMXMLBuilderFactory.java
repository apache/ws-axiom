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

import java.io.InputStream;
import java.io.Reader;
import java.text.ParseException;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.mime.ContentType;
import org.apache.axiom.om.impl.builder.AttachmentsMimePartProvider;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.ext.LexicalHandler;

/**
 * Provides static factory methods to create various kinds of object model builders from different
 * types of input sources. The methods defined by this class are the starting point to parse XML
 * documents into Axiom trees.
 */
public class OMXMLBuilderFactory {
    private OMXMLBuilderFactory() {}
    
    /**
     * Create an object model builder for plain XML that pulls events from a StAX stream reader.
     * <p>
     * The reader must be positioned on a {@link XMLStreamConstants#START_DOCUMENT} or
     * {@link XMLStreamConstants#START_ELEMENT} event. If the current event is
     * {@link XMLStreamConstants#START_DOCUMENT} then the builder will consume events up to the
     * {@link XMLStreamConstants#END_DOCUMENT} event. If the current event is
     * {@link XMLStreamConstants#START_ELEMENT}, then the builder will consume events up to the
     * corresponding {@link XMLStreamConstants#END_ELEMENT}. After the object model is completely
     * built, the stream reader will be positioned on the event immediately following this
     * {@link XMLStreamConstants#END_ELEMENT} event. This means that this method can be used in a
     * well defined way to build an object model from a fragment (corresponding to a single element)
     * of the document represented by the stream reader.
     * <p>
     * The returned builder also performs namespace repairing, i.e. it adds appropriate namespace
     * declarations if undeclared namespaces appear in the StAX stream.
     * 
     * @param parser
     *            the stream reader to read the XML data from
     * @return the builder
     * @throws OMException
     *             if the stream reader is positioned on an event other than
     *             {@link XMLStreamConstants#START_DOCUMENT} or
     *             {@link XMLStreamConstants#START_ELEMENT}
     */
    public static OMXMLParserWrapper createStAXOMBuilder(XMLStreamReader parser) {
        OMMetaFactory metaFactory = OMAbstractFactory.getMetaFactory();
        return metaFactory.createStAXOMBuilder(metaFactory.getOMFactory(), parser);
    }
    
    /**
     * Create an object model builder that pulls events from a StAX stream reader using a specified
     * object model factory.
     * <p>
     * See {@link #createStAXOMBuilder(XMLStreamReader)} for more information about the behavior of
     * the returned builder.
     * 
     * @param omFactory
     *            the object model factory to use
     * @param parser
     *            the stream reader to read the XML data from
     * @return the builder
     */
    public static OMXMLParserWrapper createStAXOMBuilder(OMFactory omFactory, XMLStreamReader parser) {
        return omFactory.getMetaFactory().createStAXOMBuilder(omFactory, parser);
    }
    
    /**
     * Create an object model builder that reads a plain XML document from the provided input stream
     * with the default parser configuration defined by {@link StAXParserConfiguration#DEFAULT}.
     * 
     * @param in
     *            the input stream representing the XML document
     * @return the builder
     */
    public static OMXMLParserWrapper createOMBuilder(InputStream in) {
        return createOMBuilder(StAXParserConfiguration.DEFAULT, in);
    }
    
    /**
     * Create an object model builder that reads a plain XML document from the provided input stream
     * with the default parser configuration defined by {@link StAXParserConfiguration#DEFAULT}.
     * 
     * @param in
     *            the input stream representing the XML document
     * @param encoding
     *            the charset encoding of the XML document or <code>null</code> if the parser should
     *            determine the charset encoding
     * @return the builder
     */
    public static OMXMLParserWrapper createOMBuilder(InputStream in, String encoding) {
        return createOMBuilder(StAXParserConfiguration.DEFAULT, in, encoding);
    }
    
    /**
     * Create an object model builder that reads a plain XML document from the provided input stream
     * with a given parser configuration.
     * 
     * @param configuration
     *            the parser configuration to use
     * @param in
     *            the input stream representing the XML document
     * @return the builder
     */
    public static OMXMLParserWrapper createOMBuilder(StAXParserConfiguration configuration, InputStream in) {
        return createOMBuilder(configuration, in, null);
    }
    
    /**
     * Create an object model builder that reads a plain XML document from the provided input stream
     * with a given parser configuration.
     * 
     * @param configuration
     *            the parser configuration to use
     * @param in
     *            the input stream representing the XML document
     * @param encoding
     *            the charset encoding of the XML document or <code>null</code> if the parser should
     *            determine the charset encoding
     * @return the builder
     */
    public static OMXMLParserWrapper createOMBuilder(StAXParserConfiguration configuration, InputStream in, String encoding) {
        OMMetaFactory metaFactory = OMAbstractFactory.getMetaFactory();
        InputSource is = new InputSource(in);
        is.setEncoding(encoding);
        return metaFactory.createOMBuilder(metaFactory.getOMFactory(), configuration, is);
    }
    
    /**
     * Create an object model builder that reads an XML document from the provided input stream
     * using a specified object model factory and with the default parser configuration defined by
     * {@link StAXParserConfiguration#DEFAULT}.
     * 
     * @param omFactory
     *            the object model factory to use
     * @param in
     *            the input stream representing the XML document
     * @return the builder
     */
    public static OMXMLParserWrapper createOMBuilder(OMFactory omFactory, InputStream in) {
        return createOMBuilder(omFactory, StAXParserConfiguration.DEFAULT, in);
    }
    
    /**
     * Create an object model builder that reads an XML document from the provided input stream
     * using a specified object model factory and with the default parser configuration defined by
     * {@link StAXParserConfiguration#DEFAULT}.
     * 
     * @param omFactory
     *            the object model factory to use
     * @param in
     *            the input stream representing the XML document
     * @param encoding
     *            the charset encoding of the XML document or <code>null</code> if the parser should
     *            determine the charset encoding
     * @return the builder
     */
    public static OMXMLParserWrapper createOMBuilder(OMFactory omFactory, InputStream in, String encoding) {
        return createOMBuilder(omFactory, StAXParserConfiguration.DEFAULT, in, encoding);
    }
    
    /**
     * Create an object model builder that reads an XML document from the provided input stream
     * using a specified object model factory and with a given parser configuration.
     * 
     * @param omFactory
     *            the object model factory to use
     * @param configuration
     *            the parser configuration to use
     * @param in
     *            the input stream representing the XML document
     * @return the builder
     */
    public static OMXMLParserWrapper createOMBuilder(OMFactory omFactory, StAXParserConfiguration configuration, InputStream in) {
        return createOMBuilder(omFactory, configuration, in, null);
    }
    
    /**
     * Create an object model builder that reads an XML document from the provided input stream
     * using a specified object model factory and with a given parser configuration.
     * 
     * @param omFactory
     *            the object model factory to use
     * @param configuration
     *            the parser configuration to use
     * @param in
     *            the input stream representing the XML document
     * @param encoding
     *            the charset encoding of the XML document or <code>null</code> if the parser should
     *            determine the charset encoding
     * @return the builder
     */
    public static OMXMLParserWrapper createOMBuilder(OMFactory omFactory, StAXParserConfiguration configuration, InputStream in, String encoding) {
        InputSource is = new InputSource(in);
        is.setEncoding(encoding);
        return omFactory.getMetaFactory().createOMBuilder(omFactory, configuration, is);
    }
    
    /**
     * Create an object model builder that reads a plain XML document from the provided character
     * stream with the default parser configuration defined by
     * {@link StAXParserConfiguration#DEFAULT}.
     * 
     * @param in
     *            the character stream representing the XML document
     * @return the builder
     */
    public static OMXMLParserWrapper createOMBuilder(Reader in) {
        return createOMBuilder(StAXParserConfiguration.DEFAULT, in);
    }
    
    /**
     * Create an object model builder that reads a plain XML document from the provided character
     * stream with a given parser configuration.
     * 
     * @param configuration
     *            the parser configuration to use
     * @param in
     *            the character stream representing the XML document
     * @return the builder
     */
    public static OMXMLParserWrapper createOMBuilder(StAXParserConfiguration configuration, Reader in) {
        OMMetaFactory metaFactory = OMAbstractFactory.getMetaFactory();
        return metaFactory.createOMBuilder(metaFactory.getOMFactory(), configuration, new InputSource(in));
    }
    
    /**
     * Create an object model builder that reads an XML document from the provided character stream
     * using a specified object model factory and with the default parser configuration defined by
     * {@link StAXParserConfiguration#DEFAULT}.
     * 
     * @param omFactory
     *            the object model factory to use
     * @param in
     *            the character stream representing the XML document
     * @return the builder
     */
    public static OMXMLParserWrapper createOMBuilder(OMFactory omFactory, Reader in) {
        return createOMBuilder(omFactory, StAXParserConfiguration.DEFAULT, in);
    }
    
    /**
     * Create an object model builder that reads an XML document from the provided character stream
     * using a specified object model factory and with a given parser configuration.
     * 
     * @param omFactory
     *            the object model factory to use
     * @param configuration
     *            the parser configuration to use
     * @param in
     *            the character stream representing the XML document
     * @return the builder
     */
    public static OMXMLParserWrapper createOMBuilder(OMFactory omFactory, StAXParserConfiguration configuration, Reader in) {
        return omFactory.getMetaFactory().createOMBuilder(omFactory, configuration, new InputSource(in));
    }
    
    /**
     * Create an object model builder that reads a plain XML document from the provided
     * {@link Source}.
     * 
     * @param source
     *            the source of the XML document
     * @return the builder
     */
    public static OMXMLParserWrapper createOMBuilder(Source source) {
        OMMetaFactory metaFactory = OMAbstractFactory.getMetaFactory();
        return metaFactory.createOMBuilder(metaFactory.getOMFactory(), source);
    }
    
    /**
     * Create an object model builder that reads a plain XML document from the provided DOM tree.
     * 
     * @param node
     *            the DOM node; must be a {@link Node#DOCUMENT_NODE} or {@link Node#ELEMENT_NODE}
     * @param expandEntityReferences
     *            Determines how {@link EntityReference} nodes are handled:
     *            <ul>
     *            <li>If the parameter is <code>false</code> then a single {@link OMEntityReference}
     *            will be created for each {@link EntityReference}. The child nodes of
     *            {@link EntityReference} nodes are not taken into account.
     *            <li>If the parameter is <code>true</code> then no {@link OMEntityReference} nodes
     *            are created and the children of {@link EntityReference} nodes are converted and
     *            inserted into the Axiom tree.
     *            </ul>
     * @return the builder
     */
    public static OMXMLParserWrapper createOMBuilder(Node node, boolean expandEntityReferences) {
        OMMetaFactory metaFactory = OMAbstractFactory.getMetaFactory();
        return metaFactory.createOMBuilder(metaFactory.getOMFactory(), node, expandEntityReferences);
    }
    
    /**
     * Create an object model builder that reads a plain XML document from the provided
     * {@link SAXSource}.
     * 
     * @param source
     *            the source of the XML document
     * @param expandEntityReferences
     *            Determines how entity references (i.e. {@link LexicalHandler#startEntity(String)}
     *            and {@link LexicalHandler#endEntity(String)} events) are handled:
     *            <ul>
     *            <li>If the parameter is <code>false</code> then a single {@link OMEntityReference}
     *            will be created for each pair of {@link LexicalHandler#startEntity(String)} and
     *            {@link LexicalHandler#endEntity(String)} events. Other events reported between
     *            these two events are not taken into account.
     *            <li>If the parameter is <code>true</code> then no {@link OMEntityReference} nodes
     *            are created and {@link LexicalHandler#startEntity(String)} and
     *            {@link LexicalHandler#endEntity(String)} events are ignored. However, events
     *            between {@link LexicalHandler#startEntity(String)} and
     *            {@link LexicalHandler#endEntity(String)} are processed normally.
     *            </ul>
     * @return the builder
     */
    public static OMXMLParserWrapper createOMBuilder(SAXSource source, boolean expandEntityReferences) {
        OMMetaFactory metaFactory = OMAbstractFactory.getMetaFactory();
        return metaFactory.createOMBuilder(metaFactory.getOMFactory(), source, expandEntityReferences);
    }
    
    /**
     * Create an object model builder that reads an XML document from the provided {@link Source}
     * using a specified object model factory.
     * 
     * @param omFactory
     *            the object model factory to use
     * @param source
     *            the source of the XML document
     * @return the builder
     */
    // TODO: update Javadoc to explain relationship with the methods that take a DOMSource or SAXSource
    public static OMXMLParserWrapper createOMBuilder(OMFactory omFactory, Source source) {
        return omFactory.getMetaFactory().createOMBuilder(omFactory, source);
    }
    
    /**
     * Create an object model builder that reads an XML document from the provided DOM tree using a
     * specified object model factory.
     * 
     * @param omFactory
     *            the object model factory to use
     * @param node
     *            the DOM node; must be a {@link Node#DOCUMENT_NODE} or {@link Node#ELEMENT_NODE}
     * @param expandEntityReferences
     *            Determines how {@link EntityReference} nodes are handled:
     *            <ul>
     *            <li>If the parameter is <code>false</code> then a single {@link OMEntityReference}
     *            will be created for each {@link EntityReference}. The child nodes of
     *            {@link EntityReference} nodes are not taken into account.
     *            <li>If the parameter is <code>true</code> then no {@link OMEntityReference} nodes
     *            are created and the children of {@link EntityReference} nodes are converted and
     *            inserted into the Axiom tree.
     *            </ul>
     * @return the builder
     */
    public static OMXMLParserWrapper createOMBuilder(OMFactory omFactory, Node node, boolean expandEntityReferences) {
        return omFactory.getMetaFactory().createOMBuilder(omFactory, node, expandEntityReferences);
    }
    
    /**
     * Create an object model builder that reads an XML document from the provided {@link SAXSource}
     * using a specified object model factory.
     * 
     * @param omFactory
     *            the object model factory to use
     * @param source
     *            the source of the XML document
     * @param expandEntityReferences
     *            Determines how entity references (i.e. {@link LexicalHandler#startEntity(String)}
     *            and {@link LexicalHandler#endEntity(String)} events) are handled:
     *            <ul>
     *            <li>If the parameter is <code>false</code> then a single {@link OMEntityReference}
     *            will be created for each pair of {@link LexicalHandler#startEntity(String)} and
     *            {@link LexicalHandler#endEntity(String)} events. Other events reported between
     *            these two events are not taken into account.
     *            <li>If the parameter is <code>true</code> then no {@link OMEntityReference} nodes
     *            are created and {@link LexicalHandler#startEntity(String)} and
     *            {@link LexicalHandler#endEntity(String)} events are ignored. However, events
     *            between {@link LexicalHandler#startEntity(String)} and
     *            {@link LexicalHandler#endEntity(String)} are processed normally.
     *            </ul>
     * @return the builder
     */
    public static OMXMLParserWrapper createOMBuilder(OMFactory omFactory, SAXSource source, boolean expandEntityReferences) {
        return omFactory.getMetaFactory().createOMBuilder(omFactory, source, expandEntityReferences);
    }
    
    /**
     * Create an XOP aware model builder from the provided {@link Attachments} object and with a
     * given parser configuration.
     * 
     * @param configuration
     *            the parser configuration to use
     * @param attachments
     *            an {@link Attachments} object that must have been created from an input stream
     * @return the builder
     * @throws OMException
     *             if an error occurs while processing the content type information from the
     *             {@link Attachments} object
     */
    public static OMXMLParserWrapper createOMBuilder(StAXParserConfiguration configuration, Attachments attachments) {
        return createOMBuilder(OMAbstractFactory.getMetaFactory().getOMFactory(), configuration, attachments);
    }
    
    /**
     * Create an XOP aware model builder from the provided {@link Attachments} object using a
     * specified object model factory and with a given parser configuration.
     * 
     * @param omFactory
     *            the object model factory to use
     * @param configuration
     *            the parser configuration to use
     * @param attachments
     *            an {@link Attachments} object that must have been created from an input stream
     * @return the builder
     * @throws OMException
     *             if an error occurs while processing the content type information from the
     *             {@link Attachments} object
     */
    public static OMXMLParserWrapper createOMBuilder(OMFactory omFactory,
            StAXParserConfiguration configuration, Attachments attachments) {
        ContentType contentType;
        try {
            contentType = new ContentType(attachments.getRootPartContentType());
        } catch (ParseException ex) {
            throw new OMException(ex);
        }
        InputSource rootPart = getRootPartInputSource(attachments, contentType);
        return omFactory.getMetaFactory().createOMBuilder(configuration, omFactory,
                rootPart, new AttachmentsMimePartProvider(attachments));
    }
    
    /**
     * Create an object model builder for SOAP that pulls events from a StAX stream reader and that
     * uses a particular Axiom implementation. The method will select the appropriate
     * {@link SOAPFactory} based on the namespace URI of the SOAP envelope.
     * <p>
     * See {@link #createStAXOMBuilder(XMLStreamReader)} for more information about the behavior of
     * the returned builder.
     * 
     * @param metaFactory
     *            the meta factory for the Axiom implementation to use
     * @param parser
     *            the stream reader to read the XML data from
     * @return the builder
     */
    public static SOAPModelBuilder createStAXSOAPModelBuilder(OMMetaFactory metaFactory, XMLStreamReader parser) {
        return metaFactory.createStAXSOAPModelBuilder(parser);
    }
    
    /**
     * Create an object model builder for SOAP that pulls events from a StAX stream reader.
     * The method will select the appropriate {@link SOAPFactory}
     * based on the namespace URI of the SOAP envelope.
     * <p>
     * See {@link #createStAXOMBuilder(XMLStreamReader)} for more information about the behavior of
     * the returned builder.
     * 
     * @param parser
     *            the stream reader to read the XML data from
     * @return the builder
     */
    public static SOAPModelBuilder createStAXSOAPModelBuilder(XMLStreamReader parser) {
        return OMAbstractFactory.getMetaFactory().createStAXSOAPModelBuilder(parser);
    }
    
    /**
     * Create an object model builder for SOAP that reads a message from the provided input stream,
     * using a given charset encoding. The method will select the appropriate {@link SOAPFactory}
     * based on the namespace URI of the SOAP envelope. It will configure the underlying parser as
     * specified by {@link StAXParserConfiguration#SOAP}.
     * 
     * @param in
     *            the input stream containing the SOAP message
     * @param encoding
     *            the charset encoding of the SOAP message or <code>null</code> if the parser should
     *            determine the charset encoding
     * @return the builder
     */
    public static SOAPModelBuilder createSOAPModelBuilder(InputStream in, String encoding) {
        return createSOAPModelBuilder(OMAbstractFactory.getMetaFactory(), in, encoding);
    }
    
    /**
     * Create an object model builder for SOAP that reads a message from the provided input stream,
     * using a particular Axiom implementation and a given charset encoding. The method will select
     * the appropriate {@link SOAPFactory} based on the namespace URI of the SOAP envelope. It will
     * configure the underlying parser as specified by {@link StAXParserConfiguration#SOAP}.
     * 
     * @param metaFactory
     *            the meta factory for the Axiom implementation to use
     * @param in
     *            the input stream containing the SOAP message
     * @param encoding
     *            the charset encoding of the SOAP message or <code>null</code> if the parser should
     *            determine the charset encoding
     * @return the builder
     */
    public static SOAPModelBuilder createSOAPModelBuilder(OMMetaFactory metaFactory, InputStream in, String encoding) {
        InputSource is = new InputSource(in);
        is.setEncoding(encoding);
        return metaFactory.createSOAPModelBuilder(StAXParserConfiguration.SOAP, is);
    }
    
    /**
     * Create an object model builder for SOAP that reads a message from the provided character
     * stream. The method will select the appropriate {@link SOAPFactory} based on the namespace URI
     * of the SOAP envelope. It will configure the underlying parser as specified by
     * {@link StAXParserConfiguration#SOAP}.
     * 
     * @param in
     *            the character stream containing the SOAP message
     * @return the builder
     */
    public static SOAPModelBuilder createSOAPModelBuilder(Reader in) {
        return createSOAPModelBuilder(OMAbstractFactory.getMetaFactory(), in);
    }
    
    /**
     * Create an object model builder for SOAP that reads a message from the provided character
     * stream using a particular Axiom implementation. The method will select the appropriate
     * {@link SOAPFactory} based on the namespace URI of the SOAP envelope. It will configure the
     * underlying parser as specified by {@link StAXParserConfiguration#SOAP}.
     * 
     * @param metaFactory
     *            the meta factory for the Axiom implementation to use
     * @param in
     *            the character stream containing the SOAP message
     * @return the builder
     */
    public static SOAPModelBuilder createSOAPModelBuilder(OMMetaFactory metaFactory, Reader in) {
        return metaFactory.createSOAPModelBuilder(StAXParserConfiguration.SOAP, new InputSource(in));
    }
    
    /**
     * Create an MTOM aware model builder from the provided {@link Attachments} object. The method
     * will determine the SOAP version based on the content type information from the
     * {@link Attachments} object. It will configure the underlying parser as specified by
     * {@link StAXParserConfiguration#SOAP}.
     * 
     * @param attachments
     *            an {@link Attachments} object that must have been created from an input stream
     * @return the builder
     * @throws OMException
     *             if an error occurs while processing the content type information from the
     *             {@link Attachments} object
     */
    public static SOAPModelBuilder createSOAPModelBuilder(Attachments attachments) {
        return createSOAPModelBuilder(OMAbstractFactory.getMetaFactory(), attachments);
    }
    
    /**
     * Create an MTOM aware model builder from the provided {@link Attachments} object using a
     * particular Axiom implementation. The method will determine the SOAP version based on the
     * content type information from the {@link Attachments} object. It will configure the
     * underlying parser as specified by {@link StAXParserConfiguration#SOAP}.
     * 
     * @param metaFactory
     *            the meta factory for the Axiom implementation to use
     * @param attachments
     *            an {@link Attachments} object that must have been created from an input stream
     * @return the builder
     * @throws OMException
     *             if an error occurs while processing the content type information from the
     *             {@link Attachments} object
     */
    public static SOAPModelBuilder createSOAPModelBuilder(OMMetaFactory metaFactory,
            Attachments attachments) {
        ContentType contentType;
        try {
            contentType = new ContentType(attachments.getRootPartContentType());
        } catch (ParseException ex) {
            throw new OMException(ex);
        }
        String type = contentType.getParameter("type");
        SOAPFactory soapFactory;
        if ("text/xml".equalsIgnoreCase(type)) {
            soapFactory = metaFactory.getSOAP11Factory();
        } else if ("application/soap+xml".equalsIgnoreCase(type)) {
            soapFactory = metaFactory.getSOAP12Factory();
        } else {
            throw new OMException("Unable to determine SOAP version");
        }
        InputSource rootPart = getRootPartInputSource(attachments, contentType);
        return metaFactory.createSOAPModelBuilder(StAXParserConfiguration.SOAP, soapFactory,
                rootPart, new AttachmentsMimePartProvider(attachments));
    }
    
    private static InputSource getRootPartInputSource(Attachments attachments, ContentType contentType) {
        InputSource rootPart = new InputSource(attachments.getRootPartInputStream(false));
        rootPart.setEncoding(contentType.getParameter("charset"));
        return rootPart;
    }
}
