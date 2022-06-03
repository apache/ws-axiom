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
package org.apache.axiom.ts.soap.factory;

import java.util.Iterator;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

/**
 * Checks the content of the SOAP envelope returned by {@link
 * SOAPFactory#createDefaultSOAPMessage()}.
 */
public class TestCreateDefaultSOAPMessage extends SOAPTestCase {
    public TestCreateDefaultSOAPMessage(OMMetaFactory metaFactory, SOAPSpec spec) {
        super(metaFactory, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        SOAPMessage message = soapFactory.createDefaultSOAPMessage();
        SOAPEnvelope env = message.getSOAPEnvelope();
        assertNotNull(env);
        assertSame(env, message.getFirstOMChild());
        assertNull(env.getNextOMSibling());

        // Check correct SOAP version
        assertEquals(spec.getEnvelopeNamespaceURI(), env.getNamespaceURI());

        // Check the children
        Iterator<OMNode> it = env.getChildren();
        assertTrue(it.hasNext());
        OMNode child = it.next();
        assertTrue(child instanceof SOAPBody);
        assertNull(((SOAPBody) child).getFirstOMChild());
        assertFalse(it.hasNext());
    }
}
