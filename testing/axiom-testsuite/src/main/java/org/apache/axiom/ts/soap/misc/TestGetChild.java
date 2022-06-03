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

public class TestGetChild extends GetSetChildTestCase {
    public TestGetChild(
            OMMetaFactory metaFactory,
            SOAPSpec spec,
            SOAPElementType type,
            SOAPElementType childType) {
        super(metaFactory, spec, type, childType);
    }

    @Override
    protected void runTest(OMElement parent, SOAPElementTypeAdapter adapter) {
        assertNull(adapter.getGetter().invoke(parent));
        OMElement child = adapter.create(soapFactory, type, parent);
        assertSame(child, adapter.getGetter().invoke(parent));
    }
}
