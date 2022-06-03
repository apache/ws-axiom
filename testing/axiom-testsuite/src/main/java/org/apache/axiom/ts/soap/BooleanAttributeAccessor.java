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
package org.apache.axiom.ts.soap;

import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.testing.multiton.AdapterType;

/**
 * {@link HeaderBlockAttribute} adapter that allows to invoke the getter and setter methods for the
 * attribute on a given {@link SOAPHeaderBlock}.
 */
@AdapterType
public interface BooleanAttributeAccessor {
    /**
     * Invoke the getter method for this attribute on the given {@link SOAPHeaderBlock}.
     *
     * @param headerBlock the header block
     * @return the value returned by the getter method
     */
    boolean getValue(SOAPHeaderBlock headerBlock);

    /**
     * Invoke the setter method for this attribute on the given {@link SOAPHeaderBlock}.
     *
     * @param headerBlock the heaer block
     * @param value the value to pass to the setter
     */
    void setValue(SOAPHeaderBlock headerBlock, boolean value);
}
