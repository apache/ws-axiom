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
package org.apache.axiom.truth;

import org.apache.axiom.om.OMAttribute;

import com.google.common.base.Objects;
import com.google.common.truth.FailureStrategy;
import com.google.common.truth.Subject;

public final class OMAttributeSubject extends Subject<OMAttributeSubject,OMAttribute>{
    OMAttributeSubject(FailureStrategy failureStrategy, OMAttribute subject) {
        super(failureStrategy, subject);
    }

    public void hasValue(String expected) {
        if (!Objects.equal(getSubject().getAttributeValue(), expected)) {
            fail("has value", expected);
        }
    }

    public void hasPrefix(String expected) {
        if (!Objects.equal(getSubject().getPrefix(), expected)) {
            fail("has prefix", expected);
        }
    }
}
