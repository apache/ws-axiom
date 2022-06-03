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
package org.apache.axiom.ts.om.attribute;

import org.apache.axiom.om.OMInformationItem;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.ts.om.DigestTestCase;

// Regression test for AXIOM-203
public class TestDigestWithoutNamespace extends DigestTestCase {
    public TestDigestWithoutNamespace(OMMetaFactory metaFactory) {
        super(metaFactory, "MD5", "78f0b570fedc6e01679f501b7b75d069");
    }

    @Override
    protected OMInformationItem createInformationItem() {
        return metaFactory.getOMFactory().createOMAttribute("attr", null, "value");
    }
}
