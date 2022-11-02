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

package org.apache.axiom.om.impl;

import java.io.OutputStream;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.ext.stax.BlobProvider;
import org.apache.axiom.ext.stax.BlobWriter;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.util.stax.XMLStreamWriterUtils;

/**
 * MTOMXMLStreamWriter is an XML + Attachments stream writer.
 * 
 * For the moment this assumes that transport takes the decision of whether to optimize or not by
 * looking at whether the MTOM optimize is enabled and also looking at the OM tree whether it has any
 * optimizable content.
 */
public abstract class MTOMXMLStreamWriter implements XMLStreamWriter {
    /**
     * Check if MTOM is enabled.
     * <p>
     * Note that serialization code should use
     * {@link XMLStreamWriterUtils#writeBlob(XMLStreamWriter, Blob, String, boolean)}
     * or
     * {@link XMLStreamWriterUtils#writeBlob(XMLStreamWriter, BlobProvider, String, boolean)}
     * to submit any binary content and let this writer decide whether the content should be written
     * as base64 encoded character data or using {@code xop:Include}. This makes optimization
     * entirely transparent for the caller and there should be no need to check if the writer is
     * producing MTOM. However, in some cases this is not possible, such as when integrating with
     * 3rd party libraries. The serialization code should then use
     * {@link #prepareDataHandler(DataHandler)} so that it can write {@code xop:Include} elements
     * directly to the stream. In that case, the code may use the {@link #isOptimized()} method
     * check if MTOM is enabled at all.
     * 
     * @return <code>true</code> if MTOM is enabled, <code>false</code> otherwise
     */
    public abstract boolean isOptimized();

    /**
     * Prepare a {@link DataHandler} for serialization without using the {@link BlobWriter}
     * API. The method first determines whether the binary data represented by the
     * {@link DataHandler} should be optimized or inlined. If the data should not be optimized, then
     * the method returns <code>null</code> and the caller is expected to use
     * {@link #writeCharacters(String)} or {@link #writeCharacters(char[], int, int)} to write the
     * base64 encoded data to the stream. If the data should be optimized, then the method returns a
     * content ID and the caller is expected to generate an {@code xop:Include} element referring
     * to that content ID.
     * <p>
     * This method should only be used to integrate Axiom with third party libraries that support
     * XOP. In all other cases,
     * {@link XMLStreamWriterUtils#writeBlob(XMLStreamWriter, Blob, String, boolean)}
     * or
     * {@link XMLStreamWriterUtils#writeBlob(XMLStreamWriter, BlobProvider, String, boolean)}
     * should be used to write base64Binary values and the application code should never generate
     * {@code xop:Include} elements itself.
     * 
     * @param dataHandler
     *            the {@link DataHandler} that the caller intends to write to the stream
     * @return the content ID that the caller must use in the {@code xop:Include} element or
     *         <code>null</code> if the base64 encoded data should not be optimized
     */
    public abstract String prepareDataHandler(DataHandler dataHandler);

    /**
     * Returns the character set encoding scheme. If the value of the charSetEncoding is not set
     * then the default will be returned.
     *
     * @return Returns encoding.
     */
    public abstract String getCharSetEncoding();

    /**
     * Get the output format used by this writer.
     * <p>
     * The caller should use the returned instance in a read-only way, i.e.
     * he should not modify the settings of the output format. Any attempt
     * to do so will lead to unpredictable results.
     * 
     * @return the output format used by this writer
     */
    public abstract OMOutputFormat getOutputFormat();

    /**
     * Get the underlying {@link OutputStream} for this writer, if available. This method allows a
     * node (perhaps an {@link org.apache.axiom.om.OMSourcedElement}) to write its content directly
     * to the byte stream.
     * <p>
     * <b>WARNING:</b> This method should be used with extreme care. The caller must be prepared to
     * handle the following issues:
     * <ul>
     * <li>The caller must use the right charset encoding when writing to the stream.
     * <li>The caller should avoid writing byte order marks to the stream.
     * <li>The caller must be aware of the fact that a default namespace might have been set in the
     * context where the byte stream is requested. If the XML data written to the stream contains
     * unqualified elements, then the caller must make sure that the default namespace is redeclared
     * as appropriate.
     * </ul>
     * 
     * @return the underlying byte stream, or <code>null</code> if the stream is not accessible
     */
    public abstract OutputStream getOutputStream() throws XMLStreamException;
}
