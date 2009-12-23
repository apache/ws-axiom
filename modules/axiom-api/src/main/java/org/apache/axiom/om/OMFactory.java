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

import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;

/** Class OMFactory */
public interface OMFactory {

    /** Creates a new OMDocument. */
    OMDocument createOMDocument();

    OMDocument createOMDocument(OMXMLParserWrapper builder);


    /**
     * @param localName
     * @param ns        - This can be null
     */
    OMElement createOMElement(String localName, OMNamespace ns);

    OMElement createOMElement(String localName, OMNamespace ns, OMContainer parent)
            throws OMException;

    /**
     * @param localName
     * @param ns        - this can be null
     * @param parent
     * @param builder
     */
    OMElement createOMElement(String localName, OMNamespace ns,
                                     OMContainer parent,
                                     OMXMLParserWrapper builder);

    /**
     * Construct element with arbitrary data source. This is an optional operation which may not be
     * supported by all factories.
     *
     * @param source
     * @param localName
     * @param ns
     */
    OMSourcedElement createOMElement(OMDataSource source, String localName,
                                     OMNamespace ns);

    /**
     * Construct element with arbitrary data source. This is an optional operation which may not be
     * supported by all factories.
     *
     * @param source the data source
     * @param qname the name of the element produced by the data source
     */
    OMSourcedElement createOMElement(OMDataSource source, QName qname);

    /**
     * This is almost the same as as createOMElement(localName,OMNamespace) method above. But some
     * people may, for some reason, need to use the conventional method of putting a namespace. Or
     * in other words people might not want to use the new OMNamespace. Well, this is for those
     * people.
     *
     * @param localName
     * @param namespaceURI
     * @param namespacePrefix
     * @return Returns the newly created OMElement.
     */
    OMElement createOMElement(String localName,
                                     String namespaceURI,
                                     String namespacePrefix);

    /**
     * Create an OMElement with the given QName under the given parent.
     *
     * If the QName contains a prefix, we will ensure that an OMNamespace is created
     * mapping the given namespace to the given prefix.  If no prefix is passed, we'll
     * use whatever's already mapped in the parent, or create a generated one.
     *
     * @param qname the QName of the element to create
     * @param parent the OMContainer in which to place the new element
     * @return Returns the new OMElement
     * @throws OMException if there's a namespace mapping problem
     */
    OMElement createOMElement(QName qname, OMContainer parent) throws OMException;

    /**
     * Create an OMElement with the given QName
     *
     * If the QName contains a prefix, we will ensure that an OMNamespace is created
     * mapping the given namespace to the given prefix.  If no prefix is passed, we'll
     * use whatever's already mapped in the parent, or create a generated one.
     *
     * @param qname
     * @return the new OMElement.
     */
    OMElement createOMElement(QName qname) throws OMException;

    /**
     * Create an {@link OMNamespace} instance or retrieve an existing one if the factory supports
     * pooling.
     * 
     * @param uri
     *            the namespace URI; must not be <code>null</code>
     * @param prefix
     *            the prefix
     * @return the {@link OMNamespace} instance
     * @throws IllegalArgumentException
     *             if <code>uri</code> is null
     */
    OMNamespace createOMNamespace(String uri, String prefix);

    /**
     * @param parent
     * @param text
     * @return Returns OMText.
     */
    OMText createOMText(OMContainer parent, String text);

    /**
     * Create OMText node that is a copy of the source text node
     * @param parent
     * @param source
     * @return
     */
    public OMText createOMText(OMContainer parent, OMText source);
    
    /**
     * @param parent
     * @param text   - This text itself can contain a namespace inside it.
     */
    OMText createOMText(OMContainer parent, QName text);

    /**
     * @param parent
     * @param text
     * @param type   - this should be either of XMLStreamConstants.CHARACTERS,
     *               XMLStreamConstants.CDATA, XMLStreamConstants.SPACE, XMLStreamConstants.ENTITY_REFERENCE
     * @return Returns OMText.
     */
    OMText createOMText(OMContainer parent, String text, int type);

    OMText createOMText(OMContainer parent, char[] charArary, int type);

    /**
     * @param parent
     * @param text   - This text itself can contain a namespace inside it.
     * @param type
     */
    OMText createOMText(OMContainer parent, QName text, int type);

    /**
     * @param s
     * @return Returns OMText.
     */
    OMText createOMText(String s);

    /**
     * @param s
     * @param type - OMText node can handle SPACE, CHARACTERS, CDATA and ENTITY REFERENCES. For
     *             Constants, use either XMLStreamConstants or constants found in OMNode.
     * @return Returns OMText.
     */
    OMText createOMText(String s, int type);

    OMText createOMText(String s, String mimeType, boolean optimize);

    OMText createOMText(Object dataHandler, boolean optimize);

    OMText createOMText(OMContainer parent, String s, String mimeType,
                               boolean optimize);

    /**
     * Create a binary {@link OMText} node supporting deferred loading of the content.
     * 
     * @param contentID
     *            the content ID identifying the binary content; may be <code>null</code>
     * @param dataHandlerProvider
     *            used to load the {@link DataHandler} when requested from the returned
     *            {@link OMText} node
     * @param optimize
     *            determines whether the binary content should be optimized
     * @return
     */
    OMText createOMText(String contentID, DataHandlerProvider dataHandlerProvider,
            boolean optimize);

    OMText createOMText(String contentID, OMContainer parent,
                               OMXMLParserWrapper builder);

    OMAttribute createOMAttribute(String localName,
                                         OMNamespace ns,
                                         String value);

    /**
     * Creates DocType/DTD.
     *
     * @param parent
     * @param content
     * @return Returns doctype.
     */
    OMDocType createOMDocType(OMContainer parent, String content);

    /**
     * Creates a PI.
     *
     * @param parent
     * @param piTarget
     * @param piData
     * @return Returns OMProcessingInstruction.
     */
    OMProcessingInstruction createOMProcessingInstruction(OMContainer parent,
                                                                 String piTarget, String piData);

    /**
     * Creates a comment.
     *
     * @param parent
     * @param content
     * @return Returns OMComment.
     */
    OMComment createOMComment(OMContainer parent, String content);
}
