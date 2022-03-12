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

import java.util.Iterator;

public interface CoreElement extends CoreChildNode, CoreMixedContentContainer, CoreNamedNode {
    /**
     * Get the first attribute of this element.
     *
     * @return the first attribute, or <code>null</code> if this element has no attributes
     */
    CoreAttribute coreGetFirstAttribute();

    /**
     * Get the last attribute of this element.
     *
     * @return the last attribute, or <code>null</code> if this element has no attributes
     */
    CoreAttribute coreGetLastAttribute();

    /**
     * Get the first attribute selected by a given {@link AttributeMatcher}.
     *
     * @param matcher the {@link AttributeMatcher} implementation to use
     * @param namespaceURI the <code>namespaceURI</code> parameter to pass to {@link
     *     AttributeMatcher#matches(CoreAttribute, String, String)}
     * @param name the <code>name</code> parameter to pass to {@link
     *     AttributeMatcher#matches(CoreAttribute, String, String)}
     * @return the (first) matching attribute, or <code>null</code> if no matching attribute was
     *     found
     */
    CoreAttribute coreGetAttribute(AttributeMatcher matcher, String namespaceURI, String name);

    /**
     * Create or update an attribute based on a given {@link AttributeMatcher}.
     *
     * @param matcher the {@link AttributeMatcher} implementation to use
     * @param namespaceURI the <code>namespaceURI</code> parameter to pass to {@link
     *     AttributeMatcher#matches(CoreAttribute, String, String)} and {@link
     *     AttributeMatcher#createAttribute(CoreElement, String, String, String, String)}
     * @param name the <code>name</code> parameter to pass to {@link
     *     AttributeMatcher#matches(CoreAttribute, String, String)} and {@link
     *     AttributeMatcher#createAttribute(CoreElement, String, String, String, String)}
     * @param prefix the <code>prefix</code> parameter to pass to {@link
     *     AttributeMatcher#createAttribute(CoreElement, String, String, String, String)} and {@link
     *     AttributeMatcher#update(CoreAttribute, String, String)}
     * @param value the <code>value</code> parameter to pass to {@link
     *     AttributeMatcher#createAttribute(CoreElement, String, String, String, String)} and {@link
     *     AttributeMatcher#update(CoreAttribute, String, String)}
     */
    void coreSetAttribute(
            AttributeMatcher matcher, String namespaceURI, String name, String prefix, String value)
            throws CoreModelException;

    /**
     * Add a new attribute or replace an existing attribute based on a given {@link
     * AttributeMatcher}. If a matching attribute on this element is found, it is replaced by the
     * specified attribute. If no matching attribute is found, then the specified attribute is added
     * to this element. If the attribute is already owned by this element, then calling this method
     * has no effect.
     *
     * @param matcher the {@link AttributeMatcher} implementation to use
     * @param attr the new attribute to add
     * @param semantics determines the new owner document for the replaced attribute
     * @return the attribute that was replaced by the new attribute, or <code>null</code> if no
     *     matching attribute existed.
     */
    CoreAttribute coreSetAttribute(
            AttributeMatcher matcher, CoreAttribute attr, Semantics semantics);

    /**
     * Append an attribute to this element. The attribute is simply added at the end of the list of
     * attributes for this element. This method should be used with care because no provisions are
     * made to ensure uniqueness of attribute names.
     *
     * @param attr the attribute to append
     */
    void coreAppendAttribute(CoreAttribute attr);

    /**
     * Remove an attribute based on a given {@link AttributeMatcher}.
     *
     * @param matcher the {@link AttributeMatcher} implementation to use
     * @param namespaceURI the <code>namespaceURI</code> parameter to pass to {@link
     *     AttributeMatcher#matches(CoreAttribute, String, String)}
     * @param name the <code>name</code> parameter to pass to {@link
     *     AttributeMatcher#matches(CoreAttribute, String, String)}
     * @return <code>true</code> if a matching attribute was found (and has been removed), <code>
     *     false</code> if no matching attribute was found
     */
    boolean coreRemoveAttribute(
            AttributeMatcher matcher, String namespaceURI, String name, Semantics semantics);

    <T extends CoreAttribute, S> Iterator<S> coreGetAttributesByType(
            Class<T> type, Mapper<S, ? super T> mapper, Semantics semantics);

    /**
     * Look up the namespace URI associated to the given prefix.
     *
     * @param prefix The prefix to look for. If this parameter is the empty string, then the URI of
     *     the default namespace will be returned.
     * @param semantics The API semantics to use.
     * @return the namespace URI or <code>null</code> if the prefix is not bound; if the prefix is
     *     the empty string and no default namespace declaration exists, then an empty string is
     *     returned
     * @see Semantics#isUseStrictNamespaceLookup()
     */
    String coreLookupNamespaceURI(String prefix, Semantics semantics) throws CoreModelException;

    /**
     * Find a prefix associated to the given namespace URI. Default namespaces are not taken into
     * account by this method.
     *
     * @param namespaceURI The namespace URI to look for. This parameter must not be <code>null
     *     </code> (XML forbids to bind a prefix to the null namespace).
     * @param semantics The API semantics to use.
     * @return a prefix bound to the given namespace URI or <code>null</code> if none is found
     * @throws IllegalArgumentException if <code>namespaceURI</code> is <code>null</code>
     * @see Semantics#isUseStrictNamespaceLookup()
     */
    // TODO: wrong Javadoc: null vs. empty string
    // TODO: we can support default namespaces!
    String coreLookupPrefix(String namespaceURI, Semantics semantics) throws CoreModelException;

    void corePromote(CoreElement newElement, Semantics semantics) throws CoreModelException;

    void internalSetFirstAttribute(CoreAttribute firstAttribute);
}
