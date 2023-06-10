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
package org.apache.axiom.truth.xml.spi;

import java.util.Map;

import javax.xml.namespace.QName;

// TODO: add a close method
public interface Traverser {
    Event next() throws TraverserException;

    String getRootName();

    String getPublicId();

    String getSystemId();

    QName getQName();

    /**
     * Get the attributes for the current element. Only valid if the last call to {@link #next()}
     * returned {@link Event#START_ELEMENT}.
     *
     * @return the attributes of the element, or <code>null</code> if the element has no attributes
     */
    Map<QName, String> getAttributes();

    /**
     * Get the namespace declarations for the current element. Only valid if the last call to {@link
     * #next()} returned {@link Event#START_ELEMENT}. Namespace declarations for the default
     * namespace are represented using an empty string as prefix.
     *
     * @return the namespace declarations of the element, or <code>null</code> if the element has no
     *     namespace declarations
     */
    Map<String, String> getNamespaces();

    String getText();

    String getEntityName();

    String getPITarget();

    String getPIData();
}
