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

import java.io.IOException;
import java.io.Writer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.core.stream.CharacterData;
import org.apache.axiom.core.stream.CharacterDataSink;
import org.apache.axiom.core.stream.stax.pull.output.InternalXMLStreamReader;
import org.apache.axiom.ext.stax.CharacterDataReader;
import org.apache.axiom.util.base64.AbstractBase64EncodingOutputStream;
import org.apache.axiom.util.base64.Base64EncodingWriterOutputStream;

final class CharacterDataReaderImpl implements CharacterDataReader {
    private final InternalXMLStreamReader reader;

    CharacterDataReaderImpl(InternalXMLStreamReader reader) {
        this.reader = reader;
    }

    @Override
    public void writeTextTo(final Writer writer) throws XMLStreamException, IOException {
        switch (reader.getEventType()) {
            case XMLStreamReader.CHARACTERS -> {
                Object data = reader.getCharacterData();
                if (data instanceof CharacterData characterData) {
                    characterData.writeTo(
                            new CharacterDataSink() {
                                @Override
                                public Writer getWriter() {
                                    return writer;
                                }

                                @Override
                                public AbstractBase64EncodingOutputStream
                                        getBase64EncodingOutputStream() {
                                    return new Base64EncodingWriterOutputStream(writer);
                                }
                            });
                } else {
                    writer.write(data.toString());
                }
            }
            case XMLStreamReader.CDATA, XMLStreamReader.SPACE, XMLStreamReader.COMMENT -> {
                // TODO: optimize this for CDATA and COMMENT
                writer.write(reader.getText());
            }
            default -> throw new IllegalStateException();
        }
    }
}
