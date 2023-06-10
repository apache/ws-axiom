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
package org.apache.axiom.truth;

import static com.google.common.truth.Fact.simpleFact;

import java.util.Iterator;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;

import com.google.common.truth.FailureMetadata;

public final class OMElementSubject extends AbstractOMContainerSubject {
    private final OMElement subject;

    OMElementSubject(FailureMetadata failureMetadata, OMElement subject) {
        super(failureMetadata, subject);
        this.subject = subject;
    }

    public void hasNoNamespaceDeclarations() {
        if (subject.getAllDeclaredNamespaces().hasNext()) {
            failWithActual(simpleFact("expected to have no namespace declarations"));
        }
    }

    public void hasNamespaceDeclaration(OMNamespace ns) {
        for (Iterator<OMNamespace> it = subject.getAllDeclaredNamespaces(); it.hasNext(); ) {
            if (it.next().equals(ns)) {
                return;
            }
        }
        failWithActual(
                simpleFact(
                        "expected to have namespace declaration for namespace URI \""
                                + ns.getNamespaceURI()
                                + "\" and prefix \""
                                + ns.getPrefix()
                                + "\""));
    }

    public void hasNamespaceDeclaration(String prefix, String namespaceURI) {
        for (Iterator<OMNamespace> it = subject.getAllDeclaredNamespaces(); it.hasNext(); ) {
            OMNamespace ns = it.next();
            if (ns.getPrefix().equals(prefix) && ns.getNamespaceURI().equals(namespaceURI)) {
                return;
            }
        }
        failWithActual(
                simpleFact(
                        "expected to have namespace declaration for namespace URI \""
                                + namespaceURI
                                + "\" and prefix \""
                                + prefix
                                + "\""));
    }
}
