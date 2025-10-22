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

/** Interface OMDocType */
public interface OMDocType extends OMNode {
    /**
     * Get the root name, i.e. the name immediately following the {@code DOCTYPE} keyword.
     *
     * @return the root name; must not be <code>null</code>
     */
    String getRootName();

    /**
     * Get the public ID of the external subset.
     *
     * @return the public ID, or <code>null</code> if there is no external subset or no public ID
     *     has been specified for the external subset
     */
    String getPublicId();

    /**
     * Get the system ID of the external subset.
     *
     * @return the system ID, or <code>null</code> if there is no external subset
     */
    String getSystemId();

    /**
     * Get the internal subset.
     *
     * @return the internal subset, or <code>null</code> if there is none
     */
    String getInternalSubset();
}
