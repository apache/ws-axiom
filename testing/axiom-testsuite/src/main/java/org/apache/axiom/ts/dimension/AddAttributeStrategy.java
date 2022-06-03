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
package org.apache.axiom.ts.dimension;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.testing.multiton.Multiton;
import org.apache.axiom.testutils.suite.Dimension;
import org.apache.axiom.testutils.suite.MatrixTestCase;

/**
 * Defines a strategy (in terms of usage of particular API methods) to add an attribute to an
 * element.
 */
public abstract class AddAttributeStrategy extends Multiton implements Dimension {
    /**
     * Creates the attribute using {@link OMFactory#createOMAttribute(String, OMNamespace, String)}
     * and then adds it using {@link OMElement#addAttribute(OMAttribute)}.
     */
    public static final AddAttributeStrategy FACTORY =
            new AddAttributeStrategy() {
                @Override
                public void addTestParameters(MatrixTestCase testCase) {
                    testCase.addTestParameter("addAttribute", "factory");
                }

                @Override
                public OMAttribute addAttribute(
                        OMElement element, String localName, OMNamespace ns, String value) {
                    OMAttribute attr =
                            element.getOMFactory().createOMAttribute(localName, ns, value);
                    element.addAttribute(attr);
                    return attr;
                }
            };

    /** Adds the attribute using {@link OMElement#addAttribute(String, String, OMNamespace)}. */
    public static final AddAttributeStrategy DIRECT =
            new AddAttributeStrategy() {
                @Override
                public void addTestParameters(MatrixTestCase testCase) {
                    testCase.addTestParameter("addAttribute", "direct");
                }

                @Override
                public OMAttribute addAttribute(
                        OMElement element, String localName, OMNamespace ns, String value) {
                    return element.addAttribute(localName, value, ns);
                }
            };

    private AddAttributeStrategy() {}

    public abstract OMAttribute addAttribute(
            OMElement element, String localName, OMNamespace ns, String value);
}
