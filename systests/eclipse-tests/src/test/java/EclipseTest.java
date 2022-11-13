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
import static org.junit.Assert.assertEquals;
import static org.ops4j.pax.exam.CoreOptions.frameworkProperty;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.url;

import java.io.StringReader;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.dom.DOMMetaFactory;
import org.eclipse.osgi.internal.location.EquinoxLocations;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Tests that Axiom works out of the box in an Eclipse platform.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class EclipseTest {
    @Configuration
    public static Option[] configuration() {
        return options(
                frameworkProperty(EquinoxLocations.PROP_HOME_LOCATION_AREA).value("target"),
                // Don't start bundles. We expect Equinox to start them lazily.
                url("link:classpath:org.apache.commons.commons-io.link").start(false),
                url("link:classpath:org.apache.james.apache-mime4j-core.link").start(false),
                url("link:classpath:org.apache.ws.commons.axiom.axiom-impl.link").start(false),
                url("link:classpath:org.apache.ws.commons.axiom.axiom-dom.link").start(false),
                url("link:classpath:org.apache.ws.commons.axiom.axiom-api.link").start(false),
                junitBundles());
    }
    
    @Test
    public void testLLOM() {
        OMElement element = OMXMLBuilderFactory.createOMBuilder(new StringReader("<ns:test xmlns:ns='urn:ns'>test</ns:test>")).getDocumentElement();
        assertEquals(new QName("urn:ns", "test"), element.getQName());
    }
    
    @Test
    public void testDOOM() throws Exception {
        DOMMetaFactory metaFactory = (DOMMetaFactory)OMAbstractFactory.getMetaFactory(OMAbstractFactory.FEATURE_DOM);
        Document document = metaFactory.newDocumentBuilderFactory().newDocumentBuilder().newDocument();
        Element element = document.createElementNS("urn:test", "p:root");
        assertEquals(new QName("urn:test", "root"), ((OMElement)element).getQName());
    }
}
