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

import org.apache.axiom.soap.SOAPFactory;

/**
 * Abstract class encapsulating a particular object model.
 * It provides instances for plain XML, SOAP 1.1 and SOAP 1.2 object model factories for the
 * given object model implementation. Currently the two OM implementations provided by
 * Axiom are LLOM (linked list) and DOM.
 * <p>
 * The factories returned by {@link #getOMFactory()}, {@link #getSOAP11Factory()} and
 * {@link #getSOAP12Factory()} might be stateless (and thread safe) or not. In the former
 * case the implementation should return the same instance on every invocation, i.e.
 * instantiate the factory for each OM type only once. In the latter case, the implementation
 * must return a new instance on every invocation. In order to work with any OM implementation,
 * code using an implementation of this class must call the relevant method once and only once
 * for every document processed.
 */
// NOTE: It is intentional that this is implemented as an abstract class rather than an interface.
//       Probably there will be a static getInstance() method in the future.
public abstract class OMImplementation {
    /**
     * Get an OM factory instance for the XML infoset model.
     *
     * @return the OM factory instance
     */
    public abstract OMFactory getOMFactory();
    
    /**
     * Get an OM factory instance for the SOAP 1.1 infoset model.
     *
     * @return the OM factory instance
     */
    public abstract SOAPFactory getSOAP11Factory();
    
    /**
     * Get an OM factory instance for the SOAP 1.2 infoset model.
     *
     * @return the OM factory instance
     */
    public abstract SOAPFactory getSOAP12Factory();
}
