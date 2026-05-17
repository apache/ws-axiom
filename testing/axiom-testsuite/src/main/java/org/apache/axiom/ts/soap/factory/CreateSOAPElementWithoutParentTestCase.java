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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Iterator;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPConstants;
import org.apache.axiom.testutils.suite.MatrixTestCase;
import org.apache.axiom.ts.soap.SOAPElementType;
import org.apache.axiom.ts.soap.SOAPSpec;

public abstract class CreateSOAPElementWithoutParentTestCase implements MatrixTestCase {
    protected final SOAPSpec spec;
    protected final SOAPElementType type;

    public CreateSOAPElementWithoutParentTestCase(SOAPSpec spec, SOAPElementType type) {
        this.spec = spec;
        this.type = type;
    }

    @Override
    public final void runTest() throws Throwable {
        QName expectedName = type.getQName(spec);
        if (expectedName == null) {
            assertThatThrownBy(() -> createSOAPElement()).isInstanceOf(UnsupportedOperationException.class);
        } else {
            String expectedPrefix =
                    expectedName.getNamespaceURI().length() == 0 ? "" : SOAPConstants.SOAP_DEFAULT_NAMESPACE_PREFIX;
            OMElement element = createSOAPElement();
            assertThat(element.isComplete()).isTrue();
            QName actualName = element.getQName();
            assertThat(actualName).isEqualTo(expectedName);
            assertThat(actualName.getPrefix()).isEqualTo(expectedPrefix);
            assertThat(element.getParent()).isNull();
            Iterator<OMNamespace> it = element.getAllDeclaredNamespaces();
            if (expectedPrefix.length() != 0) {
                assertThat(it.hasNext()).isTrue();
                OMNamespace ns = it.next();
                assertThat(ns.getNamespaceURI()).isEqualTo(expectedName.getNamespaceURI());
                assertThat(ns.getPrefix()).isEqualTo(expectedPrefix);
            }
            assertThat(it.hasNext()).isFalse();
            assertThat(element.getAllAttributes().hasNext()).isFalse();
            assertThat(element.getFirstOMChild()).isNull();
        }
    }

    protected abstract OMElement createSOAPElement();
}
