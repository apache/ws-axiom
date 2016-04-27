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

import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.core.impl.builder.BuilderImpl;
import org.apache.axiom.core.impl.builder.BuilderListener;
import org.apache.axiom.core.impl.builder.PlainXMLModel;
import org.apache.axiom.core.stream.FilteredXmlInput;
import org.apache.axiom.core.stream.NamespaceRepairingFilter;
import org.apache.axiom.core.stream.XmlInput;
import org.apache.axiom.core.stream.dom.DOMInput;
import org.apache.axiom.core.stream.sax.SAXInput;
import org.apache.axiom.mime.MimePartProvider;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.builder.Detachable;
import org.apache.axiom.om.impl.common.builder.OMXMLParserWrapperImpl;
import org.apache.axiom.om.impl.stream.stax.StAXPullInput;
import org.apache.axiom.om.impl.stream.xop.XOPDecodingFilter;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.soap.impl.common.builder.SOAPFilter;
import org.apache.axiom.soap.impl.common.builder.SOAPModel;
import org.apache.axiom.soap.impl.common.builder.SOAPModelBuilderImpl;
import org.apache.axiom.soap.impl.intf.AxiomSOAPEnvelope;
import org.apache.axiom.soap.impl.intf.AxiomSOAPMessage;
import org.apache.axiom.util.stax.XMLEventUtils;
import org.apache.axiom.util.stax.XMLFragmentStreamReader;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

abstract class BuilderFactory<T extends OMXMLParserWrapper> {
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
    
    final static BuilderFactory<OMXMLParserWrapper> OM = new BuilderFactory<OMXMLParserWrapper>() {
        @Override
        OMXMLParserWrapper createBuilder(NodeFactory nodeFactory, XmlInput input,
                Detachable detachable) {
            return new OMXMLParserWrapperImpl(new BuilderImpl(input, nodeFactory,
                    PlainXMLModel.INSTANCE, null), detachable);
        }
    };

    final static BuilderFactory<SOAPModelBuilder> SOAP = new BuilderFactory<SOAPModelBuilder>() {
        @Override
        SOAPModelBuilder createBuilder(NodeFactory nodeFactory, XmlInput input,
                Detachable detachable) {
            BuilderImpl builder = new BuilderImpl(new FilteredXmlInput(input, SOAPFilter.INSTANCE), nodeFactory, new SOAPModel(), null);
            // The SOAPFactory instance linked to the SOAPMessage is unknown until we reach the
            // SOAPEnvelope. Register a post-processor that does the necessary updates on the
            // SOAPMessage.
            builder.addListener(new BuilderListener() {
                private AxiomSOAPMessage message;
                
                @Override
                public Runnable nodeAdded(CoreNode node, int depth) {
                    if (node instanceof AxiomSOAPMessage) {
                        message = (AxiomSOAPMessage)node;
                    } else if (message != null && node instanceof AxiomSOAPEnvelope) {
                        message.initSOAPFactory((SOAPFactory)((AxiomSOAPEnvelope)node).getOMFactory());
                    }
                    return null;
                }
            });
            return new SOAPModelBuilderImpl(builder, detachable);
        }
    };

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
    
    abstract T createBuilder(NodeFactory nodeFactory, XmlInput input, Detachable detachable);

    final T createBuilder(NodeFactory nodeFactory, XmlInput input, boolean repairNamespaces,
            Detachable detachable) {
        return createBuilder(
                nodeFactory,
                repairNamespaces ? new FilteredXmlInput(input, NamespaceRepairingFilter.DEFAULT) : input,
                detachable);
    }
    
    final T createBuilder(NodeFactory nodeFactory, XMLStreamReader reader) {
        int eventType = reader.getEventType();
        switch (eventType) {
            case XMLStreamReader.START_DOCUMENT:
                break;
            case XMLStreamReader.START_ELEMENT:
                reader = new XMLFragmentStreamReader(reader);
                break;
            default:
                throw new OMException("The supplied XMLStreamReader is in an unexpected state ("
                        + XMLEventUtils.getEventTypeString(eventType) + ")");
        }
        return createBuilder(nodeFactory, new StAXPullInput(reader, false, null), true, null);
    }

    final T createBuilder(NodeFactory nodeFactory, StAXParserConfiguration configuration,
            InputSource is) {
        SourceInfo sourceInfo = createXMLStreamReader(configuration, is, true);
        return createBuilder(nodeFactory,
                new StAXPullInput(sourceInfo.getReader(), true, sourceInfo.getCloseable()), false,
                sourceInfo.getDetachable());
    }

    final T createBuilder(NodeFactory nodeFactory, StAXParserConfiguration configuration, Source source) {
        if (source instanceof SAXSource) {
            return createBuilder(nodeFactory, (SAXSource)source, true);
        } else if (source instanceof DOMSource) {
            return createBuilder(nodeFactory, ((DOMSource)source).getNode(), true);
        } else if (source instanceof StreamSource) {
            StreamSource streamSource = (StreamSource)source;
            InputSource is = new InputSource();
            is.setByteStream(streamSource.getInputStream());
            is.setCharacterStream(streamSource.getReader());
            is.setPublicId(streamSource.getPublicId());
            is.setSystemId(streamSource.getSystemId());
            return createBuilder(nodeFactory, configuration, is);
        } else {
            try {
                return createBuilder(nodeFactory,
                        new StAXPullInput(StAXUtils.getXMLInputFactory().createXMLStreamReader(source), true, null),
                        true, null);
            } catch (XMLStreamException ex) {
                throw new OMException(ex);
            }
        }
    }

    final T createBuilder(NodeFactory nodeFactory, Node node, boolean expandEntityReferences) {
        return createBuilder(nodeFactory, new DOMInput(node, expandEntityReferences), true, null);
    }

    final T createBuilder(NodeFactory nodeFactory, SAXSource source, boolean expandEntityReferences) {
        return createBuilder(nodeFactory, new SAXInput(source, expandEntityReferences), true, null);
    }

    final T createBuilder(NodeFactory nodeFactory, StAXParserConfiguration configuration,
            InputSource rootPart, MimePartProvider mimePartProvider) {
        SourceInfo sourceInfo = createXMLStreamReader(configuration, rootPart, false);
        return createBuilder(nodeFactory,
                new FilteredXmlInput(
                        new StAXPullInput(sourceInfo.getReader(), true, sourceInfo.getCloseable()),
                        new XOPDecodingFilter(mimePartProvider)),
                false,
                mimePartProvider instanceof Detachable ? (Detachable) mimePartProvider : null);
    }
}
