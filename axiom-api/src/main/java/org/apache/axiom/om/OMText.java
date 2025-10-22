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

import javax.xml.namespace.QName;

import org.apache.axiom.blob.Blob;

/**
 * Represents character data in an XML document. A node of this type is used to represent character
 * data that may appear in element content as well as the prolog and epilog of a document. Note that
 * this node type is used for normal character data, CDATA sections and ignorable whitespace. The
 * {@link OMNode#getType()} method may be used to distinguish between these different types of
 * character data.
 *
 * <p>By default, Axiom uses StAX parsers configured in coalescing mode. As a consequence, CDATA
 * sections will not result in nodes of type {@link OMNode#CDATA_SECTION_NODE} nodes, but of type
 * {@link OMNode#TEXT_NODE} (See the Javadoc of {@link org.apache.axiom.om.util.StAXUtils} for
 * information about how to change this behavior). In addition, the object model instance will never
 * contain two adjacent {@link OMText} siblings.
 *
 * <p>The same is not necessarily true for Axiom trees that have been built or updated
 * programmatically or that contain nodes resulting from the expansion of an {@link
 * OMSourcedElement}. Therefore, code that manipulates character data MUST NOT assume that text
 * nodes are always coalesced. In particular, when extracting character data from an element, {@link
 * OMElement#getText()} should be used instead of {@link OMText#getText()}.
 *
 * <p>An {@link OMText} node stores the character data as {@link String}, <code>char[]</code> or a
 * {@link Blob}. The latter is used for base64 encoded binary data.
 */
public interface OMText extends OMNode {
    /**
     * Returns the text value of this node.
     *
     * @return Returns String.
     */
    String getText();

    /**
     * @deprecated
     */
    char[] getTextCharacters();

    /**
     * @deprecated
     */
    boolean isCharacters();

    /**
     * @deprecated If the underlying parser is non coalescing, then this method may unexpectedly
     *     fail or return an incorrect result. Always use {@link OMElement#getTextAsQName()} to get
     *     the QName value of an element.
     */
    QName getTextAsQName();

    /**
     * @deprecated This API is going away. Please use {@link OMElement#getTextAsQName()} instead.
     * @see #getTextAsQName()
     */
    OMNamespace getNamespace();

    /**
     * Returns a {@link Blob} containing the base64-decoded content of this node.
     *
     * @return the base64-decoded content
     */
    Blob getBlob();

    /**
     * @return Returns boolean flag saying whether the node contains an optimized text or not.
     */
    // TODO: inconsistent naming
    boolean isOptimized();

    /**
     * Sets the optimize flag.
     *
     * @param value true to optimize binary content (usually w/MTOM)
     */
    void setOptimize(boolean value);

    /**
     * @return Returns boolean flag saying whether the node contains binary or not.
     */
    boolean isBinary();

    /**
     * Sets the isBinary flag. Receiving binary can happen as either MTOM attachments or as Base64
     * Text In the case of Base64 user has to explicitly specify that the content is binary, before
     * calling getDataHandler(), getInputStream()....
     *
     * @param value true if the content is binary
     */
    void setBinary(boolean value);

    /**
     * Gets the content id.
     *
     * @return Returns String.
     */
    String getContentID();

    /**
     * Set a specific content id
     *
     * @param cid
     */
    void setContentID(String cid);
}
