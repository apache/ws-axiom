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

package org.apache.axiom.om.ds.custombuilder;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPBody;

import javax.xml.stream.XMLStreamReader;

/**
 * A Custom Builder is registered on the {@link OMXMLParserWrapper} for a particular set of elements.
 * When a matching element is encountered, the CustomBuilder will build an {@link OMDataSource}
 * for the builder.
 *
 * @see CustomBuilderSupport#registerCustomBuilder(Selector, CustomBuilder)
 */
public interface CustomBuilder {
    /**
     * Selects the elements to which a custom builder is applied. Note that this interface may be
     * implemented by the {@link CustomBuilder} itself.
     */
    public interface Selector {
        /**
         * Selects the message payload element. For plain XML documents, that is the document
         * element. For SOAP messages, that is the child element of the SOAP body.
         */
        Selector PAYLOAD = new Selector() {
            @Override
            public boolean accepts(OMContainer parent, int depth, String namespaceURI, String localName) {
                // Note: usage of SOAPBody here may create a package cycle, but that cycle could easily
                // be broken by introducing a marker interface to be extended by SOAPBody.
                return parent instanceof OMDocument || parent instanceof SOAPBody;
            }
        };
        
        /**
         * Check if the custom builder registered with this selector should be applied to the given
         * element. Note that this method will only be invoked for elements that can be represented
         * as {@link OMSourcedElement} instances in the object model. For plain XML documents this
         * means every element, but for SOAP messages this restricts the set of elements. E.g. this
         * method will never be invoked for SOAP faults.
         * 
         * @param parent
         *            the parent of the {@link OMElement} to be built
         * @param depth
         *            the depth of the element (with the root element having depth 1)
         * @param namespaceURI
         *            the namespace URI of the element; never {@code null}
         * @param localName
         *            the local name of the element; never {@code null}
         * @return {@code true} if the element should be built as an {@link OMSourcedElement} using
         *         the custom builder registered with this selector, in which case
         *         {@link CustomBuilder#create(XMLStreamReader)} will be called to create the
         *         corresponding {@link OMDataSource}; {@code false} otherwise
         */
        boolean accepts(OMContainer parent, int depth, String namespaceURI, String localName);
    }
    
    /**
     * Create an {@link OMDataSource} for this whole subtree.
     * 
     * @param reader
     *            The stream reader to read the StAX events from. The data read
     *            from this reader always represents plain XML, even if the
     *            original document was XOP encoded. The reader optionally
     *            implements the {@link org.apache.axiom.ext.stax.datahandler.DataHandlerReader}
     *            extension to give the custom builder access to optimized
     *            binary data. This is appropriate for custom builders that
     *            support {@link org.apache.axiom.ext.stax.datahandler.DataHandlerReader}
     *            or in cases where there is no other option than to transfer
     *            binary data as base64 encoded character data.
     *            <p>
     *            However, if the custom builder interacts with a third party
     *            library that supports XOP, it may want to use that encoding
     *            to optimize the transfer of binary data. To do so, the
     *            custom builder MUST use {@link org.apache.axiom.util.stax.xop.XOPUtils#getXOPEncodedStream(XMLStreamReader)}
     *            to get an XOP encoded stream.
     * @return OMDataSource
     */
    public OMDataSource create(XMLStreamReader reader) throws OMException;
}
