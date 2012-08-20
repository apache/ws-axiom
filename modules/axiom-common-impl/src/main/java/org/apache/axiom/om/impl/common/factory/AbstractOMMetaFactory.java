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
package org.apache.axiom.om.impl.common.factory;

import java.io.IOException;
import java.net.URL;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;

import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.builder.SAXOMBuilder;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.builder.XOPAwareStAXOMBuilder;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.soap.impl.builder.MTOMStAXSOAPModelBuilder;
import org.apache.axiom.soap.impl.builder.OMMetaFactoryEx;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.apache.axiom.util.stax.XMLEventUtils;
import org.apache.axiom.util.stax.XMLFragmentStreamReader;
import org.apache.axiom.util.stax.xop.MimePartProvider;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Base class for {@link OMMetaFactory} implementations that make use of the standard builders
 * ({@link org.apache.axiom.om.impl.builder.StAXOMBuilder} and its subclasses).
 */
public abstract class AbstractOMMetaFactory implements OMMetaFactoryEx {
    private static XMLStreamReader createXMLStreamReader(StAXParserConfiguration configuration, InputSource is) {
        try {
            if (is.getByteStream() != null) {
                String encoding = is.getEncoding();
                if (encoding == null) {
                    return StAXUtils.createXMLStreamReader(configuration, is.getByteStream());
                } else {
                    return StAXUtils.createXMLStreamReader(configuration, is.getByteStream(), encoding);
                }
            } else if (is.getCharacterStream() != null) {
                return StAXUtils.createXMLStreamReader(configuration, is.getCharacterStream());
            } else {
                String systemId = is.getSystemId();
                return StAXUtils.createXMLStreamReader(configuration, systemId, new URL(systemId).openConnection().getInputStream());
            }
        } catch (XMLStreamException ex) {
            throw new OMException(ex);
        } catch (IOException ex) {
            throw new OMException(ex);
        }
    }
    
    private static XMLStreamReader getXMLStreamReader(XMLStreamReader originalReader) {
        int eventType = originalReader.getEventType();
        switch (eventType) {
            case XMLStreamReader.START_DOCUMENT:
                return originalReader;
            case XMLStreamReader.START_ELEMENT:
                return new XMLFragmentStreamReader(originalReader);
            default:
                throw new OMException("The supplied XMLStreamReader is in an unexpected state ("
                        + XMLEventUtils.getEventTypeString(eventType) + ")");
        }
    }
    
    private static OMXMLParserWrapper internalCreateStAXOMBuilder(OMFactory omFactory, XMLStreamReader parser) {
        StAXOMBuilder builder = new StAXOMBuilder(omFactory, parser);
        // StAXOMBuilder defaults to the "legacy" behavior, which is to keep a reference to the
        // parser after the builder has been closed. Since releasing this reference is a good idea
        // we default to releaseParserOnClose=true for builders created through the OMMetaFactory
        // API.
        builder.releaseParserOnClose(true);
        return builder;
    }

    public OMXMLParserWrapper createStAXOMBuilder(OMFactory omFactory, XMLStreamReader parser) {
        return internalCreateStAXOMBuilder(omFactory, getXMLStreamReader(parser));
    }

    public OMXMLParserWrapper createOMBuilder(OMFactory omFactory, StAXParserConfiguration configuration, InputSource is) {
        return internalCreateStAXOMBuilder(omFactory, createXMLStreamReader(configuration, is));
    }
    
    public OMXMLParserWrapper createOMBuilder(OMFactory omFactory, Source source) {
        if (source instanceof SAXSource) {
            return createOMBuilder(omFactory, (SAXSource)source, true);
        } else if (source instanceof DOMSource) {
            return createOMBuilder(omFactory, ((DOMSource)source).getNode(), true);
        } else {
            try {
                return new StAXOMBuilder(omFactory, StAXUtils.getXMLInputFactory().createXMLStreamReader(source));
            } catch (XMLStreamException ex) {
                throw new OMException(ex);
            }
        }
    }

    public OMXMLParserWrapper createOMBuilder(OMFactory omFactory, Node node,
            boolean expandEntityReferences) {
        return new StAXOMBuilder(omFactory, new DOMXMLStreamReader(node, expandEntityReferences));
    }

    public OMXMLParserWrapper createOMBuilder(OMFactory omFactory, SAXSource source,
            boolean expandEntityReferences) {
        return new SAXOMBuilder(omFactory, source, expandEntityReferences);
    }

    public OMXMLParserWrapper createOMBuilder(StAXParserConfiguration configuration,
            OMFactory omFactory, InputSource rootPart, MimePartProvider mimePartProvider) {
        XOPAwareStAXOMBuilder builder = new XOPAwareStAXOMBuilder(omFactory, createXMLStreamReader(
                configuration, rootPart), mimePartProvider);
        builder.releaseParserOnClose(true);
        return builder;
    }

    private SOAPModelBuilder internalCreateStAXSOAPModelBuilder(XMLStreamReader parser) {
        StAXSOAPModelBuilder builder = new StAXSOAPModelBuilder(this, parser);
        builder.releaseParserOnClose(true);
        return builder;
    }

    public SOAPModelBuilder createStAXSOAPModelBuilder(XMLStreamReader parser) {
        return internalCreateStAXSOAPModelBuilder(getXMLStreamReader(parser));
    }

    public SOAPModelBuilder createSOAPModelBuilder(StAXParserConfiguration configuration, InputSource is) {
        return internalCreateStAXSOAPModelBuilder(createXMLStreamReader(configuration, is));
    }

    public SOAPModelBuilder createSOAPModelBuilder(StAXParserConfiguration configuration,
            SOAPFactory soapFactory, InputSource rootPart, MimePartProvider mimePartProvider) {
        MTOMStAXSOAPModelBuilder builder = new MTOMStAXSOAPModelBuilder(soapFactory, createXMLStreamReader(
                configuration, rootPart), mimePartProvider);
        builder.releaseParserOnClose(true);
        return builder;
    }
}
