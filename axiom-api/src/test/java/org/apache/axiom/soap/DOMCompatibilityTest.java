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
package org.apache.axiom.soap;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.axiom.om.MethodCollisionTestCase;
import org.apache.axiom.om.MethodSignature;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Checks that there are no collisions between the SOAP object model interfaces and DOM.
 *
 * @see org.apache.axiom.om.DOMCompatibilityTest
 */
public class DOMCompatibilityTest extends TestCase {
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();

        // These methods are known to have the same behavior in Axiom and DOM:
        MethodSignature[] elementExceptions =
                new MethodSignature[] {
                    new MethodSignature("getPrefix", new Class[0]),
                    new MethodSignature("getNamespaceURI", new Class[0]),
                    new MethodSignature("getLocalName", new Class[0])
                };

        suite.addTest(
                new MethodCollisionTestCase(SOAPBody.class, Element.class, elementExceptions));
        suite.addTest(
                new MethodCollisionTestCase(SOAPEnvelope.class, Element.class, elementExceptions));
        suite.addTest(
                new MethodCollisionTestCase(SOAPFault.class, Element.class, elementExceptions));
        suite.addTest(
                new MethodCollisionTestCase(SOAPFaultCode.class, Element.class, elementExceptions));
        suite.addTest(
                new MethodCollisionTestCase(
                        SOAPFaultDetail.class, Element.class, elementExceptions));
        suite.addTest(
                new MethodCollisionTestCase(SOAPFaultNode.class, Element.class, elementExceptions));
        suite.addTest(
                new MethodCollisionTestCase(
                        SOAPFaultReason.class, Element.class, elementExceptions));
        suite.addTest(
                new MethodCollisionTestCase(SOAPFaultRole.class, Element.class, elementExceptions));
        suite.addTest(
                new MethodCollisionTestCase(
                        SOAPFaultSubCode.class, Element.class, elementExceptions));
        suite.addTest(
                new MethodCollisionTestCase(SOAPFaultText.class, Element.class, elementExceptions));
        suite.addTest(
                new MethodCollisionTestCase(
                        SOAPFaultValue.class, Element.class, elementExceptions));
        suite.addTest(
                new MethodCollisionTestCase(SOAPHeader.class, Element.class, elementExceptions));
        suite.addTest(
                new MethodCollisionTestCase(
                        SOAPHeaderBlock.class, Element.class, elementExceptions));
        suite.addTest(
                new MethodCollisionTestCase(SOAPMessage.class, Document.class, elementExceptions));
        return suite;
    }
}
