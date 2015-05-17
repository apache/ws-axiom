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

import java.util.Iterator;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;

import com.google.common.truth.FailureStrategy;
import com.google.common.truth.IterableSubject;
import com.google.common.truth.TestVerb;
import com.google.common.truth.Truth;

public final class AxiomTestVerb extends TestVerb {
    public static final AxiomTestVerb ASSERT = new AxiomTestVerb(Truth.THROW_ASSERTION_ERROR);
    
    private AxiomTestVerb(FailureStrategy failureStrategy) {
        super(failureStrategy);
    }

    public <T,C extends Iterable<T>> IterableSubject<? extends IterableSubject<?,T,C>,T,C> that(final Iterator<T> target) {
        return that(new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return target;
            }
        });
    }
    
    public OMContainerSubject that(OMContainer target) {
        return new OMContainerSubject(getFailureStrategy(), target);
    }
    
    public OMElementSubject that(OMElement target) {
        return new OMElementSubject(getFailureStrategy(), target);
    }
    
    public OMAttributeSubject that(OMAttribute target) {
        return new OMAttributeSubject(getFailureStrategy(), target);
    }
}
