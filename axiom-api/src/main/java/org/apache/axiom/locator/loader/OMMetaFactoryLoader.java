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
package org.apache.axiom.locator.loader;

import java.util.Map;

import org.apache.axiom.om.OMMetaFactory;

/**
 * Loads the {@link OMMetaFactory} for a given Axiom implementation. An Axiom implementation must
 * provide an implementation of this interface. That implementation class is specified in the {@code
 * META-INF/axiom.xml} of the Axiom implementation.
 */
public interface OMMetaFactoryLoader {
    /**
     * Get the {@link OMMetaFactory} instance for the Axiom implementation. Note that the
     * implementation is not required to return the same instance on every invocation. It is the
     * responsibility of the Axiom API to cache the instance if necessary.
     *
     * @param properties reserved for future use
     * @return the {@link OMMetaFactory} instance; must not be <code>null</code>
     */
    OMMetaFactory load(Map<String, Object> properties);
}
