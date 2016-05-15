/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id$
 */
package org.apache.axiom.core.stream.serializer;

import javax.xml.transform.SourceLocator;

import org.apache.axiom.core.stream.StreamException;

/**
 * This interface describes extensions to the SAX ContentHandler interface.
 * It is intended to be used by a serializer. The methods on this interface will
 * implement SAX- like behavior. This allows the gradual collection of
 * information rather than having it all up front. For example the call
 * <pre>
 * startElement(namespaceURI,localName,qName,atts)
 * </pre>
 * could be replaced with the calls
 * <pre>
 * startElement(namespaceURI,localName,qName)
 * addAttributes(atts)
 * </pre>
 * If there are no attributes the second call can be dropped. If attributes are
 * to be added one at a time with calls to
 * <pre>
 * addAttribute(namespaceURI, localName, qName, type, value)
 * </pre>
 * @xsl.usage internal
 */
public interface ExtendedContentHandler
{
    /**
     * This method is used to notify of a character event, but passing the data
     * as a character String rather than the standard character array.
     * @param chars the character data
     * @throws StreamException
     */
    public void characters(String chars) throws StreamException;
    
    /**
     * This method is used to notify that an element has ended. Unlike the
     * standard SAX method
     * <pre>
     * endElement(namespaceURI,localName,qName)
     * </pre>
     * only the last parameter is passed. If needed the serializer can derive
     * the localName from the qualified name and derive the namespaceURI from
     * its implementation.
     * @param elemName the fully qualified element name.
     * @throws StreamException
     */
    public void endElement(String elemName) throws StreamException;

    /**
     * This method is used to notify that an element is starting.
     * This method is just like the standard SAX method
     * <pre>
     * startElement(uri,localName,qname,atts)
     * </pre>
     * but without the attributes.
     * @param uri the namespace URI of the element
     * @param localName the local name (without prefix) of the element
     * @param qName the qualified name of the element
     * 
     * @throws StreamException
     */
    public void startElement(String uri, String localName, String qName)
        throws StreamException;

    /**
     * This method is used to notify of the start of an element
     * @param qName the fully qualified name of the element
     * @throws StreamException
     */
    public void startElement(String qName) throws StreamException;

    /**
     * Notify of an entity reference.
     * @param entityName the name of the entity
     * @throws StreamException
     */
    public void entityReference(String entityName) throws StreamException;

    /**
     * This method is used to set the source locator, which might be used to
     * generated an error message.
     * @param locator the source locator
     */
    public void setSourceLocator(SourceLocator locator);

    // Bit constants for addUniqueAttribute().
    
    // The attribute value contains no bad characters. A "bad" character is one which
    // is greater than 126 or it is one of '<', '>', '&' or '"'.
    public static final int NO_BAD_CHARS = 0x1;
    
    // An HTML empty attribute (e.g. <OPTION selected>).
    public static final int HTML_ATTREMPTY = 0x2;
    
    // An HTML URL attribute
    public static final int HTML_ATTRURL = 0x4;

    public void characters(char chars[], int start, int length)
            throws StreamException;
}
