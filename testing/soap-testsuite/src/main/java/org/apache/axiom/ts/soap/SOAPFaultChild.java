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

import javax.xml.namespace.QName;

public abstract class SOAPFaultChild extends SOAPElementType {
    public static final SOAPFaultChild CODE =
            new SOAPFaultChild() {
                @Override
                public QName getQName(SOAPSpec spec) {
                    return spec.getFaultCodeQName();
                }

                @Override
                public int getOrder() {
                    return 1;
                }
            };

    public static final SOAPFaultChild REASON =
            new SOAPFaultChild() {
                @Override
                public QName getQName(SOAPSpec spec) {
                    return spec.getFaultReasonQName();
                }

                @Override
                public int getOrder() {
                    return 2;
                }
            };

    public static final SOAPFaultChild NODE =
            new SOAPFaultChild() {
                @Override
                public QName getQName(SOAPSpec spec) {
                    return spec.getFaultNodeQName();
                }

                @Override
                public int getOrder() {
                    return 3;
                }
            };

    public static final SOAPFaultChild ROLE =
            new SOAPFaultChild() {
                @Override
                public QName getQName(SOAPSpec spec) {
                    return spec.getFaultRoleQName();
                }

                @Override
                public int getOrder() {
                    return 4;
                }
            };

    public static final SOAPFaultChild DETAIL =
            new SOAPFaultChild() {
                @Override
                public QName getQName(SOAPSpec spec) {
                    return spec.getFaultDetailQName();
                }

                @Override
                public int getOrder() {
                    return 5;
                }
            };

    private SOAPFaultChild() {}

    public abstract int getOrder();
}
