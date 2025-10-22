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

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

/**
 * Defines additional configuration options for {@link OMContainer#getXMLStreamReader(boolean,
 * OMXMLStreamReaderConfiguration)}.
 */
public class OMXMLStreamReaderConfiguration {
    private boolean preserveNamespaceContext;
    private boolean namespaceURIInterning;

    /**
     * Determine whether strict namespace preservation is enabled. See {@link
     * #setPreserveNamespaceContext(boolean)} for more information about this option.
     *
     * @return the current value of this option
     */
    public boolean isPreserveNamespaceContext() {
        return preserveNamespaceContext;
    }

    /**
     * Specify whether the namespace context determined by the ancestors of the element should be
     * preserved. This option only applies to {@link OMElement} instances and is disabled by
     * default.
     *
     * <p>When this option is enabled, the {@link XMLStreamReader#getNamespaceCount()}, {@link
     * XMLStreamReader#getNamespacePrefix(int)} and {@link XMLStreamReader#getNamespaceURI(int)}
     * will report additional namespace declarations for the {@link
     * XMLStreamConstants#START_ELEMENT} event corresponding to the element on which this method is
     * called, i.e. the root element of the resulting stream. These namespace declarations
     * correspond to namespaces declared by the ancestors of the element and that are visible in the
     * context of the element.
     *
     * <p>More precisely, if this option is enabled, then the namespace declarations reported for
     * the first {@link XMLStreamConstants#START_ELEMENT} event in the returned stream will be the
     * same as the declarations that would be returned by {@link OMElement#getNamespacesInScope()},
     * with the exception that a {@code xmlns=""} declaration present on the element will be
     * preserved.
     *
     * <p>This feature is useful for code that relies on the namespace declarations reported by the
     * {@link XMLStreamReader} to reconstruct the namespace context (instead of using the namespace
     * context provided by {@link XMLStreamReader#getNamespaceContext()}). An example helps to
     * illustrate how this works. Consider the following XML message:
     *
     * <pre>
     * &lt;soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
     *                   xmlns:xsd="http://www.w3.org/2001/XMLSchema"
     *                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"&gt;
     *   &lt;soapenv:Body&gt;
     *     &lt;ns:echo xmlns:ns="urn:test"&gt;
     *       &lt;in xsi:type="xsd:string"&gt;test&lt;/in&gt;
     *     &lt;/ns:echo&gt;
     *   &lt;/soapenv:Body&gt;
     * &lt;/soapenv:Envelope&gt;
     * </pre>
     *
     * <p>When {@link OMContainer#getXMLStreamReader(boolean)} is invoked on the {@link OMElement}
     * corresponding to {@code ns:echo}, only the namespace declaration for the {@code ns} prefix
     * will be reported. This may cause a problem when the caller attempts to resolve the QName
     * value {@code xsd:string} of the {@code xsi:type} attribute. If namespace context preservation
     * is enabled, then the {@link XMLStreamReader} returned by this method will generate additional
     * namespace declarations for the {@code soapenv}, {@code xsd} and {@code xsi} prefixes. They
     * are reported for the {@link XMLStreamConstants#START_ELEMENT} event representing the {@code
     * ns:echo} element.
     *
     * @param preserveNamespaceContext the value to set for this option
     */
    public void setPreserveNamespaceContext(boolean preserveNamespaceContext) {
        this.preserveNamespaceContext = preserveNamespaceContext;
    }

    /**
     * Determine whether namespace URIs returned by the {@link XMLStreamReader} should be interned.
     *
     * @return the current value of this option
     */
    public boolean isNamespaceURIInterning() {
        return namespaceURIInterning;
    }

    /**
     * Specify whether namespace URIs returned by the {@link XMLStreamReader} should be interned.
     * This applies to the return values of the following methods:
     *
     * <ul>
     *   <li>{@link XMLStreamReader#getAttributeNamespace(int)}
     *   <li>{@link XMLStreamReader#getNamespaceURI()}
     *   <li>{@link XMLStreamReader#getNamespaceURI(int)}
     *   <li>{@link XMLStreamReader#getNamespaceURI(String)}
     *   <li>{@link NamespaceContext#getNamespaceURI(String)} of the {@link NamespaceContext}
     *       instance returned by {@link XMLStreamReader#getNamespaceContext()}
     * </ul>
     *
     * @param namespaceURIInterning the value to set for this option
     * @see String#intern()
     */
    public void setNamespaceURIInterning(boolean namespaceURIInterning) {
        this.namespaceURIInterning = namespaceURIInterning;
    }
}
