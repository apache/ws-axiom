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

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.testing.multiton.Multiton;

public abstract class SOAPElementType extends Multiton {
    public static final SOAPElementType ENVELOPE =
            new SOAPElementType() {
                @Override
                public QName getQName(SOAPSpec spec) {
                    return spec.getEnvelopeQName();
                }
            };

    public static final SOAPElementType HEADER =
            new SOAPElementType() {
                @Override
                public QName getQName(SOAPSpec spec) {
                    return spec.getHeaderQName();
                }
            };

    public static final SOAPElementType BODY =
            new SOAPElementType() {
                @Override
                public QName getQName(SOAPSpec spec) {
                    return spec.getBodyQName();
                }
            };

    public static final SOAPElementType FAULT =
            new SOAPElementType() {
                @Override
                public QName getQName(SOAPSpec spec) {
                    return spec.getFaultQName();
                }
            };

    public static final SOAPElementType VALUE =
            new SOAPElementType() {
                @Override
                public QName getQName(SOAPSpec spec) {
                    return spec.getFaultValueQName();
                }
            };

    public static final SOAPElementType SUB_CODE =
            new SOAPElementType() {
                @Override
                public QName getQName(SOAPSpec spec) {
                    return spec.getFaultSubCodeQName();
                }
            };

    public static final SOAPElementType TEXT =
            new SOAPElementType() {
                @Override
                public QName getQName(SOAPSpec spec) {
                    return spec.getFaultTextQName();
                }
            };

    private static final SOAPElementType[] allTypes = {
        SOAPElementType.ENVELOPE,
        SOAPElementType.HEADER,
        SOAPElementType.BODY,
        SOAPElementType.FAULT,
        SOAPFaultChild.CODE,
        SOAPElementType.VALUE,
        SOAPElementType.SUB_CODE,
        SOAPFaultChild.REASON,
        SOAPElementType.TEXT,
        SOAPFaultChild.NODE,
        SOAPFaultChild.ROLE,
        SOAPFaultChild.DETAIL,
    };

    static {
        addRelation(ENVELOPE, HEADER);
        addRelation(ENVELOPE, BODY);
        addRelation(BODY, FAULT);
        addRelation(FAULT, SOAPFaultChild.CODE);
        addRelation(SOAPFaultChild.CODE, SOAPFaultChild.VALUE);
        addRelation(SOAPFaultChild.CODE, SOAPFaultChild.SUB_CODE);
        addRelation(SOAPFaultChild.SUB_CODE, SOAPFaultChild.VALUE);
        addRelation(SOAPFaultChild.SUB_CODE, SOAPFaultChild.SUB_CODE);
        addRelation(FAULT, SOAPFaultChild.REASON);
        addRelation(SOAPFaultChild.REASON, SOAPFaultChild.TEXT);
        addRelation(FAULT, SOAPFaultChild.NODE);
        addRelation(FAULT, SOAPFaultChild.ROLE);
        addRelation(FAULT, SOAPFaultChild.DETAIL);
    }

    private final List<SOAPElementType> parentTypes = new ArrayList<>();
    private final List<SOAPElementType> childTypes = new ArrayList<>();

    SOAPElementType() {}

    private static void addRelation(SOAPElementType parentType, SOAPElementType childType) {
        parentType.childTypes.add(childType);
        childType.parentTypes.add(parentType);
    }

    public static SOAPElementType[] getAll() {
        return allTypes.clone();
    }

    /**
     * Get the qualified name for this element type in the given SOAP version.
     *
     * @param spec the SOAP version
     * @return the element name, or <code>null</code> if the element type doesn't exist in the given
     *     SOAP version
     */
    public abstract QName getQName(SOAPSpec spec);

    public SOAPElementType[] getParentTypes() {
        return parentTypes.toArray(new SOAPElementType[parentTypes.size()]);
    }

    public SOAPElementType[] getChildTypes() {
        return childTypes.toArray(new SOAPElementType[childTypes.size()]);
    }
}
