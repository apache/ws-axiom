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
package org.apache.axiom.ts.om.sourcedelement;

import static com.google.common.truth.Truth.assertThat;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.sax.SAXSource;

import org.apache.axiom.core.stream.sax.SAX;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.ds.AbstractPushOMDataSource;
import org.apache.axiom.ts.AxiomTestCase;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class TestGetSAXSourceWithPushOMDataSourceThrowingException extends AxiomTestCase {
    public TestGetSAXSourceWithPushOMDataSourceThrowingException(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMElement element =
                metaFactory
                        .getOMFactory()
                        .createOMElement(
                                new AbstractPushOMDataSource() {

                                    @Override
                                    public void serialize(XMLStreamWriter xmlWriter)
                                            throws XMLStreamException {
                                        throw new XMLStreamException("TEST");
                                    }

                                    @Override
                                    public boolean isDestructiveWrite() {
                                        return false;
                                    }
                                });
        SAXSource saxSource = element.getSAXSource(true);
        XMLReader reader = saxSource.getXMLReader();
        reader.setContentHandler(SAX.createNullContentHandler());
        try {
            reader.parse(saxSource.getInputSource());
            fail("Expected SAXException");
        } catch (SAXException ex) {
            Throwable cause = ex.getCause();
            assertThat(cause).isInstanceOf(XMLStreamException.class);
            assertThat(cause.getMessage()).isEqualTo("TEST");
        }
    }
}
