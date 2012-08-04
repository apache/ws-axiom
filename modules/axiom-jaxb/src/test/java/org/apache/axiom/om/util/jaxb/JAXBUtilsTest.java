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
package org.apache.axiom.om.util.jaxb;

import static org.junit.Assert.assertEquals;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.ds.jaxb.JAXBOMDataSource;
import org.apache.axiom.om.ds.jaxb.beans.DocumentBean;
import org.junit.Test;

public class JAXBUtilsTest {
    @Test
    public void testUnmarshalWithDataHandler() throws Exception {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        JAXBContext context = JAXBContext.newInstance(DocumentBean.class);
        DocumentBean orgBean = new DocumentBean();
        orgBean.setId("AB23498");
        orgBean.setContent(new DataHandler("test content", "text/plain"));
        OMElement element = factory.createOMElement(new JAXBOMDataSource(context, orgBean));
        DocumentBean bean = (DocumentBean)JAXBUtils.unmarshal(context, element, true);
        assertEquals(orgBean.getId(), bean.getId());
        assertEquals(orgBean.getContent(), bean.getContent());
    }
}
