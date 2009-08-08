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

package org.apache.axiom.util.stax.dialect;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.Reader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;

import org.apache.axiom.util.stax.wrapper.XMLInputFactoryWrapper;

class BEAInputFactoryWrapper extends XMLInputFactoryWrapper {
    public BEAInputFactoryWrapper(XMLInputFactory parent) {
        super(parent);
    }

    public XMLStreamReader createXMLStreamReader(InputStream stream) throws XMLStreamException {
        return createXMLStreamReader(null, stream);
    }

    public XMLStreamReader createXMLStreamReader(String systemId, InputStream stream)
            throws XMLStreamException {
        // The getEncoding() method of the stream reader produced by the reference implementation
        // doesn't return complete information about the effective encoding. To work around this,
        // we need to implement the detection algorithm described in Appendix F.1 of the
        // XML 1.0 specifications (Fifth Edition). Note that the encoding determined here may be
        // overridden by the XML encoding declaration, if present in the XML document. This
        // information is already available from the stream reader, so that we don't need to
        // reimplement this part.
        // TODO: this needs some more unit testing!
        byte[] startBytes = new byte[4];
        try {
            boolean useMark = stream.markSupported();
            if (useMark) {
                stream.mark(4);
            } else {
                stream = new PushbackInputStream(stream, 4);
            }
            int read = 0;
            do {
                int c = stream.read(startBytes, read, 4-read);
                if (c == -1) {
                    throw new XMLStreamException("Unexpected end of stream");
                }
                read += c;
            } while (read < 4);
            if (useMark) {
                stream.reset();
            } else {
                ((PushbackInputStream)stream).unread(startBytes);
            }
        } catch (IOException ex) {
            throw new XMLStreamException("Unable to read start bytes", ex);
        }
        int marker = (startBytes[0] & 0xFF) << 24 + (startBytes[1] & 0xFF) << 16
                + (startBytes[2] & 0xFF) << 8 + (startBytes[3] & 0xFF);
        String encoding;
        switch (marker) {
            case 0x0000FEFF:
            case 0xFFFE0000:
            case 0x0000FFFE:
            case 0xFEFF0000:
            case 0x0000003C:
            case 0x3C000000:
            case 0x00003C00:
            case 0x003C0000:
                encoding = "UCS-4";
                break;
            case 0x003C003F:
                encoding = "UTF-16BE";
                break;
            case 0x3C003F00:
                encoding = "UTF-16LE";
                break;
            case 0x3C3F786D:
                encoding = "UTF-8";
                break;
            default:
                if ((marker & 0xFFFF0000) == 0xFEFF0000) {
                    encoding = "UTF-16BE";
                } else if ((marker & 0xFFFF0000) == 0xFFFE0000) {
                    encoding = "UTF-16LE";
                } else {
                    encoding = "UTF-8";
                }
        }
        XMLStreamReader reader;
        if (systemId == null) {
            reader = super.createXMLStreamReader(stream);
        } else {
            reader = super.createXMLStreamReader(systemId, stream);
        }
        return new BEAStreamReaderWrapper(reader, encoding);
    }

    public XMLStreamReader createXMLStreamReader(InputStream stream, String encoding)
            throws XMLStreamException {
        return new BEAStreamReaderWrapper(super.createXMLStreamReader(stream, encoding), null);
    }

    public XMLStreamReader createXMLStreamReader(Reader reader) throws XMLStreamException {
        return new BEAStreamReaderWrapper(super.createXMLStreamReader(reader), null);
    }

    public XMLStreamReader createXMLStreamReader(Source source) throws XMLStreamException {
        return new BEAStreamReaderWrapper(super.createXMLStreamReader(source), null);
    }

    public XMLStreamReader createXMLStreamReader(String systemId, Reader reader)
            throws XMLStreamException {
        return new BEAStreamReaderWrapper(super.createXMLStreamReader(systemId, reader), null);
    }
}
