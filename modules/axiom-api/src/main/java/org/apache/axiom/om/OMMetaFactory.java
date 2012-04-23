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

import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.util.stax.xop.MimePartProvider;
import org.xml.sax.InputSource;

/**
 * Object model meta factory. This interface encapsulates a particular Axiom implementation and
 * provides instances for plain XML, SOAP 1.1 and SOAP 1.2 object model factories for that
 * implementation. Currently the two OM implementations provided by Axiom are LLOM (linked list) and
 * DOOM (DOM compatible).
 * <p>
 * The factories returned by {@link #getOMFactory()}, {@link #getSOAP11Factory()} and
 * {@link #getSOAP12Factory()} MUST be stateless (and thread safe). The implementation MUST return
 * the same instance on every invocation, i.e. instantiate the factory for each OM type only once.
 */
public interface OMMetaFactory {
    /**
     * Get the OM factory instance for the XML infoset model.
     *
     * @return the OM factory instance
     */
    OMFactory getOMFactory();
    
    /**
     * Get the OM factory instance for the SOAP 1.1 infoset model.
     *
     * @return the OM factory instance
     */
    SOAPFactory getSOAP11Factory();
    
    /**
     * Get the OM factory instance for the SOAP 1.2 infoset model.
     *
     * @return the OM factory instance
     */
    SOAPFactory getSOAP12Factory();
    
    /**
     * Create an object model builder for plain XML that pulls events from a StAX stream reader.
     * <p>
     * The implementation must perform namespace repairing, i.e. it must add appropriate namespace
     * declarations if undeclared namespaces appear in the StAX stream.
     * 
     * @param omFactory
     *            The object model factory to use. This factory must be obtained from the same
     *            {@link OMMetaFactory} instance as the one used to invoke this method. In general
     *            the factory will be retrieved from {@link #getOMFactory()}), but in some cases it
     *            may be necessary to pass a {@link SOAPFactory} instance, although this method will
     *            never produce a SOAP infoset.
     * @param parser
     *            the stream reader to read the XML data from
     * @return the builder
     */
    OMXMLParserWrapper createStAXOMBuilder(OMFactory omFactory, XMLStreamReader parser);
    
    /**
     * Create an object model builder for plain XML that reads a document from the provided input
     * source.
     * 
     * @param omFactory
     *            The object model factory to use. This factory must be obtained from the same
     *            {@link OMMetaFactory} instance as the one used to invoke this method. In general
     *            the factory will be retrieved from {@link #getOMFactory()}), but in some cases it
     *            may be necessary to pass a {@link SOAPFactory} instance, although this method will
     *            never produce a SOAP infoset.
     * @param configuration
     *            the parser configuration to use
     * @param is
     *            the source of the XML document
     * @return the builder
     */
    OMXMLParserWrapper createOMBuilder(OMFactory omFactory, StAXParserConfiguration configuration, InputSource is);
    
    /**
     * Create an object model builder for plain XML that gets its input from a {@link Source}.
     * 
     * @param omFactory
     *            The object model factory to use. This factory must be obtained from the same
     *            {@link OMMetaFactory} instance as the one used to invoke this method. In general
     *            the factory will be retrieved from {@link #getOMFactory()}), but in some cases it
     *            may be necessary to pass a {@link SOAPFactory} instance, although this method will
     *            never produce a SOAP infoset.
     * @param source
     *            the source of the XML document
     * @return the builder
     */
    OMXMLParserWrapper createOMBuilder(OMFactory omFactory, Source source);
    
    /**
     * Create an XOP aware object model builder.
     * 
     * @param configuration
     *            the parser configuration to use
     * @param omFactory
     *            The object model factory to use. This factory must be obtained from the same
     *            {@link OMMetaFactory} instance as the one used to invoke this method.
     * @param rootPart
     *            the source of the root part of the XOP message
     * @param mimePartProvider
     *            the provider from which MIME parts referenced in the root part will be retrieved
     * @return the builder
     */
    OMXMLParserWrapper createOMBuilder(StAXParserConfiguration configuration,
            OMFactory omFactory, InputSource rootPart, MimePartProvider mimePartProvider);
    
    /**
     * Create an object model builder for SOAP that pulls events from a StAX stream reader. The
     * implementation will select the appropriate {@link SOAPFactory} based on the namespace URI of
     * the SOAP envelope.
     * 
     * @param parser
     *            the stream reader to read the SOAP message from
     * @return the builder
     */
    SOAPModelBuilder createStAXSOAPModelBuilder(XMLStreamReader parser);
    
    /**
     * Create an object model builder for SOAP that reads a message from the provided input source.
     * The implementation will select the appropriate {@link SOAPFactory} based on the namespace URI
     * of the SOAP envelope.
     * 
     * @param configuration
     *            the parser configuration to use; for security reasons, this should in general be
     *            {@link StAXParserConfiguration#SOAP}
     * @param is
     *            the source of the SOAP message
     * @return the builder
     */
    SOAPModelBuilder createSOAPModelBuilder(StAXParserConfiguration configuration, InputSource is);
    
    /**
     * Create an MTOM aware object model builder.
     * 
     * @param configuration
     *            the parser configuration to use; for security reasons, this should in general be
     *            {@link StAXParserConfiguration#SOAP}
     * @param soapFactory
     *            the {@link SOAPFactory} to use, or <code>null</code> if the implementation should
     *            select the appropriate {@link SOAPFactory} based on the namespace URI of the SOAP
     *            envelope.
     * @param rootPart
     *            the source of the root part of the MTOM message
     * @param mimePartProvider
     *            the provider from which MIME parts referenced in the root part will be retrieved
     * @return the builder
     */
    SOAPModelBuilder createSOAPModelBuilder(StAXParserConfiguration configuration,
            SOAPFactory soapFactory, InputSource rootPart, MimePartProvider mimePartProvider);
}
