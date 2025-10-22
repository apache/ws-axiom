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

/** Interface OMAttribute */
public interface OMAttribute extends OMNamedInformationItem {
    /**
     * Get the attribute value.
     *
     * @return the attribute value; never <code>null</code>
     */
    String getAttributeValue();

    /**
     * @param value
     */
    void setAttributeValue(String value);

    /**
     * Get the attribute type. For most attributes, the type is <code>CDATA</code>.
     *
     * @return the attribute type
     */
    String getAttributeType();

    /**
     * @param value
     */
    void setAttributeType(String value);

    /**
     * @deprecated Use {@link OMNamedInformationItem#setNamespace(OMNamespace, boolean)} instead.
     */
    void setOMNamespace(OMNamespace omNamespace);

    /**
     * Returns the owner element of this attribute
     *
     * @return OMElement - The owner element
     */
    OMElement getOwner();
}
