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
package org.apache.axiom.core;

/** Selects, creates or updates an attribute based on some match rule. */
public interface AttributeMatcher {
    /**
     * Check if the given attribute matches. The values of the <code>namespaceURI</code> and <code>
     * name</code> parameters are those passed to {@link
     * CoreElement#coreGetAttribute(AttributeMatcher, String, String)} or {@link
     * CoreElement#coreSetAttribute(AttributeMatcher, String, String, String, String)}, or they are
     * determined by the return values of {@link #getNamespaceURI(CoreAttribute)} and {@link
     * #getName(CoreAttribute)} if {@link CoreElement#coreSetAttribute(AttributeMatcher,
     * CoreAttribute, Semantics)} is used. It is not required that these parameters strictly
     * represent the namespace URI and local name of the attribute. Their exact meaning is defined
     * by the particular {@link AttributeMatcher} implementation.
     *
     * @param attr the attribute to check
     * @param namespaceURI see above
     * @param name see above
     * @return <code>true</code> if the attribute matches, <code>false</code> otherwise
     */
    boolean matches(CoreAttribute attr, String namespaceURI, String name);

    /**
     * Get the {@code namespaceURI} parameter for an existing attribute. This method is used by
     * {@link CoreElement#coreSetAttribute(AttributeMatcher, CoreAttribute, Semantics)} which passes
     * its return value as parameter to {@link #matches(CoreAttribute, String, String)}.
     *
     * @param attr the attribute
     * @return the {@code namespaceURI} parameter to be passed to {@link #matches(CoreAttribute,
     *     String, String)}
     */
    String getNamespaceURI(CoreAttribute attr);

    /**
     * Get the {@code name} parameter for an existing attribute. This method is used by {@link
     * CoreElement#coreSetAttribute(AttributeMatcher, CoreAttribute, Semantics)} which passes its
     * return value as parameter to {@link #matches(CoreAttribute, String, String)}.
     *
     * @param attr the attribute
     * @return the {@code name} parameter to be passed to {@link #matches(CoreAttribute, String,
     *     String)}
     */
    String getName(CoreAttribute attr);

    /**
     * Create a new attribute node. The values of the <code>namespaceURI</code>, <code>name</code>,
     * <code>prefix</code> and <code>value</code> parameters are those passed to {@link
     * CoreElement#coreSetAttribute(AttributeMatcher, String, String, String, String)}.
     *
     * @param nodeFactory the node factory to be used to create the new attribute node
     * @param namespaceURI see above
     * @param name see above
     * @param prefix see above
     * @param value see above
     * @return
     * @throws CoreModelException
     */
    CoreAttribute createAttribute(
            NodeFactory2 nodeFactory, String namespaceURI, String name, String prefix, String value)
            throws CoreModelException;

    /**
     * Update an existing attribute. The values of the <code>prefix</code> and <code>value</code>
     * parameters are those passed to {@link CoreElement#coreSetAttribute(AttributeMatcher, String,
     * String, String, String)}.
     *
     * @param attr the attribute to update
     * @param prefix see above
     * @param value see above
     * @throws CoreModelException
     */
    void update(CoreAttribute attr, String prefix, String value) throws CoreModelException;
}
