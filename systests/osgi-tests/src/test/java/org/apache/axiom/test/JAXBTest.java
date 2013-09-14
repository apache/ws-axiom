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

import static org.ops4j.pax.exam.CoreOptions.frameworkProperty;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.MavenUtils.asInProject;

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
                mavenBundle().groupId("org.apache.geronimo.specs").artifactId("geronimo-activation_1.1_spec").version(asInProject()),
                mavenBundle().groupId("org.apache.servicemix.specs").artifactId("org.apache.servicemix.specs.stax-api-1.0").version(asInProject()),
                mavenBundle().groupId("org.codehaus.woodstox").artifactId("stax2-api").version(asInProject()),
                mavenBundle().groupId("org.codehaus.woodstox").artifactId("woodstox-core-asl").version(asInProject()),
                mavenBundle().groupId("org.apache.james").artifactId("apache-mime4j-core").version(asInProject()),
                mavenBundle().groupId("org.apache.ws.commons.axiom").artifactId("axiom-api").version(asInProject()),
                mavenBundle().groupId("org.apache.ws.commons.axiom").artifactId("axiom-impl").version(asInProject()),
                mavenBundle().groupId("org.apache.servicemix.specs").artifactId("org.apache.servicemix.specs.jaxb-api-2.1").version(asInProject()),
                mavenBundle().groupId("org.apache.servicemix.bundles").artifactId("org.apache.servicemix.bundles.jaxb-impl").version(asInProject()),
                mavenBundle().groupId("org.apache.ws.commons.axiom").artifactId("axiom-jaxb").version(asInProject()),
                junitBundles(),
                frameworkProperty("foo").value("bar"));
    }
    
    @Test
    public void test() throws Exception {
        OMFactory factory = OMAbstractFactory.getOMFactory();
        JAXBContext context = JAXBContext.newInstance(DummyBean.class);
        OMSourcedElement element = factory.createOMElement(new JAXBOMDataSource(context, new DummyBean()));
        element.serialize(new ByteArrayOutputStream());
    }
}
