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
package org.apache.axiom.ts.om.builder;

import static org.apache.axiom.truth.AxiomTruth.assertThat;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.jaxp.dom.DOMImplementation;
import org.junit.jupiter.api.function.Executable;
import org.w3c.dom.Element;

public class TestCreateOMBuilderFromDOMWithNSUnawareNamespaceDeclaration implements Executable {
    @Inject
    private OMFactory factory;

    @Inject
    @Named("prefix")
    private String prefix;

    @Override
    public void execute() throws Throwable {
        Element domElement = DOMImplementation.XERCES.newDocument().createElementNS(null, "test");
        domElement.setAttribute(prefix.isEmpty() ? "xmlns" : "xmlns:" + prefix, "urn:ns1");
        OMElement element =
                OMXMLBuilderFactory.createOMBuilder(factory, domElement, false).getDocumentElement();
        assertThat(element).hasNamespaceDeclaration(prefix, "urn:ns1");
    }
}
