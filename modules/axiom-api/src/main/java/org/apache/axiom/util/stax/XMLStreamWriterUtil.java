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

package org.apache.axiom.util.stax;

import java.io.IOException;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.util.base64.Base64WriterOutputStream;

/**
 * Contains utility methods to work with {@link XMLStreamWriter} objects.
 */
public class XMLStreamWriterUtil {
    /**
     * Write base64 encoded data to a stream writer. This will result in one or more
     * {@link javax.xml.stream.XMLStreamConstants#CHARACTERS} events to be written
     * to the stream (or zero events if the data handler produces an empty byte sequence),
     * i.e. the data is streamed from the data handler directly to the stream writer.
     * Since no in-memory base64 representation of the entire binary data is built, this
     * method is suitable for very large amounts of data.
     * 
     * @param writer the stream writer to write the data to
     * @param dh the data handler containing the data to encode
     * @throws IOException if an error occurs when reading the data from the data handler
     * @throws XMLStreamException if an error occurs when writing the base64 encoded data to
     *         the stream
     */
    public static void writeBase64(XMLStreamWriter writer, DataHandler dh)
            throws IOException, XMLStreamException {
        
        Base64WriterOutputStream out = new Base64WriterOutputStream(
                new XMLStreamWriterWriter(writer));
        try {
            dh.writeTo(out);
            out.close();
        } catch (XMLStreamIOException ex) {
            throw ex.getXMLStreamException();
        }
    }
}
