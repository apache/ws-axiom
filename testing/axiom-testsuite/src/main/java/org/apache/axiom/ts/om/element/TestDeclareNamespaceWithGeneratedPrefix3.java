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
import java.util.Iterator;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.junit.jupiter.api.function.Executable;

/**
 * Tests that {@link OMElement#declareNamespace(String, String)} generates a new prefix if the
 * specified prefix is <code>null</code>.
 */
public class TestDeclareNamespaceWithGeneratedPrefix3 implements Executable {
    @Inject
    private OMFactory factory;

    @Override
    public void execute() throws Throwable {
        OMElement element = factory.createOMElement(new QName("test"));
        OMNamespace ns = element.declareNamespace("urn:ns", null);
        assertThat(ns.getNamespaceURI()).isEqualTo("urn:ns");
        assertThat(ns.getPrefix()).isNotNull();
        assertThat(ns.getPrefix().length() > 0).isTrue();
        Iterator<OMNamespace> it = element.getAllDeclaredNamespaces();
        assertThat(it.hasNext()).isTrue();
        OMNamespace ns2 = it.next();
        assertThat(ns2).isEqualTo(ns);
        assertThat(it.hasNext()).isFalse();
    }
}
