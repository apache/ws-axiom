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

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.attachments.ByteArrayDataSource;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.om.util.Base64;

/**
 * Contains utility methods to work with {@link XMLStreamReader} objects.
 */
public class XMLStreamReaderUtils {
    /**
     * Get the {@link DataHandlerReader} extension from a given {@link XMLStreamReader}.
     * 
     * @param reader
     *            the stream for which the method should return the {@link DataHandlerReader}
     *            extension
     * @return the reference to the {@link DataHandlerReader} extension, or <code>null</code> if
     *         the reader doesn't implement the extension
     */
    public static DataHandlerReader getDataHandlerReader(XMLStreamReader reader) {
        try {
            return (DataHandlerReader)reader.getProperty(DataHandlerReader.PROPERTY);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
    
    /**
     * Get a {@link DataHandler} for the binary data encoded in an element. The method supports
     * base64 encoded character data as well as optimized binary data through the
     * {@link DataHandlerReader} extension.
     * <p>
     * <em>Precondition</em>: the reader is on a {@link XMLStreamConstants#START_ELEMENT}
     * <p>
     * <em>Postcondition</em>: the reader is on the corresponding
     * {@link XMLStreamConstants#END_ELEMENT}
     * 
     * @param reader the stream to read the data from
     * @return the binary data from the element
     */
    public static DataHandler getDataHandlerFromElement(XMLStreamReader reader)
            throws XMLStreamException {
        
        DataHandlerReader dhr = getDataHandlerReader(reader);
        String base64;
        if (dhr == null) {
            // In this case the best way to get the content of the element is using
            // the getElementText method
            base64 = reader.getElementText();
        } else {
            int event = reader.next();
            if (event == XMLStreamConstants.END_ELEMENT) {
                // This means that the element is actaullay empty -> return empty DataHandler
                return new DataHandler(new ByteArrayDataSource(new byte[0]));
            } else if (event != XMLStreamConstants.CHARACTERS) {
                throw new XMLStreamException("Expected a CHARACTER event");
            }
            if (dhr.isBinary()) {
                DataHandler dh = dhr.getDataHandler();
                reader.next();
                return dh;
            }
            base64 = reader.getText();
            StringBuffer buff = null;
            // Take into account that in non coalescing mode, there may be additional
            // CHARACTERS events
            loop: while (true) {
                switch (reader.next()) {
                    case XMLStreamConstants.CHARACTERS:
                        if (buff == null) {
                            buff = new StringBuffer(base64);
                        }
                        buff.append(reader.getText());
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        break loop;
                    default:
                        throw new XMLStreamException("Expected a CHARACTER event");
                }
            }
            if (buff != null) {
                base64 = buff.toString();
            }
        }
        return new DataHandler(new ByteArrayDataSource(Base64.decode(base64)));
    }
}
