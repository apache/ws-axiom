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
package org.apache.axiom.ts.omdom.text;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.testutils.blob.RandomBlob;
import org.apache.axiom.ts.AxiomTestCase;
import org.w3c.dom.Text;

public class TestCloneNodeBinary extends AxiomTestCase {
    public TestCloneNodeBinary(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest() throws Throwable {
        OMFactory factory = metaFactory.getOMFactory();
        Blob blob = new RandomBlob(666L, 1000);
        Text text = (Text) factory.createOMText(blob, false);
        String base64 = text.getData();
        assertTrue(base64.length() > 0);
        assertEquals(base64, ((Text) text.cloneNode(true)).getData());
    }
}
