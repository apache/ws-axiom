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

package org.apache.axiom.om;

import java.io.InputStream;
import java.io.Reader;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.axiom.mime.MultipartBody;
import org.w3c.dom.Node;

/** Interface OMXMLParserWrapper */
public interface OMXMLParserWrapper {
    /** @return Returns the complete status. */
    boolean isCompleted();

    /**
     * Get the document being built by this builder.
     * 
     * @return the {@link OMDocument} instance
     * @throws UnsupportedOperationException
     *             if there is no document linked to this builder; this may occur if the builder is
     *             associated with an {@link OMSourcedElement}
     */
    OMDocument getDocument();
    
    /**
     * Get the document element, i.e. the root element of the document. Using this method is
     * equivalent to using {@link OMDocument#getOMDocumentElement()} on the document returned by
     * {@link #getDocument()}.
     * <p>
     * Note that this method will never return <code>null</code> (except in the very special case
     * where the document has been requested before and the document element has been removed
     * explicitly): if the document being parsed has no document element, then this will result in a
     * parser error, i.e. an {@link OMException} will be thrown.
     * 
     * @return the document element
     * @throws OMException
     *             if a parse error occurs
     * @throws UnsupportedOperationException
     *             if there is no document linked to this builder; this may occur if the builder is
     *             associated with an {@link OMSourcedElement}
     */
    OMElement getDocumentElement();

    /**
     * Get the document element, optionally discarding the document. The return value of this method
     * is the same as {@link #getDocumentElement()}. However, if the <code>discardDocument</code>
     * parameter is set to <code>true</code>, then the document element is removed from the document
     * and the document itself is discarded. In contrast to using {@link OMElement#detach()} this
     * will not build the element. The implementation also ensures that the element is not built
     * when it is added to another OM tree. This makes it possible to add the content of a document
     * to an existing OM tree while preserving the deferred parsing feature. It is even possible to
     * create an OM tree where different subtrees are associated with different builder instances.
     * 
     * @param discardDocument
     *            specifies whether the document should be discarded
     * @return the document element
     * @throws OMException
     *             if a parse error occurs
     * @throws UnsupportedOperationException
     *             if there is no document linked to this builder; this may occur if the builder is
     *             associated with an {@link OMSourcedElement}
     */
    OMElement getDocumentElement(boolean discardDocument);

    /**
     * Close this builder. This method frees the resources associated with this builder. In
     * particular, it releases the resources held by the underlying parser. This method does
     * <b>not</b> close the underlying input source.
     */
    void close();
    
    /**
     * Detach this builder from its underlying source, so that the state of the source object can be
     * changed without impact on the object model produced by this builder. The effect of this
     * method depends on the type of source object passed to {@link OMXMLBuilderFactory} to create
     * the builder:
     * <p>
     * <table border="1">
     * <caption>Actions performed by the {@link #detach()} method</caption>
     * <tr>
     * <th>Source object type</th>
     * <th>Action performed by this method</th>
     * </tr>
     * <tr>
     * <td>{@link InputStream}, {@link Reader}, {@link StreamSource} with {@link InputStream} or
     * {@link Reader}</td>
     * <td>The remaining unprocessed content of the stream is read into memory so that it can be
     * safely closed. Note that this method doesn't close the stream; this is the responsibility of
     * the caller.</td>
     * </tr>
     * <tr>
     * <td>{@link StreamSource} with system ID and no stream</td>
     * <td>The remaining unprocessed content of the document is read into memory and the associated
     * stream is closed.</td>
     * <tr>
     * <td>{@link Node}, {@link DOMSource}, {@link SAXSource}</td>
     * <td>The object model is built completely.</td>
     * </tr>
     * <tr>
     * <td>{@link MultipartBody}</td>
     * <td>All MIME parts are fetched so that the stream from which the {@link MultipartBody} object
     * has been created can safely be closed.</td>
     * </tr>
     * </table>
     */
    void detach();
}
