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
import static org.ops4j.pax.exam.CoreOptions.systemPackages;
import static org.ops4j.pax.exam.CoreOptions.url;

import java.io.StringReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

/**
 * Tests that Axiom works properly with SJSXP (instead of Woodstox) in an OSGi environment. In
 * particular, this is a regression test for <a
 * href="https://issues.apache.org/jira/browse/AXIOM-454">AXIOM-454</a>; it checks that the <code>
 * org.codehaus.stax2</code> import is optional.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class SJSXPTest {
    @Configuration
    public static Option[] configuration() {
        return options(
                url("link:classpath:org.apache.servicemix.specs.stax-api-1.0.link"),
                url("link:classpath:org.apache.servicemix.bundles.jaxp-ri.link"),
                url("link:classpath:org.apache.commons.commons-io.link"),
                url("link:classpath:org.apache.james.apache-mime4j-core.link"),
                url("link:classpath:org.apache.ws.commons.axiom.axiom-api.link"),
                url("link:classpath:org.apache.ws.commons.axiom.axiom-impl.link"),
                filteredSystemPackages("javax.xml.stream"),
                // For whatever reason, these packages are no longer exported by the system bundle
                // in Felix >= 4.0.0
                systemPackages("org.w3c.dom.html", "org.w3c.dom.ranges", "org.w3c.dom.traversal"),
                junitBundles(),
                url("link:classpath:net.bytebuddy.byte-buddy.link"),
                url("link:classpath:assertj-core.link"));
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
