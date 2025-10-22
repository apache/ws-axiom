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
 * Object model meta factory. This interface encapsulates a particular Axiom implementation and
 * provides instances for plain XML, SOAP 1.1 and SOAP 1.2 object model factories for that
 * implementation. Currently the two OM implementations provided by Axiom are LLOM (linked list) and
 * DOOM (DOM compatible).
 *
 * <p>The factories returned by {@link #getOMFactory()}, {@link #getSOAP11Factory()} and {@link
 * #getSOAP12Factory()} MUST be stateless (and thread safe). The implementation MUST return the same
 * instance on every invocation, i.e. instantiate the factory for each OM type only once.
 */
public interface OMMetaFactory {
    /**
     * Get the OM factory instance for the XML infoset model.
     *
     * @return the OM factory instance
     */
    OMFactory getOMFactory();

    /**
     * Get the OM factory instance for the SOAP 1.1 infoset model.
     *
     * @return the OM factory instance
     */
    SOAPFactory getSOAP11Factory();

    /**
     * Get the OM factory instance for the SOAP 1.2 infoset model.
     *
     * @return the OM factory instance
     */
    SOAPFactory getSOAP12Factory();
}
