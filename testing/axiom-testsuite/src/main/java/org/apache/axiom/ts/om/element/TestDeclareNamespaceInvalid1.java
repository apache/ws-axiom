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

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.inject.Inject;
import javax.xml.namespace.QName;
import junit.framework.TestCase;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;

/**
 * Tests Axiom's behavior when {@link OMElement#declareNamespace(String, String)} is used to add a
 * namespace declaration that binds a prefix to an empty namespace URI. This is forbidden by both
 * XML 1.0 and XML 1.1.
 */
public class TestDeclareNamespaceInvalid1 extends TestCase {
    @Inject
    private OMFactory factory;

    @Override
    protected void runTest() throws Throwable {
        OMElement element = factory.createOMElement(new QName("test"));
        assertThatThrownBy(() -> element.declareNamespace("", "ns")).isInstanceOf(IllegalArgumentException.class);
    }
}
