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

import static com.google.common.truth.Truth.assertThat;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.util.jaxb.JAXBUtils;
import org.apache.axiom.testutils.activation.TextDataSource;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.jaxb.beans.DocumentBean2;

public class TestUnmarshalWithDataHandlerToByteArray extends AxiomTestCase {
    public TestUnmarshalWithDataHandlerToByteArray(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace("http://ws.apache.org/axiom/test/jaxb", "p");
        OMElement element = factory.createOMElement("document2", ns);
        factory.createOMElement("id", ns, element).setText("12345");
        OMElement content = factory.createOMElement("content", ns, element);
        content.addChild(
                factory.createOMText(
                        new DataHandler(new TextDataSource("test content", "utf-8", "plain")),
                        true));
        JAXBContext context = JAXBContext.newInstance(DocumentBean2.class);
        DocumentBean2 bean = (DocumentBean2) JAXBUtils.unmarshal(element, context, null, true);
        assertThat(bean.getId()).isEqualTo("12345");
        assertThat(bean.getContent()).isEqualTo("test content".getBytes("utf-8"));
    }
}
