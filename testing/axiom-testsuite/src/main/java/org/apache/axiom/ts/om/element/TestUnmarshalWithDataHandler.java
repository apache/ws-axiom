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
package org.apache.axiom.ts.om.element;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.ds.jaxb.JAXBOMDataSource;
import org.apache.axiom.om.util.jaxb.JAXBUtils;
import org.apache.axiom.testutils.activation.TextDataSource;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.jaxb.beans.DocumentBean;

public class TestUnmarshalWithDataHandler extends AxiomTestCase {
    public TestUnmarshalWithDataHandler(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        JAXBContext context = JAXBContext.newInstance(DocumentBean.class);
        DocumentBean orgBean = new DocumentBean();
        orgBean.setId("AB23498");
        orgBean.setContent(new DataHandler(new TextDataSource("test content", "utf-8", "plain")));
        OMElement element = factory.createOMElement(new JAXBOMDataSource(context, orgBean));
        DocumentBean bean = (DocumentBean) JAXBUtils.unmarshal(element, context, null, true);
        assertEquals(orgBean.getId(), bean.getId());
        assertEquals(orgBean.getContent(), bean.getContent());
    }
}
