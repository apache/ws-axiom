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
package org.apache.axiom.om.impl.stream.stax.pull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.core.stream.CharacterData;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.stax.pull.input.DTDInfo;
import org.apache.axiom.core.stream.stax.pull.input.XMLStreamReaderHelper;
import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.om.impl.intf.TextContent;
import org.apache.axiom.util.stax.XMLStreamReaderUtils;

final class AxiomXMLStreamReaderHelper extends XMLStreamReaderHelper {
    private final XMLStreamReader reader;

    /**
     * Reference to the {@link DataHandlerReader} extension of the reader, or <code>null</code> if
     * the reader doesn't support this extension.
     */
    private final DataHandlerReader dataHandlerReader;

    AxiomXMLStreamReaderHelper(XMLStreamReader reader) {
        this.reader = reader;
        dataHandlerReader = XMLStreamReaderUtils.getDataHandlerReader(reader);
    }

    @Override
    public DTDInfo getDTDInfo() throws StreamException {
        DTDReader dtdReader;
        try {
            dtdReader = (DTDReader) reader.getProperty(DTDReader.PROPERTY);
        } catch (IllegalArgumentException ex) {
            dtdReader = null;
        }
        if (dtdReader == null) {
            throw new StreamException(
                    "Cannot process DTD events because the XMLStreamReader doesn't support the DTDReader extension");
        }
        return new DTDInfo(
                dtdReader.getRootName(), dtdReader.getPublicId(), dtdReader.getSystemId());
    }

    @Override
    public CharacterData getCharacterData() throws StreamException {
        if (dataHandlerReader != null && dataHandlerReader.isBinary()) {
            if (dataHandlerReader.isDeferred()) {
                return new TextContent(
                        dataHandlerReader.getContentID(),
                        dataHandlerReader.getDataHandlerProvider(),
                        dataHandlerReader.isOptimized());
            } else {
                try {
                    return new TextContent(
                            dataHandlerReader.getContentID(),
                            dataHandlerReader.getDataHandler(),
                            dataHandlerReader.isOptimized());
                } catch (XMLStreamException ex) {
                    throw new StreamException(ex);
                }
            }
        } else {
            return null;
        }
    }
}
