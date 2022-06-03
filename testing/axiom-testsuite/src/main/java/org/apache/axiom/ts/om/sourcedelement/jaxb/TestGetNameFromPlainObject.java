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

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.jaxb.JAXBOMDataSource;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.jaxb.beans.DocumentBean;

/**
 * Tests that {@link JAXBOMDataSource} backed by a plain Java bean is able to determine the
 * namespace URI and local name of the element without expansion.
 */
public class TestGetNameFromPlainObject extends AxiomTestCase {
    public TestGetNameFromPlainObject(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory omFactory = metaFactory.getOMFactory();
        JAXBContext context = JAXBContext.newInstance(DocumentBean.class);
        OMSourcedElement element =
                omFactory.createOMElement(new JAXBOMDataSource(context, new DocumentBean()));
        assertEquals("http://ws.apache.org/axiom/test/jaxb", element.getNamespaceURI());
        assertEquals("document", element.getLocalName());
        assertFalse(element.isExpanded());
        // Force expansion so that OMSourcedElement compares the namespace URI and local name
        // provided by JAXBOMDataSource with the actual name of the element
        element.getFirstOMChild();
    }
}
