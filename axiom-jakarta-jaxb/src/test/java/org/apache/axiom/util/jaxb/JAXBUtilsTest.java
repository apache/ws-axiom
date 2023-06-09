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
package org.apache.axiom.util.jaxb;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;

import javax.xml.namespace.QName;

import org.apache.axiom.jaxb.DocumentBean;
import org.apache.axiom.jaxb.DocumentBean2;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.ds.jaxb.JAXBOMDataSource;
import org.apache.axiom.om.util.jaxb.JAXBUtils;
import org.apache.axiom.testutils.blob.TextBlob;
import org.junit.jupiter.api.Test;

import jakarta.activation.DataHandler;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;

public class JAXBUtilsTest {
    @Test
    public void testUnmarshalWithDataHandler() throws Exception {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        JAXBContext context = JAXBContext.newInstance(DocumentBean.class);
        DocumentBean orgBean = new DocumentBean();
        orgBean.setId("AB23498");
        orgBean.setContent(new DataHandler("test content", "text/plain"));
        OMElement element = factory.createOMElement(new JAXBOMDataSource(context, orgBean));
        DocumentBean bean = (DocumentBean) JAXBUtils.unmarshal(element, context, null, true);
        assertThat(bean.getId()).isEqualTo(orgBean.getId());
        assertThat(bean.getContent()).isSameAs(orgBean.getContent());
    }

    @Test
    public void testUnmarshalWithDataHandlerToByteArray() throws Exception {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace("http://ws.apache.org/axiom/test/jaxb", "p");
        OMElement element = factory.createOMElement("document2", ns);
        factory.createOMElement("id", ns, element).setText("12345");
        OMElement content = factory.createOMElement("content", ns, element);
        content.addChild(
                factory.createOMText(new TextBlob("test content", StandardCharsets.UTF_8), true));
        JAXBContext context = JAXBContext.newInstance(DocumentBean2.class);
        DocumentBean2 bean = (DocumentBean2) JAXBUtils.unmarshal(element, context, null, true);
        assertThat(bean.getId()).isEqualTo("12345");
        assertThat(bean.getContent()).isEqualTo("test content".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testUnmarshalWithDeclaredType() throws Exception {
        OMElement element = OMAbstractFactory.getOMFactory().createOMElement("foo", null);
        element.setText("bar");
        JAXBElement<String> result =
                JAXBUtils.unmarshal(element, JAXBContext.newInstance(), null, String.class, true);
        assertThat(result.getName()).isEqualTo(new QName("foo"));
        assertThat(result.getValue()).isEqualTo("bar");
    }
}
