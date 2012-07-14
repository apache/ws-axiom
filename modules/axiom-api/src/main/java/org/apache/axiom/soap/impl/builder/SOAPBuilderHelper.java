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

package org.apache.axiom.soap.impl.builder;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.exception.OMBuilderException;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPProcessingException;

import javax.xml.stream.XMLStreamReader;

public abstract class SOAPBuilderHelper {
    protected final SOAPFactoryEx factory;
    protected final StAXSOAPModelBuilder builder;
    protected XMLStreamReader parser;

    protected SOAPBuilderHelper(StAXSOAPModelBuilder builder, SOAPFactoryEx factory) {
        this.builder = builder;
        this.factory = factory;
    }

    public abstract OMElement handleEvent(XMLStreamReader parser,
                                          OMElement element,
                                          int elementLevel) throws SOAPProcessingException;

    protected void processNamespaceData(OMElement node, boolean checkSOAPNamespace) {
        builder.processNamespaceData(node);

        // TODO we got to have this to make sure OM reject mesagess that are not name space qualified
        // But got to comment this to interop with Axis.1.x
        // if (namespace == null) {
        // throw new OMException("All elements must be namespace qualified!");
        // }
        if (checkSOAPNamespace) {
            if (node.getNamespace() != null &&
                    !node.getNamespace().getNamespaceURI().equals(
                            SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI) &&
                    !node.getNamespace().getNamespaceURI().equals(
                            SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI)) {
                throw new OMBuilderException("invalid SOAP namespace URI");
            }
        }

    }

    protected void processAttributes(OMElement node) {
        int attribCount = parser.getAttributeCount();
        for (int i = 0; i < attribCount; i++) {
            OMNamespace ns = null;
            String uri = parser.getAttributeNamespace(i);
            if (uri != null && uri.hashCode() != 0) {
                ns = node.findNamespace(uri,
                                        parser.getAttributePrefix(i));
            }

            // todo if the attributes are supposed to namespace qualified all the time
            // todo then this should throw an exception here
            node.addAttribute(parser.getAttributeLocalName(i),
                              parser.getAttributeValue(i), ns);
        }
    }
}
