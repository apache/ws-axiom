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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.builder.Detachable;
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
    private final static class SourceInfo {
        private final XMLStreamReader reader;
        private final Detachable detachable;
        private final Closeable closeable;
        
        SourceInfo(XMLStreamReader reader, Detachable detachable, Closeable closeable) {
            this.reader = reader;
            this.detachable = detachable;
            this.closeable = closeable;
        }

        XMLStreamReader getReader() {
            return reader;
        }

        Detachable getDetachable() {
            return detachable;
        }

        Closeable getCloseable() {
            return closeable;
        }
    }
    
    private static SourceInfo createXMLStreamReader(StAXParserConfiguration configuration,
            InputSource is, boolean makeDetachable) {
        XMLStreamReader reader;
        Detachable detachable;
        Closeable closeable;
        try {
            if (is.getByteStream() != null) {
                String systemId = is.getSystemId();
                String encoding = is.getEncoding();
                InputStream in = is.getByteStream();
                if (makeDetachable) {
                    DetachableInputStream detachableInputStream = new DetachableInputStream(in, false);
                    in = detachableInputStream;
                    detachable = detachableInputStream;
                } else {
                    detachable = null;
                }
                if (systemId != null) {
                    if (encoding == null) {
                        reader = StAXUtils.createXMLStreamReader(configuration, systemId, in);
                    } else {
                        throw new UnsupportedOperationException();
                    }
                } else {
                    if (encoding == null) {
                        reader = StAXUtils.createXMLStreamReader(configuration, in);
                    } else {
                        reader = StAXUtils.createXMLStreamReader(configuration, in, encoding);
                    }
                }
                closeable = null;
            } else if (is.getCharacterStream() != null) {
                Reader in = is.getCharacterStream();
                if (makeDetachable) {
                    DetachableReader detachableReader = new DetachableReader(in);
                    in = detachableReader;
                    detachable = detachableReader;
                } else {
                    detachable = null;
                }
                reader = StAXUtils.createXMLStreamReader(configuration, in);
                closeable = null;
            } else {
                String systemId = is.getSystemId();
                InputStream in = new URL(systemId).openConnection().getInputStream();
                if (makeDetachable) {
                    DetachableInputStream detachableInputStream = new DetachableInputStream(in, true);
                    in = detachableInputStream;
                    detachable = detachableInputStream;
                } else {
                    detachable = null;
                }
                reader = StAXUtils.createXMLStreamReader(configuration, systemId, in);
                closeable = in;
            }
        } catch (XMLStreamException ex) {
            throw new OMException(ex);
        } catch (IOException ex) {
            throw new OMException(ex);
        }
        return new SourceInfo(reader, detachable, closeable);
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
    
    public OMXMLParserWrapper createStAXOMBuilder(OMFactory omFactory, XMLStreamReader parser) {
        return new StAXOMBuilder(omFactory, getXMLStreamReader(parser));
    }

    public OMXMLParserWrapper createOMBuilder(OMFactory omFactory, StAXParserConfiguration configuration, InputSource is) {
        SourceInfo sourceInfo = createXMLStreamReader(configuration, is, true);
        StAXOMBuilder builder = new StAXOMBuilder(omFactory, sourceInfo.getReader(),
                sourceInfo.getDetachable(), sourceInfo.getCloseable());
        builder.setAutoClose(true);
        return builder;
    }
    
    private static InputSource toInputSource(StreamSource source) {
        InputSource is = new InputSource();
        is.setByteStream(source.getInputStream());
        is.setCharacterStream(source.getReader());
        is.setPublicId(source.getPublicId());
        is.setSystemId(source.getSystemId());
        return is;
    }
    
    public OMXMLParserWrapper createOMBuilder(OMFactory omFactory, Source source) {
        if (source instanceof SAXSource) {
            return createOMBuilder(omFactory, (SAXSource)source, true);
        } else if (source instanceof DOMSource) {
            return createOMBuilder(omFactory, ((DOMSource)source).getNode(), true);
        } else if (source instanceof StreamSource) {
            return createOMBuilder(omFactory, StAXParserConfiguration.DEFAULT,
                    toInputSource((StreamSource)source));
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
        SourceInfo sourceInfo = createXMLStreamReader(configuration, rootPart, false);
        XOPAwareStAXOMBuilder builder = new XOPAwareStAXOMBuilder(
                omFactory,
                sourceInfo.getReader(),
                mimePartProvider,
                mimePartProvider instanceof Detachable ? (Detachable)mimePartProvider : null,
                sourceInfo.getCloseable());
        builder.setAutoClose(true);
        return builder;
    }

    public SOAPModelBuilder createStAXSOAPModelBuilder(XMLStreamReader parser) {
        return new StAXSOAPModelBuilder(this, getXMLStreamReader(parser), null, null);
    }

    public SOAPModelBuilder createSOAPModelBuilder(StAXParserConfiguration configuration, InputSource is) {
        SourceInfo sourceInfo = createXMLStreamReader(configuration, is, true);
        StAXSOAPModelBuilder builder = new StAXSOAPModelBuilder(this, sourceInfo.getReader(),
                sourceInfo.getDetachable(), sourceInfo.getCloseable());
        builder.setAutoClose(true);
        return builder;
    }

    public SOAPModelBuilder createSOAPModelBuilder(StAXParserConfiguration configuration,
            SOAPFactory soapFactory, InputSource rootPart, MimePartProvider mimePartProvider) {
        SourceInfo sourceInfo = createXMLStreamReader(configuration, rootPart, false);
        MTOMStAXSOAPModelBuilder builder = new MTOMStAXSOAPModelBuilder(
                soapFactory,
                sourceInfo.getReader(),
                mimePartProvider,
                mimePartProvider instanceof Detachable ? (Detachable)mimePartProvider : null,
                sourceInfo.getCloseable());
        builder.setAutoClose(true);
        return builder;
    }
}
