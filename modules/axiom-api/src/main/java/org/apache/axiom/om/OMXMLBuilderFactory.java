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

import javax.mail.internet.ContentType;
import javax.mail.internet.ParseException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;

import org.apache.axiom.attachments.Attachments;
import org.apache.axiom.om.impl.builder.OMAttachmentAccessorMimePartProvider;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.xml.sax.InputSource;

/**
 * Provides static factory methods to create various kinds of object model builders from different
 * types of input sources. The methods defined by this class are the starting point to parse XML
 * documents into Axiom trees.
 * <p>
 * <b>WARNING: This API is still under construction (see <a
 * href="https://issues.apache.org/jira/browse/AXIOM-353">AXIOM-353</a>) and may slightly change in
 * subsequent releases!</b>
 */
public class OMXMLBuilderFactory {
    private OMXMLBuilderFactory() {}
    
    /**
     * Create an object model builder for plain XML that pulls events from a StAX stream reader.
     * 
     * @param parser
     *            the stream reader to read the XML data from
     * @return the builder
     */
    public static OMXMLParserWrapper createStAXOMBuilder(XMLStreamReader parser) {
        OMMetaFactory metaFactory = OMAbstractFactory.getMetaFactory();
        return metaFactory.createStAXOMBuilder(metaFactory.getOMFactory(), parser);
    }
    
    /**
     * Create an object model builder that pulls events from a StAX stream reader using a specified
     * object model factory.
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
     * Create an object model builder that reads an XML document from the provided {@link Source}
     * using a specified object model factory.
     * 
     * @param omFactory
     *            the object model factory to use
     * @param source
     *            the source of the XML document
     * @return the builder
     */
    public static OMXMLParserWrapper createOMBuilder(OMFactory omFactory, Source source) {
        return omFactory.getMetaFactory().createOMBuilder(omFactory, source);
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
        InputSource rootPart = new InputSource(attachments.getRootPartInputStream());
        rootPart.setEncoding(contentType.getParameter("charset"));
        return omFactory.getMetaFactory().createOMBuilder(configuration, omFactory,
                rootPart, new OMAttachmentAccessorMimePartProvider(attachments));
    }
    
    /**
     * Create an object model builder for SOAP that pulls events from a StAX stream reader.
     * The method will select the appropriate {@link SOAPFactory}
     * based on the namespace URI of the SOAP envelope.
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
        InputSource rootPart = new InputSource(attachments.getRootPartInputStream());
        rootPart.setEncoding(contentType.getParameter("charset"));
        return metaFactory.createSOAPModelBuilder(StAXParserConfiguration.SOAP, soapFactory,
                rootPart, new OMAttachmentAccessorMimePartProvider(attachments));
    }
}
