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
package org.apache.axiom.ts.soap.misc;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.ts.soap.SOAPElementType;
import org.apache.axiom.ts.soap.SOAPElementTypeAdapter;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

public abstract class GetSetChildTestCase extends SOAPTestCase {
    protected final SOAPElementType type;
    protected final SOAPElementType childType;

    public GetSetChildTestCase(
            OMMetaFactory metaFactory,
            SOAPSpec spec,
            SOAPElementType type,
            SOAPElementType childType) {
        super(metaFactory, spec);
        this.type = type;
        this.childType = childType;
        addTestParameter(
                "type", type.getAdapter(SOAPElementTypeAdapter.class).getType().getSimpleName());
        addTestParameter(
                "childType",
                childType.getAdapter(SOAPElementTypeAdapter.class).getType().getSimpleName());
    }

    @Override
    protected final void runTest() throws Throwable {
        runTest(
                type.getAdapter(SOAPElementTypeAdapter.class).create(soapFactory),
                childType.getAdapter(SOAPElementTypeAdapter.class));
    }

    protected abstract void runTest(OMElement parent, SOAPElementTypeAdapter adapter);
}
