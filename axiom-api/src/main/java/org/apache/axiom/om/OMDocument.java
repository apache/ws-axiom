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

import java.util.Iterator;

public interface OMDocument extends OMContainer {

    /** Field XML_10 XML Version 1.0 */
    static final String XML_10 = "1.0";

    /** Field XML_11 XML Version 1.1 */
    static final String XML_11 = "1.1";

    /**
     * Get the document element.
     *
     * @return the document element, or <code>null</code> if the document doesn't have any children
     *     of type {@link OMElement}
     */
    OMElement getOMDocumentElement();

    /**
     * Set the document element of the XML document. If the document has no document element, then
     * the new document element will be appended as the last child. If the document already has a
     * document element, then it will be replaced by the new one and the position of the other
     * children relative to the document element is preserved.
     *
     * <p>Some models (such as SOAP) may throw an exception if the specified element is not allowed
     * as a root element.
     *
     * @param documentElement the new document element; must not be <code>null</code>
     * @throws IllegalArgumentException if the parameter is <code>null</code>
     */
    void setOMDocumentElement(OMElement documentElement);

    /**
     * Get the character set encoding scheme. This is the encoding that was used used for this
     * document at the time of the parsing. This is <code>null</code> when it is not known, such as
     * when the document was created in memory or from a character stream.
     *
     * @return the charset encoding for this document, or <code>null</code> if the encoding is not
     *     known
     */
    String getCharsetEncoding();

    /**
     * Sets the character set encoding scheme to be used.
     *
     * @param charsetEncoding
     */
    void setCharsetEncoding(String charsetEncoding);

    /**
     * Returns the XML version.
     *
     * @return Returns String.
     */
    String getXMLVersion();

    /**
     * Sets the XML version.
     *
     * @param version
     * @see #XML_10
     * @see #XML_11
     */
    void setXMLVersion(String version);

    /**
     * Get the charset encoding of this document as specified in the XML declaration.
     *
     * @return the charset encoding specified in the XML declaration, or <code>null</code> if the
     *     document didn't have an XML declaration or if the <code>encoding</code> attribute was not
     *     specified in the XML declaration
     */
    String getXMLEncoding();

    /**
     * Set the charset encoding for the XML declaration of this document.
     *
     * @param encoding the value of the <code>encoding</code> attribute of the XML declaration
     */
    void setXMLEncoding(String encoding);

    /**
     * XML standalone value. This will be yes, no or null (if not available)
     *
     * @return Returns boolean.
     */
    String isStandalone();

    void setStandalone(String isStandalone);

    /** {@inheritDoc} */
    @Override
    Iterator<OMSerializable> getDescendants(boolean includeSelf);
}
