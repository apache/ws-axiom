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

import static org.apache.axiom.test.Utils.filteredSystemPackages;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.url;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.ds.jaxb.JAXBOMDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

/**
 * Tests that the axiom-jaxb bundle can be loaded successfully and is operational.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class JAXBTest {
    @Configuration
    public static Option[] configuration() {
        return options(
                url("link:classpath:org.apache.aries.spifly.dynamic.framework.extension.link"),
                url("link:classpath:com.sun.activation.jakarta.activation.link"),
                url("link:classpath:org.apache.servicemix.specs.stax-api-1.0.link"),
                url("link:classpath:stax2-api.link"),
                url("link:classpath:com.fasterxml.woodstox.woodstox-core.link"),
                url("link:classpath:org.apache.commons.commons-io.link"),
                url("link:classpath:org.apache.james.apache-mime4j-core.link"),
                url("link:classpath:org.apache.ws.commons.axiom.axiom-api.link"),
                url("link:classpath:org.apache.ws.commons.axiom.axiom-impl.link"),
                url("link:classpath:org.glassfish.hk2.osgi-resource-locator.link"),
                url("link:classpath:jakarta.xml.bind-api.link"),
                url("link:classpath:com.sun.xml.bind.jaxb-osgi.link"),
                url("link:classpath:org.apache.ws.commons.axiom.axiom-activation.link"),
                url("link:classpath:org.apache.ws.commons.axiom.axiom-jaxb.link"),
                filteredSystemPackages("javax.xml.stream"),
                junitBundles());
    }
    
    @Test
    public void test() throws Exception {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        JAXBContext context = JAXBContext.newInstance(DummyBean.class);
        OMSourcedElement element = factory.createOMElement(new JAXBOMDataSource(context, new DummyBean()));
        element.serialize(new ByteArrayOutputStream());
    }
}
