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
package org.apache.axiom.systest.wildfly;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.apache.axiom.om.OMDocType;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.util.stax.dialect.StAXDialectDetector;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class DialectTest {
    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "dialect-test.war")
                .addAsLibraries(new File("target/deps").listFiles());
    }

    /**
     * Directly tests {@link StAXDialectDetector}.
     *
     * @throws Exception
     */
    @Test
    public void testStAXDialectDetector() throws Exception {
        assertFalse(
                StAXDialectDetector.getDialect(XMLInputFactory.newInstance())
                        .getName()
                        .equals("Unknown"));
        assertFalse(
                StAXDialectDetector.getDialect(XMLOutputFactory.newInstance())
                        .getName()
                        .equals("Unknown"));
    }

    /**
     * Tests that Axiom is able to read a DOCTYPE declaration. Since accessing the information in
     * the DOCTYPE declaration is not standardized by the StAX specification, this will fail if the
     * StAX dialect is not detected correctly.
     *
     * @throws Exception
     */
    @Test
    public void testDTD() throws Exception {
        OMDocument document =
                OMXMLBuilderFactory.createOMBuilder(new StringReader("<!DOCTYPE root><root/>"))
                        .getDocument();
        OMDocType dtd = (OMDocType) document.getFirstOMChild();
        assertEquals("root", dtd.getRootName());
    }
}
