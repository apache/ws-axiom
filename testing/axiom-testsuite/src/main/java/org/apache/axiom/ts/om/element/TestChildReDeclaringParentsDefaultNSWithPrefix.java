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

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import java.io.ByteArrayInputStream;
import javax.xml.namespace.QName;
import junit.framework.TestCase;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;

public class TestChildReDeclaringParentsDefaultNSWithPrefix extends TestCase {
    @Inject
    private OMFactory factory;

    @Override
    protected void runTest() throws Throwable {
        OMElement elem = factory.createOMElement(
                "RequestSecurityToken", factory.createOMNamespace("http://schemas.xmlsoap.org/ws/2005/02/trust", ""));
        factory.createOMElement(new QName("TokenType"), elem).setText("test");
        factory.createOMElement(new QName("RequestType"), elem).setText("test1");

        factory.createOMElement(new QName("http://schemas.xmlsoap.org/ws/2005/02/trust", "Entropy", "wst"), elem);
        String xml = elem.toString();

        OMXMLParserWrapper builder =
                OMXMLBuilderFactory.createOMBuilder(factory, new ByteArrayInputStream(xml.getBytes()));

        builder.getDocumentElement().build();

        // The StAX implementation may or may not have a trailing blank in the tag
        String assertText1 = "<wst:Entropy xmlns:wst=\"http://schemas.xmlsoap.org/ws/2005/02/trust\" />";
        String assertText2 = "<wst:Entropy xmlns:wst=\"http://schemas.xmlsoap.org/ws/2005/02/trust\"/>";
        String assertText3 = "<wst:Entropy xmlns:wst=\"http://schemas.xmlsoap.org/ws/2005/02/trust\"></wst:Entropy>";

        assertThat(xml)
                .satisfiesAnyOf(
                        s -> assertThat(s).contains(assertText1),
                        s -> assertThat(s).contains(assertText2),
                        s -> assertThat(s).contains(assertText3));
    }
}
