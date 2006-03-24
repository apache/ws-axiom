/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axiom.om.impl.llom.factory;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocType;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.llom.OMAttributeImpl;
import org.apache.axiom.om.impl.llom.OMCommentImpl;
import org.apache.axiom.om.impl.llom.OMDocTypeImpl;
import org.apache.axiom.om.impl.llom.OMDocumentImpl;
import org.apache.axiom.om.impl.llom.OMElementImpl;
import org.apache.axiom.om.impl.llom.OMNamespaceImpl;
import org.apache.axiom.om.impl.llom.OMProcessingInstructionImpl;
import org.apache.axiom.om.impl.llom.OMTextImpl;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;

/**
 * Class OMLinkedListImplFactory
 */
public class OMLinkedListImplFactory implements OMFactory {

    private static final String uriAndPrefixSeparator = ";";
    /**
     * This is a map of namespaces with the namespace URI as the key and
     * Namespace object itself as the value.
     */
    protected Map namespaceTable = new HashMap(5);

    /**
     * Method createOMElement.
     *
     * @param localName
     * @param ns
     * @return Returns OMElement.
     */
    public OMElement createOMElement(String localName, OMNamespace ns) {
        return new OMElementImpl(localName, ns, this);
    }

    public OMElement createOMElement(String localName, OMNamespace ns, OMContainer parent) {
        return new OMElementImpl(localName, ns, parent, this);
    }

    /**
     * Method createOMElement.
     *
     * @param localName
     * @param ns
     * @param parent
     * @param builder
     * @return Returns OMElement.
     */
    public OMElement createOMElement(String localName, OMNamespace ns,
                                     OMContainer parent,
                                     OMXMLParserWrapper builder) {
        return new OMElementImpl(localName, ns, parent,
                builder, this);
    }

    /**
     * Method createOMElement.
     *
     * @param localName
     * @param namespaceURI
     * @param namespacePrefix
     * @return Returns OMElement.
     */
    public OMElement createOMElement(String localName, String namespaceURI,
                                     String namespacePrefix) {
        return this.createOMElement(localName,
                this.createOMNamespace(namespaceURI,
                        namespacePrefix));
    }

    /**
     * Method createOMElement.
     *
     * @param qname
     * @param parent
     * @return Returns OMElement.
     * @throws OMException
     */
    public OMElement createOMElement(QName qname, OMContainer parent)
            throws OMException {
        return new OMElementImpl(qname, parent, this);
    }

    /**
     * Method createOMNamespace.
     *
     * @param uri
     * @param prefix
     * @return Returns OMNamespace.
     */
    public OMNamespace createOMNamespace(String uri, String prefix) {
        String key = uri + uriAndPrefixSeparator + prefix;
        OMNamespace existingNamespaceObject = (OMNamespace) namespaceTable.get(key);
        if (existingNamespaceObject == null) {
            existingNamespaceObject = new OMNamespaceImpl(uri, prefix, this);
            namespaceTable.put(key, existingNamespaceObject);
        }
        return existingNamespaceObject;
    }

    /**
     * Method createText.
     *
     * @param parent
     * @param text
     * @return Returns OMText.
     */
    public OMText createText(OMElement parent, String text) {
        return new OMTextImpl(parent, text, this);
    }

    public OMText createText(OMElement parent, String text, int type) {
        return new OMTextImpl(parent, text, type, this);
    }

    /**
     * Method createText.
     *
     * @param s
     * @return Returns OMText.
     */
    public OMText createText(String s) {
        return new OMTextImpl(s, this);
    }

    public OMText createText(String s, int type) {
        return new OMTextImpl(s, type, this);
    }

    /**
     * Creates text.
     *
     * @param s
     * @param mimeType
     * @param optimize
     * @return Returns OMText.
     */
    public OMText createText(String s, String mimeType, boolean optimize) {
        return new OMTextImpl(s, mimeType, optimize, this);
    }

    /**
     * Creates text.
     *
     * @param dataHandler
     * @param optimize
     * @return Returns OMText.
     */
    public OMText createText(Object dataHandler, boolean optimize) {
        return new OMTextImpl(dataHandler, optimize, this);
    }

    public OMText createText(String contentID, OMElement parent,
                             OMXMLParserWrapper builder) {
        return new OMTextImpl(contentID, parent, builder, this);
    }

    /**
     * Creates text.
     *
     * @param parent
     * @param s
     * @param mimeType
     * @param optimize
     * @return Returns OMText.
     */
    public OMText createText(OMElement parent,
                             String s,
                             String mimeType,
                             boolean optimize) {
        return new OMTextImpl(parent, s, mimeType, optimize, this);
    }

    /**
     * Creates attribute.
     *
     * @param localName
     * @param ns
     * @param value
     * @return Returns OMAttribute.
     */
    public OMAttribute createOMAttribute(String localName,
                                         OMNamespace ns,
                                         String value) {
        return new OMAttributeImpl(localName, ns, value, this);
    }

    /**
     * Creates DocType/DTD.
     *
     * @param parent
     * @param content
     * @return Returns doctype.
     */
    public OMDocType createOMDocType(OMContainer parent, String content) {
        return new OMDocTypeImpl(parent, content, this);
    }

    /**
     * Creates a PI.
     *
     * @param parent
     * @param piTarget
     * @param piData
     * @return Returns OMProcessingInstruction.
     */
    public OMProcessingInstruction createOMProcessingInstruction(OMContainer parent, String piTarget, String piData) {
        return new OMProcessingInstructionImpl(parent, piTarget, piData, this);
    }

    /**
     * Creates a comment.
     *
     * @param parent
     * @param content
     * @return Returns OMComment.
     */
    public OMComment createOMComment(OMContainer parent, String content) {
        return new OMCommentImpl(parent, content, this);
    }

    /* (non-Javadoc)
    * @see org.apache.axiom.om.OMFactory#createOMDocument()
    */
    public OMDocument createOMDocument() {
        return new OMDocumentImpl();
    }

    /* (non-Javadoc)
      * @see org.apache.axiom.om.OMFactory#createOMDocument(org.apache.axiom.om.OMXMLParserWrapper)
      */
    public OMDocument createOMDocument(OMXMLParserWrapper builder) {
        return new OMDocumentImpl(builder);
    }
}
