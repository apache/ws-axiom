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
package org.apache.axiom.ts.soap.body;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

public class TestGetFaultNoFault extends SOAPTestCase {
    private final QName qname;

    public TestGetFaultNoFault(OMMetaFactory metaFactory, SOAPSpec spec, QName qname) {
        super(metaFactory, spec);
        this.qname = qname;
        addTestParameter("prefix", qname.getPrefix());
        addTestParameter("uri", qname.getNamespaceURI());
        addTestParameter("localName", qname.getLocalPart());
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPBody body = soapFactory.getDefaultEnvelope().getBody();
        body.addChild(
                soapFactory.createOMElement(
                        qname.getLocalPart(), qname.getNamespaceURI(), qname.getPrefix()));
        assertNull(body.getFault());
    }
}
