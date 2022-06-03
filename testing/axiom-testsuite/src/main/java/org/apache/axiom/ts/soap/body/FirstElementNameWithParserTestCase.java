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
package org.apache.axiom.ts.soap.body;

import static com.google.common.truth.Truth.assertThat;

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.ds.AbstractPushOMDataSource;
import org.apache.axiom.om.ds.custombuilder.CustomBuilder;
import org.apache.axiom.om.ds.custombuilder.CustomBuilderSupport;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

public abstract class FirstElementNameWithParserTestCase extends SOAPTestCase {
    protected final QName qname;
    private final boolean supportsOptimization;

    public FirstElementNameWithParserTestCase(
            OMMetaFactory metaFactory, SOAPSpec spec, QName qname, boolean supportsOptimization) {
        super(metaFactory, spec);
        this.qname = qname;
        addTestParameter("prefix", qname.getPrefix());
        addTestParameter("uri", qname.getNamespaceURI());
        addTestParameter("localName", qname.getLocalPart());
        this.supportsOptimization = supportsOptimization;
    }

    @Override
    protected final void runTest() throws Throwable {
        SOAPEnvelope orgEnvelope = soapFactory.getDefaultEnvelope();
        orgEnvelope
                .getBody()
                .addChild(
                        soapFactory.createOMElement(
                                qname.getLocalPart(), qname.getNamespaceURI(), qname.getPrefix()));
        SOAPModelBuilder builder =
                OMXMLBuilderFactory.createSOAPModelBuilder(
                        metaFactory, new StringReader(orgEnvelope.toString()));
        SOAPBody body = builder.getSOAPEnvelope().getBody();
        runTest(body);
        if (supportsOptimization) {
            // The expectation is that even after looking at the payload element name, registering
            // a custom builder still transforms the element.
            ((CustomBuilderSupport) builder)
                    .registerCustomBuilder(
                            CustomBuilder.Selector.PAYLOAD,
                            new CustomBuilder() {
                                @Override
                                public OMDataSource create(OMElement element) throws OMException {
                                    try {
                                        element.getXMLStreamReaderWithoutCaching().close();
                                    } catch (XMLStreamException ex) {
                                        throw new OMException(ex);
                                    }
                                    return new AbstractPushOMDataSource() {
                                        @Override
                                        public void serialize(XMLStreamWriter xmlWriter)
                                                throws XMLStreamException {
                                            xmlWriter.writeEmptyElement(
                                                    qname.getPrefix(),
                                                    qname.getLocalPart(),
                                                    qname.getNamespaceURI());
                                        }

                                        @Override
                                        public boolean isDestructiveWrite() {
                                            return false;
                                        }
                                    };
                                }
                            });
            assertThat(body.getFirstElement()).isInstanceOf(OMSourcedElement.class);
        }
    }

    protected abstract void runTest(SOAPBody body) throws Throwable;
}
