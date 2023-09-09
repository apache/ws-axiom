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
import static org.assertj.core.api.Assertions.assertThat;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.url;

import java.io.StringReader;

import javax.inject.Inject;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class FactoryTest {
    @Configuration
    public static Option[] configuration() {
        return options(
                url("link:classpath:org.apache.aries.spifly.dynamic.framework.extension.link"),
                url("link:classpath:org.apache.servicemix.specs.stax-api-1.0.link"),
                url("link:classpath:stax2-api.link"),
                url("link:classpath:com.fasterxml.woodstox.woodstox-core.link"),
                url("link:classpath:org.apache.commons.commons-io.link"),
                url("link:classpath:org.apache.james.apache-mime4j-core.link"),
                url("link:classpath:org.apache.ws.commons.axiom.axiom-api.link"),
                url("link:classpath:org.apache.ws.commons.axiom.axiom-impl.link"),
                url("link:classpath:org.apache.ws.commons.axiom.axiom-dom.link"),
                filteredSystemPackages("javax.xml.stream"),
                junitBundles(),
                url("link:classpath:net.bytebuddy.byte-buddy.link"),
                url("link:classpath:assertj-core.link"));
    }

    @Inject private BundleContext context;

    @Test
    public void testGetOMFactory() throws Exception {
        assertThat(OMAbstractFactory.getOMFactory()).isNotNull();
    }

    @Test
    public void testGetSOAP11Factory() throws Exception {
        assertThat(OMAbstractFactory.getSOAP11Factory()).isNotNull();
    }

    @Test
    public void testGetSOAP12Factory() throws Exception {
        assertThat(OMAbstractFactory.getSOAP12Factory()).isNotNull();
    }

    @Test
    public void testLLOMMetaFactoryServicePresent() throws Exception {
        ServiceReference<?>[] omfactRefs =
                context.getServiceReferences(
                        "org.apache.axiom.om.OMMetaFactory", "(implementationName=llom)");
        assertThat(omfactRefs).isNotNull();
        assertThat(omfactRefs).hasSize(1);
    }

    @Test
    public void testDOOMMetaFactoryServicePresent() throws Exception {
        ServiceReference<?>[] omfactRefs =
                context.getServiceReferences(
                        "org.apache.axiom.om.OMMetaFactory", "(implementationName=doom)");
        assertThat(omfactRefs).isNotNull();
        assertThat(omfactRefs).hasSize(2);
    }

    @Test
    public void testLookupByFeature() throws Exception {
        ServiceReference<?>[] omfactRefs =
                context.getServiceReferences("org.apache.axiom.om.OMMetaFactory", "(feature=dom)");
        assertThat(omfactRefs).isNotNull();
        assertThat(omfactRefs).hasSize(1);
    }

    @Test
    public void testLookupDOMMetaFactory() throws Exception {
        ServiceReference<?>[] omfactRefs =
                context.getServiceReferences("org.apache.axiom.om.dom.DOMMetaFactory", null);
        assertThat(omfactRefs).isNotNull();
        assertThat(omfactRefs).hasSize(1);
    }

    @Test
    public void testCreateOMBuilder() throws Exception {
        OMElement oe =
                OMXMLBuilderFactory.createOMBuilder(
                                new StringReader(
                                        "<a:testElement xmlns:a=\"http://test/namespace\"/>"))
                        .getDocumentElement();
        assertThat(oe.getLocalName()).isEqualTo("testElement");
        assertThat(oe.getNamespace().getNamespaceURI()).isEqualTo("http://test/namespace");
    }
}
