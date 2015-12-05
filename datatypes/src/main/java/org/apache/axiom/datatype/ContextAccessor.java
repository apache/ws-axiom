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
package org.apache.axiom.datatype;

public interface ContextAccessor<T,O> {
    /**
     * Look up the namespace URI associated to the given prefix.
     * 
     * @param contextObject
     *            The context object passed to
     *            {@link Type#parse(String, ContextAccessor, Object, Object)} or
     *            {@link Type#format(Object, ContextAccessor, Object, Object)}.
     * @param options
     *            The options passed to {@link Type#parse(String, ContextAccessor, Object, Object)}
     *            or {@link Type#format(Object, ContextAccessor, Object, Object)}.
     * @param prefix
     *            The prefix to look for. If this parameter is the empty string, then the URI of the
     *            default namespace will be returned.
     * @return the namespace URI or <code>null</code> if the prefix is not bound; if the prefix is
     *         the empty string and no default namespace declaration exists, then an empty string is
     *         returned
     */
    String lookupNamespaceURI(T contextObject, O options, String prefix);
    
    String lookupPrefix(T contextObject, O options, String namespaceURI);
    
    void declareNamespace(T contextObject, O options, String prefix, String namespaceURI);
}
