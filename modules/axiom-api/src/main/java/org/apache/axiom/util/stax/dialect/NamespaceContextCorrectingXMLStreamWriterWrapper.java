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

package org.apache.axiom.util.stax.dialect;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.util.namespace.ScopedNamespaceContext;
import org.apache.axiom.util.stax.wrapper.XMLStreamWriterWrapper;

/**
 * {@link XMLStreamWriter} wrapper that handles namespace bindings on behalf of the underlying
 * writer. This wrapper can be used to correct two issues found in some stream writer
 * implementations:
 * <ol>
 *   <li>The writer doesn't correctly scope the namespace bindings. According to the StAX
 *       specifications, the scope of a namespace binding defined using
 *       {@link XMLStreamWriter#setPrefix(String, String)} or
 *       {@link XMLStreamWriter#setDefaultNamespace(String)} is limited to
 *       "the current <tt>START_ELEMENT</tt> / <tt>END_ELEMENT</tt> pair". Some implementations
 *       such as early versions of XL XP-J don't satisfy this requirement.
 *   <li>The writer doesn't handle masked prefixes correctly. To ensure consistent behavior
 *       in the presence of masked prefixes, the {@link XMLStreamWriter#getPrefix(String)} method
 *       (and the corresponding methods in the namespace context returned by
 *       {@link XMLStreamWriter#getNamespaceContext()}) must not return a prefix that
 *       is bound to a different namespace URI in a nested scope. Some implementations such as
 *       the StAX reference implementation fail to meet this requirement.
 * </ol>
 * <p>
 * Invocations of the following methods will be completely processed by the wrapper, and will never
 * reach the underlying writer:
 * <ul>
 *   <li>{@link XMLStreamWriter#getNamespaceContext()}
 *   <li>{@link XMLStreamWriter#setNamespaceContext(NamespaceContext)}
 *   <li>{@link XMLStreamWriter#getPrefix(String)}
 *   <li>{@link XMLStreamWriter#setDefaultNamespace(String)}
 *   <li>{@link XMLStreamWriter#setPrefix(String, String)}
 * </ul>
 * <p>
 * The following methods rely on information from the namespace context to choose a the namespace
 * prefix; the wrapper redirects invocations of these methods to the corresponding variants taking
 * an explicit prefix parameter:
 * <ul>
 *   <li>{@link XMLStreamWriter#writeStartElement(String, String)}
 *   <li>{@link XMLStreamWriter#writeAttribute(String, String, String)}
 *   <li>{@link XMLStreamWriter#writeEmptyElement(String, String)}
 * </ul>
 * <p>
 * This implies that if the wrapper is used, these methods will never be called on the underlying
 * writer.
 */
public class NamespaceContextCorrectingXMLStreamWriterWrapper extends XMLStreamWriterWrapper {
    private final ScopedNamespaceContext namespaceContext = new ScopedNamespaceContext();

    public NamespaceContextCorrectingXMLStreamWriterWrapper(XMLStreamWriter parent) {
        super(parent);
    }

    public NamespaceContext getNamespaceContext() {
        return namespaceContext;
    }

    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        // TODO: not sure yet how to implement this method
        throw new UnsupportedOperationException();
    }

    public String getPrefix(String uri) throws XMLStreamException {
        return namespaceContext.getPrefix(uri);
    }

    public void setDefaultNamespace(String uri) throws XMLStreamException {
        namespaceContext.setPrefix("", uri);
    }

    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        namespaceContext.setPrefix(prefix, uri);
    }

    public String internalGetPrefix(String namespaceURI) throws XMLStreamException {
        String prefix = namespaceContext.getPrefix(namespaceURI);
        if (prefix == null) {
            throw new XMLStreamException("Unbound namespace URI '" + namespaceURI + "'");
        } else {
            return prefix;
        }
    }
    
    public void writeStartElement(String prefix, String localName, String namespaceURI)
            throws XMLStreamException {
        super.writeStartElement(prefix, localName, namespaceURI);
        namespaceContext.startScope();
    }

    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        super.writeStartElement(internalGetPrefix(namespaceURI), namespaceURI, localName);
    }

    public void writeStartElement(String localName) throws XMLStreamException {
        super.writeStartElement(localName);
        namespaceContext.startScope();
    }

    public void writeEndElement() throws XMLStreamException {
        super.writeEndElement();
        namespaceContext.endScope();
    }

    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        super.writeEmptyElement(internalGetPrefix(namespaceURI), namespaceURI, localName);
    }

    public void writeAttribute(String namespaceURI, String localName, String value)
            throws XMLStreamException {
        super.writeAttribute(internalGetPrefix(namespaceURI), namespaceURI, localName, value);
    }
}
