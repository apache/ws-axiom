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
import java.io.Writer;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.ext.stax.CharacterDataReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.util.activation.EmptyDataSource;
import org.apache.axiom.util.base64.Base64DecodingOutputStreamWriter;
import org.apache.axiom.util.blob.BlobDataSource;
import org.apache.axiom.util.blob.MemoryBlob;
import org.apache.axiom.util.blob.WritableBlob;
import org.apache.axiom.util.stax.wrapper.XMLStreamReaderContainer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Contains utility methods to work with {@link XMLStreamReader} objects.
 */
public class XMLStreamReaderUtils {
    
    private static Log log = LogFactory.getLog(XMLStreamReaderUtils.class);
   
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
        
        int event = reader.next();
        if (event == XMLStreamConstants.END_ELEMENT) {
            // This means that the element is actually empty -> return empty DataHandler
            return new DataHandler(new EmptyDataSource("application/octet-stream"));
        } else if (event != XMLStreamConstants.CHARACTERS) {
            throw new XMLStreamException("Expected a CHARACTER event");
        }
        DataHandlerReader dhr = getDataHandlerReader(reader);
        if (dhr != null && dhr.isBinary()) {
            DataHandler dh = dhr.getDataHandler();
            reader.next();
            return dh;
        } else {
            WritableBlob blob = new MemoryBlob();
            Writer out = new Base64DecodingOutputStreamWriter(blob.getOutputStream());
            try {
                writeTextTo(reader, out);
                // Take into account that in non coalescing mode, there may be additional
                // CHARACTERS events
                loop: while (true) {
                    switch (reader.next()) {
                        case XMLStreamConstants.CHARACTERS:
                            writeTextTo(reader, out);
                            break;
                        case XMLStreamConstants.END_ELEMENT:
                            break loop;
                        default:
                            throw new XMLStreamException("Expected a CHARACTER event");
                    }
                }
                out.close();
            } catch (IOException ex) {
                throw new XMLStreamException("Error during base64 decoding", ex);
            }
            return new DataHandler(new BlobDataSource(blob, "application/octet-string"));
        }
    }
    
    /**
     * Get the character data for the current event from the given reader and
     * write it to the given writer. The method will try to figure out the most
     * efficient way to copy the data without unnecessary buffering or
     * conversions between strings and character arrays.
     * 
     * @param reader
     *            the reader to get the character data from
     * @param writer
     *            the writer to write the character data to
     * @throws XMLStreamException
     *             if the underlying XML source is not well-formed
     * @throws IOException
     *             if an I/O error occurs when writing the character data
     * @throws IllegalStateException
     *             if this state is not a valid text state.
     * @see CharacterDataReader
     */
    public static void writeTextTo(XMLStreamReader reader, Writer writer) throws XMLStreamException, IOException {
        CharacterDataReader cdataReader;
        try {
            cdataReader = (CharacterDataReader)reader.getProperty(CharacterDataReader.PROPERTY);
        } catch (IllegalArgumentException ex) {
            cdataReader = null;
        }
        if (cdataReader != null) {
            cdataReader.writeTextTo(writer);
        } else {
            writer.write(reader.getText());
        }
    }
    
    /**
     * Searches the wrapper and delegate classes to find the original {@link XMLStreamReader}.
     * This method should only be used when a consumer of Axiom really needs to 
     * access the original stream reader.
     * @param parser XMLStreamReader used by Axiom
     * @return original parser 
     */
    public static XMLStreamReader getOriginalXMLStreamReader(XMLStreamReader parser) {
        if (log.isDebugEnabled()) {
            String clsName = (parser != null) ? parser.getClass().toString() : "null";
            log.debug("Entry getOriginalXMLStreamReader: " + clsName);
        }
        while (parser instanceof XMLStreamReaderContainer) {
            parser = ((XMLStreamReaderContainer) parser).getParent();
            if (log.isDebugEnabled()) {
                String clsName = (parser != null) ? parser.getClass().toString() : "null";
                log.debug("  parent: " + clsName);
            }
        }
        if (log.isDebugEnabled()) {
            String clsName = (parser != null) ? parser.getClass().toString() : "null";
            log.debug("Exit getOriginalXMLStreamReader: " + clsName);
        }
        return parser;
    }
    
    /**
     * Searches the wrapper and delegate classes to find an
     * {@link XMLStreamReader} of a given type.
     * 
     * @param parser
     *            the object to extract the wrapped reader from
     * @param type
     *            the type of reader to search for
     * @return the first {@link XMLStreamReader} of the given type in the chain
     *         of wrappers, or <code>null</code> if no such reader was found
     */
    public static XMLStreamReader getWrappedXMLStreamReader(XMLStreamReader parser, Class type) {
        if (log.isDebugEnabled()) {
            String clsName = (parser != null) ? parser.getClass().toString() : "null";
            log.debug("Entry getWrappedXMLStreamReader: " + clsName);
        }
        while (!type.isInstance(parser) &&
                (parser instanceof XMLStreamReaderContainer)) {
            parser = ((XMLStreamReaderContainer) parser).getParent();
            if (log.isDebugEnabled()) {
                String clsName = (parser != null) ? parser.getClass().toString() : "null";
                log.debug("  parent: " + clsName);
            }
        }
        if (!type.isInstance(parser)) {
            parser = null;
        }
        if (log.isDebugEnabled()) {
            String clsName = (parser != null) ? parser.getClass().toString() : "null";
            log.debug("Exit getWrappedXMLStreamReader: " + clsName);
        }
        return parser;
    }
}
