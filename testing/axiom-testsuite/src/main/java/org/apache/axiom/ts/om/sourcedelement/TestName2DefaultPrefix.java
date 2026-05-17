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
package org.apache.axiom.ts.om.sourcedelement;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import java.io.StringWriter;
import junit.framework.TestCase;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.ts.om.sourcedelement.util.PullOMDataSource;

/**
 * Tests the OMSourcedElement localName, namespace and prefix settings before and after
 * serialization Document: testDocument (which uses the default namespace) Type of Serialization:
 * Serialize and consume Tests update of prefix
 */
public class TestName2DefaultPrefix extends TestCase {
    @Inject
    private OMFactory factory;

    @Override
    protected void runTest() throws Throwable {

        // Create OMSE with a DUMMYPREFIX prefix even though the underlying element uses the default
        // prefix
        OMNamespace rootNS = factory.createOMNamespace("http://sampleroot", "rootPrefix");
        OMNamespace ns = factory.createOMNamespace("http://www.sosnoski.com/uwjws/library", "DUMMYPREFIX");
        OMElement element =
                factory.createOMElement(new PullOMDataSource(TestDocument.DOCUMENT1.getContent()), "library", ns);
        OMElement root = factory.createOMElement("root", rootNS);
        root.addChild(element);

        // Test getting the namespace, localpart and prefix.  This should used not result in
        // expansion
        assertThat(element.getLocalName()).isEqualTo("library");
        assertThat(element.getNamespace().getNamespaceURI()).isEqualTo("http://www.sosnoski.com/uwjws/library");
        assertThat(element.getNamespace().getPrefix()).isEqualTo("DUMMYPREFIX");

        // Serialize and consume.  This should not cause expansion and currently won't update
        // the name of the element.
        StringWriter writer = new StringWriter();
        root.serializeAndConsume(writer);
        String result = writer.toString();

        assertThat(element.getLocalName()).isEqualTo("library");
        assertThat(element.getNamespace().getNamespaceURI()).isEqualTo("http://www.sosnoski.com/uwjws/library");
        assertThat(element.getNamespace().getPrefix()).isEqualTo("DUMMYPREFIX");
        // Make sure that the serialized string does not contain DUMMYPREFIX
        assertThat(result).doesNotContain("DUMMYPREFIX");

        assertThat(result).contains("1930110111");
    }
}
