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
package org.apache.axiom.om;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Checks that there are no collisions between the Axiom object model interfaces and DOM.
 *
 * <p>A fundamental design constraint of the Axiom API is that there must be no collisions between
 * Axiom methods and DOM methods, so that it is possible to create an Axiom implementation that also
 * implements DOM. Note however that Axiom may define methods with the same signature as DOM
 * methods, provided that they have the same behavior. This is e.g. the case for {@link
 * OMElement#getNamespaceURI()} and {@link OMElement#getLocalName()}.
 */
public class DOMCompatibilityTest extends TestCase {
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        // Note: All exceptions are methods that are known to have the same behavior in Axiom
        //       and DOM. In these cases, method collisions are allowed.
        suite.addTest(
                new MethodCollisionTestCase(
                        OMAttribute.class,
                        Attr.class,
                        new MethodSignature[] {
                            new MethodSignature("getPrefix", new Class[0]),
                            new MethodSignature("getNamespaceURI", new Class[0]),
                            new MethodSignature("getLocalName", new Class[0])
                        }));
        suite.addTest(new MethodCollisionTestCase(OMComment.class, Comment.class));
        suite.addTest(
                new MethodCollisionTestCase(
                        OMDocType.class,
                        DocumentType.class,
                        new MethodSignature[] {
                            new MethodSignature("getPublicId", new Class[0]),
                            new MethodSignature("getSystemId", new Class[0]),
                            new MethodSignature("getInternalSubset", new Class[0])
                        }));
        suite.addTest(new MethodCollisionTestCase(OMDocument.class, Document.class));
        suite.addTest(
                new MethodCollisionTestCase(
                        OMProcessingInstruction.class,
                        ProcessingInstruction.class,
                        new MethodSignature[] {new MethodSignature("getTarget", new Class[0])}));
        suite.addTest(
                new MethodCollisionTestCase(
                        OMElement.class,
                        Element.class,
                        new MethodSignature[] {
                            new MethodSignature("getPrefix", new Class[0]),
                            new MethodSignature("getNamespaceURI", new Class[0]),
                            new MethodSignature("getLocalName", new Class[0])
                        }));
        suite.addTest(
                new MethodCollisionTestCase(
                        OMSourcedElement.class,
                        Element.class,
                        new MethodSignature[] {
                            new MethodSignature("getPrefix", new Class[0]),
                            new MethodSignature("getNamespaceURI", new Class[0]),
                            new MethodSignature("getLocalName", new Class[0])
                        }));
        suite.addTest(new MethodCollisionTestCase(OMText.class, Text.class));
        suite.addTest(new MethodCollisionTestCase(OMText.class, CDATASection.class));
        suite.addTest(new MethodCollisionTestCase(OMEntityReference.class, EntityReference.class));
        return suite;
    }
}
