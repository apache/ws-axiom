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

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.ts.AxiomTestCase;

public class TestChildReDeclaringGrandParentsDefaultNSWithPrefix extends AxiomTestCase {
    public TestChildReDeclaringGrandParentsDefaultNSWithPrefix(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory fac = metaFactory.getOMFactory();
        OMElement elem =
                fac.createOMElement(
                        "RequestSecurityToken",
                        fac.createOMNamespace("http://schemas.xmlsoap.org/ws/2005/02/trust", ""));
        fac.createOMElement(new QName("TokenType"), elem).setText("test");
        fac.createOMElement(new QName("RequestType"), elem).setText("test1");

        OMElement entElem =
                fac.createOMElement(
                        new QName("http://schemas.xmlsoap.org/ws/2005/02/trust", "Entropy", "wst"),
                        elem);
        OMElement binSecElem =
                fac.createOMElement(
                        new QName(
                                "http://schemas.xmlsoap.org/ws/2005/02/trust",
                                "Binarysecret",
                                "wst"),
                        entElem);
        binSecElem.setText("secret value");
        String xml = elem.toString();
        assertTrue(
                "Binarysecret element should have \'wst\' ns prefix",
                xml.indexOf("<wst:Binarysecret") != -1);
    }
}
