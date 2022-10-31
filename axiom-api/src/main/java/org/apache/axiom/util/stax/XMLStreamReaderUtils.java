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
import java.io.Reader;
import java.io.Writer;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.blob.Blobs;
import org.apache.axiom.blob.MemoryBlob;
import org.apache.axiom.ext.stax.CharacterDataReader;
import org.apache.axiom.ext.stax.DelegatingXMLStreamReader;
import org.apache.axiom.ext.stax.datahandler.DataHandlerReader;
import org.apache.axiom.util.activation.BlobDataSource;
import org.apache.axiom.util.activation.EmptyDataSource;
import org.apache.axiom.util.base64.Base64DecodingOutputStreamWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Contains utility methods to work with {@link XMLStreamReader} objects, including the extension
 * defined by {@link DataHandlerReader}.
 */
public class XMLStreamReaderUtils {
    private static final Log log = LogFactory.getLog(XMLStreamReaderUtils.class);
    
    private XMLStreamReaderUtils() {}
    
    /**
     * Get the {@link DataHandlerReader} extension for a given {@link XMLStreamReader}, if
     * available.
     * 
     * @param reader
     *            the stream reader to get the {@link DataHandlerReader} extension from
     * @return the implementation of the extension, or <code>null</code> if the
     *         {@link XMLStreamReader} doesn't expose base64 encoded binary content as
     *         {@link DataHandler} objects.
     */
    public static DataHandlerReader getDataHandlerReader(final XMLStreamReader reader) {
        try {
            return (DataHandlerReader)reader.getProperty(DataHandlerReader.PROPERTY);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
    
    /**
     * Helper method to implement {@link XMLStreamReader#getProperty(String)}. This method
     * processes the property defined by {@link DataHandlerReader#PROPERTY}.
     * 
     * @param extension
     *            the reference to the {@link DataHandlerReader} extension for the
     *            {@link XMLStreamReader} implementation
     * @param propertyName
     *            the name of the property, as passed to the
     *            {@link XMLStreamReader#getProperty(String)} method
     * @return the property value as specified by the {@link DataHandlerReader} extension;
     *         <code>null</code> if the property doesn't match
     */
    public static Object processGetProperty(DataHandlerReader extension, String propertyName) {
        if (extension == null || propertyName == null) {
            throw new IllegalArgumentException();
        } else if (propertyName.equals(DataHandlerReader.PROPERTY)) {
            return extension;
        } else {
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
            MemoryBlob blob = Blobs.createMemoryBlob();
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
     * Get the text content of the current element as a {@link Reader} object.
     * 
     * @param reader
     *            The XML stream reader to read the element text from. The reader must be positioned
     *            on a {@link XMLStreamConstants#START_ELEMENT} event.
     * @param allowNonTextChildren
     *            If set to <code>true</code>, non text child nodes are allowed and skipped. If set
     *            to <code>false</code> only text nodes are allowed and the presence of any other
     *            type of child node will trigger an exception.
     * @return The reader from which the element text can be read. After the reader has reported the
     *         end of the stream, the XML stream reader will be positioned on the
     *         {@link XMLStreamConstants#END_ELEMENT} event corresponding to the initial
     *         {@link XMLStreamConstants#START_ELEMENT} event. Calling {@link Reader#close()} on the
     *         returned reader has no effect. Any parser exception will be reported by the reader
     *         using {@link XMLStreamIOException}.
     * @throws IllegalStateException
     *             if the XML stream reader is not positioned on a
     *             {@link XMLStreamConstants#START_ELEMENT} event
     */
    public static Reader getElementTextAsStream(XMLStreamReader reader,
            boolean allowNonTextChildren) {
        if (reader.getEventType() != XMLStreamReader.START_ELEMENT) {
            throw new IllegalStateException("Reader must be on a START_ELEMENT event");
        }
        return new TextFromElementReader(reader, allowNonTextChildren);
    }
    
    /**
     * Searches the wrapper and delegate classes to find the original {@link XMLStreamReader}.
     * This method should only be used when a consumer of Axiom really needs to 
     * access the original stream reader.
     * @param parser XMLStreamReader used by Axiom
     * @return original parser 
     * @deprecated As of version 1.3.0, Axiom no longer permits access to the original stream
     *             reader.
     */
    public static XMLStreamReader getOriginalXMLStreamReader(XMLStreamReader parser) {
        if (log.isDebugEnabled()) {
            String clsName = (parser != null) ? parser.getClass().toString() : "null";
            log.debug("Entry getOriginalXMLStreamReader: " + clsName);
        }
        while (parser instanceof DelegatingXMLStreamReader) {
            parser = ((DelegatingXMLStreamReader) parser).getParent();
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
}
