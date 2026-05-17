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
import javax.xml.namespace.QName;
import junit.framework.TestCase;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;

public class TestChildReDeclaringGrandParentsDefaultNSWithPrefix extends TestCase {
    @Inject
    private OMFactory factory;

    @Override
    protected void runTest() throws Throwable {
        OMElement elem = factory.createOMElement(
                "RequestSecurityToken", factory.createOMNamespace("http://schemas.xmlsoap.org/ws/2005/02/trust", ""));
        factory.createOMElement(new QName("TokenType"), elem).setText("test");
        factory.createOMElement(new QName("RequestType"), elem).setText("test1");

        OMElement entElem = factory.createOMElement(
                new QName("http://schemas.xmlsoap.org/ws/2005/02/trust", "Entropy", "wst"), elem);
        OMElement binSecElem = factory.createOMElement(
                new QName("http://schemas.xmlsoap.org/ws/2005/02/trust", "Binarysecret", "wst"), entElem);
        binSecElem.setText("secret value");
        String xml = elem.toString();
        assertThat(xml).contains("<wst:Binarysecret");
    }
}
