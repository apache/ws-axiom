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

package org.apache.axiom.om;

/**
 * A namespace binding specifying a namespace prefix and a namespace URI.
 *
 * <p>Implementations of this interface must be immutable or behave as if they were immutable, i.e.
 * {@link #getPrefix()} and {@link #getNamespaceURI()} must always return the same values when
 * invoked on the same instance.
 *
 * <p>Implementations must override {@link Object#equals(Object)} and {@link Object#hashCode()}. Two
 * {@link OMNamespace} instances are considered equal if their namespace URIs and prefixes are
 * equal.
 */
public interface OMNamespace {
    /**
     * Method equals.
     *
     * @param uri
     * @param prefix
     * @return Returns boolean.
     */
    boolean equals(String uri, String prefix);

    /**
     * Get the namespace prefix. This method may return <code>null</code> for instances returned by
     * {@link OMFactory#createOMNamespace(String, String)}. This indicates that Axiom should
     * generate a namespace prefix when this instance is passed to one of the factory methods in
     * {@link OMFactory}. In all other cases the return value is not null. In particular, an empty
     * string indicates that no prefix is used, i.e. that the namespace is used as default
     * namespace.
     *
     * @return the namespace prefix
     */
    String getPrefix();

    /**
     * Method getName.
     *
     * @return Returns String.
     * @deprecated This method is deprecated. Please use getNamespaceURI() method instead.
     */
    String getName();

    /**
     * Get the namespace URI. This method never returns <code>null</code>. It may return an empty
     * string if this instance represents a namespace declaration of type {@code xmlns=""}. This may
     * be the case for instances returned by {@link OMElement#getAllDeclaredNamespaces()}. On the
     * other hand, methods such as {@link OMNamedInformationItem#getNamespace()} will return <code>
     * null</code> for information items that have no namespace. In that case the returned string is
     * never empty.
     *
     * @return the namespace URI
     */
    String getNamespaceURI();
}
