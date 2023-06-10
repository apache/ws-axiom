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

import com.google.common.truth.FailureMetadata;
import com.google.common.truth.Subject;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;

import static com.google.common.truth.Truth.assertAbout;

public final class AxiomTruth {
    private AxiomTruth() {}

    public static OMContainerSubject assertThat(OMContainer target) {
        return assertAbout(
                        new Subject.Factory<OMContainerSubject, OMContainer>() {
                            @Override
                            public OMContainerSubject createSubject(
                                    FailureMetadata metadata, OMContainer actual) {
                                return new OMContainerSubject(metadata, actual);
                            }
                        })
                .that(target);
    }

    public static OMElementSubject assertThat(OMElement target) {
        return assertAbout(
                        new Subject.Factory<OMElementSubject, OMElement>() {
                            @Override
                            public OMElementSubject createSubject(
                                    FailureMetadata metadata, OMElement actual) {
                                return new OMElementSubject(metadata, actual);
                            }
                        })
                .that(target);
    }

    public static OMAttributeSubject assertThat(OMAttribute target) {
        return assertAbout(
                        new Subject.Factory<OMAttributeSubject, OMAttribute>() {
                            @Override
                            public OMAttributeSubject createSubject(
                                    FailureMetadata metadata, OMAttribute actual) {
                                return new OMAttributeSubject(metadata, actual);
                            }
                        })
                .that(target);
    }
}
