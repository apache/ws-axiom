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
package org.apache.axiom.om.impl;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;

/**
 * Interface that is used internally by Axiom and that should not be considered being part of the
 * public API.
 */
public interface OMElementEx extends OMElement, OMNodeEx, OMContainerEx {
    /**
     * Adds a namespace declaration without doing any additional checks. This method is used
     * internally by the builder (which can safely assume that the data received from the parser is
     * well formed with respect to namespaces).
     * <p>
     * In contrast to {@link OMElement#declareNamespace(String, String)} this method can be used to
     * declare the default namespace.
     * 
     * @param uri
     *            the namespace to declare
     * @param prefix
     *            the prefix to associate with the given namespace
     * @return the created namespace information item
     */
    OMNamespace addNamespaceDeclaration(String uri, String prefix);
}
