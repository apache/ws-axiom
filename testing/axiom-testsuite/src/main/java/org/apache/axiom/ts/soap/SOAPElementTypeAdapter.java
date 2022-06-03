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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.testing.multiton.AdapterType;
import org.apache.axiom.testutils.suite.Dimension;
import org.apache.axiom.testutils.suite.MatrixTestCase;

@AdapterType
public abstract class SOAPElementTypeAdapter implements Dimension {
    public interface Getter {
        OMElement invoke(OMElement parent);
    }

    public interface Setter {
        void invoke(OMElement parent, OMElement child);
    }

    private final Class<? extends OMElement> type;
    private final Getter getter;
    private final Setter setter;

    SOAPElementTypeAdapter(Class<? extends OMElement> type, Getter getter, Setter setter) {
        this.type = type;
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public final void addTestParameters(MatrixTestCase testCase) {
        testCase.addTestParameter("type", type.getSimpleName());
    }

    public final Class<? extends OMElement> getType() {
        return type;
    }

    public final Getter getGetter() {
        return getter;
    }

    public final Setter getSetter() {
        return setter;
    }

    public abstract OMElement create(SOAPFactory factory);

    public abstract OMElement create(
            SOAPFactory factory, SOAPElementType parentType, OMElement parent);
}
