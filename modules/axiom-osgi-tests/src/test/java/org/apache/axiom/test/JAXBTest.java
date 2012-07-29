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
package org.apache.axiom.test;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.jaxb.JAXBOMDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.MavenConfiguredJUnit4TestRunner;

/**
 * Tests that the axiom-jaxb bundle can be loaded successfully and is operational.
 */
@RunWith(MavenConfiguredJUnit4TestRunner.class)
public class JAXBTest {
    @Test
    public void test() throws Exception {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        JAXBContext context = JAXBContext.newInstance(DummyBean.class);
        OMSourcedElement element = factory.createOMElement(new JAXBOMDataSource(context, new DummyBean()));
        element.serialize(new ByteArrayOutputStream());
    }
}
