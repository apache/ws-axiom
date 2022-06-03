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
package org.apache.axiom.ts.om.sourcedelement.jaxb;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.core.stream.stax.StAX;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.jaxb.JAXBOMDataSource;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.jaxb.beans.DocumentBean;

/**
 * Tests that an {@link XMLStreamException} thrown by the {@link XMLStreamWriter} during
 * serialization is propagated without being wrapped. Note that this implies that the data must
 * unwrap {@link JAXBException} to extract the cause.
 */
public class TestExceptionDuringSerialization extends AxiomTestCase {
    public TestExceptionDuringSerialization(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory omFactory = metaFactory.getOMFactory();
        JAXBContext context = JAXBContext.newInstance(DocumentBean.class);
        DocumentBean object = new DocumentBean();
        object.setId("test");
        OMSourcedElement element = omFactory.createOMElement(new JAXBOMDataSource(context, object));
        XMLStreamException exception = new XMLStreamException("TEST");
        try {
            element.serialize(
                    new ExceptionXMLStreamWriterWrapper(
                            StAX.createNullXMLStreamWriter(), exception));
            fail("Expected XMLStreamException");
        } catch (XMLStreamException ex) {
            assertSame(exception, ex);
        }
    }
}
