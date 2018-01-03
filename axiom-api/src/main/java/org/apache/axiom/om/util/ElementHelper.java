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

package org.apache.axiom.om.util;

import org.apache.axiom.blob.Blobs;
import org.apache.axiom.blob.MemoryBlob;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMInformationItem;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.ds.BlobOMDataSource;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeaderBlock;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.Iterator;

/**
 * Helper class to provide extra utility stuff against elements. The code is designed to work with
 * any element implementation.
 */

public class ElementHelper {

    private OMElement element;

    /**
     * Constructs and binds to an element.
     *
     * @param element element to work with
     */
    public ElementHelper(OMElement element) {
        this.element = element;
    }

    /**
     * @deprecated The algorithm used by this method is incorrect. See <a
     *             href="https://issues.apache.org/jira/browse/AXIOM-356">AXIOM-356</a> for more
     *             details.
     */
    public QName resolveQName(String qname, boolean defaultToParentNameSpace) {
        int colon = qname.indexOf(':');
        if (colon < 0) {
            if (defaultToParentNameSpace) {
                //get the parent ns and use it for the child
                OMNamespace namespace = element.getNamespace();
                if (namespace != null) {
                    // Guard against QName implementation sillyness.
                    if (namespace.getPrefix() == null)
                        return new QName(namespace.getNamespaceURI(), qname);
                    else
                        return new QName(namespace.getNamespaceURI(), qname, namespace.getPrefix());
                }
            }
            //else things without no prefix are local.
            return new QName(qname);
        }
        String prefix = qname.substring(0, colon);
        String local = qname.substring(colon + 1);
        if (local.length() == 0) {
            //empy local, exit accordingly
            return null;
        }

        OMNamespace namespace = element.findNamespaceURI(prefix);
        if (namespace == null) {
            return null;
        }
        return new QName(namespace.getNamespaceURI(), local, prefix);
    }

    /**
     * @deprecated The algorithm used by this method is incorrect. See <a
     *             href="https://issues.apache.org/jira/browse/AXIOM-356">AXIOM-356</a> for more
     *             details.
     */
    public QName resolveQName(String qname) {
        return resolveQName(qname, true);
    }

    /**
     * @deprecated
     */
    public static void setNewElement(OMElement parent,
                                     OMElement myElement,
                                     OMElement newElement) {
        if (myElement != null) {
            myElement.discard();
        }
        parent.addChild(newElement);
    }

    /**
     * @deprecated please use OMElement.getFirstChildWithName(qname) instead!
     */
    public static OMElement getChildWithName(OMElement parent,
                                             String childName) {
        Iterator childrenIter = parent.getChildren();
        while (childrenIter.hasNext()) {
            OMNode node = (OMNode) childrenIter.next();
            if (node.getType() == OMNode.ELEMENT_NODE &&
                    childName.equals(((OMElement) node).getLocalName())) {
                return (OMElement) node;
            }
        }
        return null;
    }

    /**
     * @deprecated use {@link #getContentID(XMLStreamReader)} instead (see AXIOM-129)
     */
    public static String getContentID(XMLStreamReader parser, String charsetEncoding) {
        return getContentID(parser);
    }

    public static String getContentID(XMLStreamReader parser) {
        if (parser.getAttributeCount() > 0 &&
                parser.getAttributeLocalName(0).equals("href")) {
            return getContentIDFromHref(parser.getAttributeValue(0));
        } else {
            throw new OMException(
                    "Href attribute not found in XOP:Include element");
        }
    }

    /**
     * Extract the content ID from a href attribute value, i.e. from a URI following the
     * cid: scheme defined by RFC2392.
     * 
     * @param href the value of the href attribute
     * @return the corresponding content ID
     */
    public static String getContentIDFromHref(String href) {
        if (href.startsWith("cid:")) {
            try {
                // URIs should always be decoded using UTF-8 (see AXIOM-129). On the
                // other hand, since non ASCII characters are not allowed in content IDs,
                // we can simply decode using ASCII (which is a subset of UTF-8)
                return URLDecoder.decode(href.substring(4), "ascii");
            } catch (UnsupportedEncodingException ex) {
                // We should never get here
                throw new Error(ex);
            }
        } else {
            throw new IllegalArgumentException("The URL doesn't use the cid scheme");
        }
    }
    
    /**
     * Some times two OMElements needs to be added to the same object tree. But in Axiom, a single
     * tree should always contain object created from the same type of factory (eg:
     * LinkedListImplFactory, DOMFactory, etc.,). If one OMElement is created from a different
     * factory than that of the factory which was used to create the object in the existing tree, we
     * need to convert the new OMElement to match to the factory of existing object tree. This
     * method will convert omElement to the given omFactory.
     *
     * @see AttributeHelper#importOMAttribute(OMAttribute, OMElement) to convert instances of
     *      OMAttribute
     * 
     * @deprecated Use {@link OMFactory#importInformationItem(OMInformationItem)} instead.
     */
    public static OMElement importOMElement(OMElement omElement, OMFactory omFactory) {
        // first check whether the given OMElement has the same omFactory
        if (omElement.getOMFactory().getMetaFactory() == omFactory.getMetaFactory()) {
            return omElement;
        } else {
            return (OMElement)omFactory.importInformationItem(omElement);
        }
    }

    /**
     * This is a method to convert regular OMElements to SOAPHeaderBlocks.
     * 
     * @param omElement
     * @param factory
     * @return TODO
     * @throws Exception
     * 
     * @deprecated Use {@link SOAPFactory#createSOAPHeaderBlock(OMElement)} instead.
     */
    public static SOAPHeaderBlock toSOAPHeaderBlock(OMElement omElement, SOAPFactory factory) throws Exception {
        if (omElement instanceof SOAPHeaderBlock)
            return (SOAPHeaderBlock) omElement;
        
        QName name = omElement.getQName();
        String localName = name.getLocalPart();
        OMNamespace namespace = factory.createOMNamespace(name.getNamespaceURI(), name.getPrefix());
        MemoryBlob blob = Blobs.createMemoryBlob();
        OutputStream out = blob.getOutputStream();
        omElement.serialize(out);
        out.close();
        BlobOMDataSource ds = new BlobOMDataSource(blob, "utf-8");
        SOAPHeaderBlock block = factory.createSOAPHeaderBlock(localName, namespace, ds);
        
        return block;
    }
    
    /**
     * @deprecated Use {@link OMElement#getTextAsStream(boolean)} instead.
     */
    public static Reader getTextAsStream(OMElement element, boolean cache) {
        return element.getTextAsStream(cache);
    }
    
    /**
     * @deprecated Use {@link OMElement#writeTextTo(Writer, boolean)} instead.
     */
    public static void writeTextTo(OMElement element, Writer out, boolean cache)
            throws XMLStreamException, IOException {
        element.writeTextTo(out, cache);
    }
}
