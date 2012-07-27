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
package org.apache.axiom.om.ds.jaxb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.ds.jaxb.beans.DocumentBean;
import org.junit.Test;

public class JAXBOMDataSourceTest {
    /**
     * Tests the expansion of an {@link OMSourcedElement} backed by a {@link JAXBOMDataSource} with
     * a bean that has a {@link DataHandler}. The expansion should result in an {@link OMText} node
     * linked to that {@link DataHandler}.
     */
    @Test
    public void testDataHandlerExpansion() throws Exception {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        JAXBContext context = JAXBContext.newInstance(DocumentBean.class);
        DataHandler dh = new DataHandler("some content", "text/plain");
        DocumentBean object = new DocumentBean();
        object.setId("123456");
        object.setContent(dh);
        OMElement element = factory.createOMElement(new JAXBOMDataSource(context, object));
        OMElement child = (OMElement)element.getFirstOMChild();
        assertEquals("id", child.getLocalName());
        assertEquals("123456", child.getText());
        child = (OMElement)child.getNextOMSibling();
        assertEquals("content", child.getLocalName());
        OMText content = (OMText)child.getFirstOMChild();
        assertTrue(content.isBinary());
        assertTrue(content.isOptimized());
        assertSame(dh, content.getDataHandler());
    }
}
