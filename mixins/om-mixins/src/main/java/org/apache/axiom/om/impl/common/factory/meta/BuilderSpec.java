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
package org.apache.axiom.om.impl.common.factory.meta;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.axiom.core.stream.FilteredXmlInput;
import org.apache.axiom.core.stream.NamespaceRepairingFilter;
import org.apache.axiom.core.stream.XmlInput;
import org.apache.axiom.core.stream.dom.input.DOMInput;
import org.apache.axiom.core.stream.sax.input.SAXInput;
import org.apache.axiom.core.stream.stax.pull.input.StAXPullInput;
import org.apache.axiom.mime.MultipartBody;
import org.apache.axiom.mime.Part;
import org.apache.axiom.om.OMAttachmentAccessor;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.common.builder.Detachable;
import org.apache.axiom.om.impl.stream.stax.pull.AxiomXMLStreamReaderHelperFactory;
import org.apache.axiom.om.impl.stream.xop.XOPDecodingFilter;
import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.util.stax.XMLFragmentStreamReader;
import org.apache.axiom.util.xml.stream.XMLEventUtils;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

final class BuilderSpec {
    private final XmlInput input;
    private final Detachable detachable;

    private BuilderSpec(XmlInput input, Detachable detachable) {
        this.input = input;
        this.detachable = detachable;
    }

    private static BuilderSpec create(StAXParserConfiguration configuration,
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
        return new BuilderSpec(new StAXPullInput(reader, AxiomXMLStreamReaderHelperFactory.INSTANCE, true, closeable), detachable);
    }
    
    static BuilderSpec from(XMLStreamReader reader) {
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
        return new BuilderSpec(new FilteredXmlInput(new StAXPullInput(reader, AxiomXMLStreamReaderHelperFactory.INSTANCE, false, null), NamespaceRepairingFilter.DEFAULT), null);
    }

    static BuilderSpec from(StAXParserConfiguration configuration,
            InputSource is) {
        return create(configuration, is, true);
    }

    static BuilderSpec from(StAXParserConfiguration configuration, Source source) {
        if (source instanceof SAXSource) {
            return from((SAXSource)source, true);
        } else if (source instanceof DOMSource) {
            return from(((DOMSource)source).getNode(), true);
        } else if (source instanceof StreamSource) {
            StreamSource streamSource = (StreamSource)source;
            InputSource is = new InputSource();
            is.setByteStream(streamSource.getInputStream());
            is.setCharacterStream(streamSource.getReader());
            is.setPublicId(streamSource.getPublicId());
            is.setSystemId(streamSource.getSystemId());
            return from(configuration, is);
        } else if (source instanceof StAXSource) {
            return from(((StAXSource)source).getXMLStreamReader());
        } else {
            try {
                return new BuilderSpec(
                        new FilteredXmlInput(
                                new StAXPullInput(StAXUtils.getXMLInputFactory().createXMLStreamReader(source), AxiomXMLStreamReaderHelperFactory.INSTANCE, true, null),
                                NamespaceRepairingFilter.DEFAULT),
                        null);
            } catch (XMLStreamException ex) {
                throw new OMException(ex);
            }
        }
    }

    static BuilderSpec from(Node node, boolean expandEntityReferences) {
        return new BuilderSpec(
                new FilteredXmlInput(
                        new FilteredXmlInput(
                                new DOMInput(node, expandEntityReferences),
                                NSUnawareNodeFilter.INSTANCE),
                        NamespaceRepairingFilter.DEFAULT),
                null);
    }

    static BuilderSpec from(SAXSource source, boolean expandEntityReferences) {
        return new BuilderSpec(new FilteredXmlInput(new SAXInput(source, expandEntityReferences), NamespaceRepairingFilter.DEFAULT), null);
    }

    static BuilderSpec from(StAXParserConfiguration configuration, final MultipartBody message) {
        Part rootPart = message.getRootPart();
        InputSource is = new InputSource(rootPart.getInputStream(false));
        is.setEncoding(rootPart.getContentType().getParameter("charset"));
        BuilderSpec spec = create(configuration, is, false);
        return new BuilderSpec(
                new FilteredXmlInput(
                        spec.getInput(),
                        new XOPDecodingFilter(new OMAttachmentAccessor() {
                            @Override
                            public DataHandler getDataHandler(String contentID) {
                                Part part = message.getPart(contentID);
                                return part == null ? null : part.getDataHandler();
                            }
                        })),
                new Detachable() {
                    @Override
                    public void detach() {
                        message.detach();
                    }
                });
    }

    static BuilderSpec from(StAXParserConfiguration configuration, Source source, OMAttachmentAccessor attachmentAccessor) {
        BuilderSpec spec = from(configuration, source);
        return new BuilderSpec(
                new FilteredXmlInput(
                        spec.getInput(),
                        new XOPDecodingFilter(attachmentAccessor)),
                spec.getDetachable());
    }

    XmlInput getInput() {
        return input;
    }

    Detachable getDetachable() {
        return detachable;
    }
}
