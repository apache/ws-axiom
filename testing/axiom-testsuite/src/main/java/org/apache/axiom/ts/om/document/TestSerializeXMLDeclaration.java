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
package org.apache.axiom.ts.om.document;

import static com.google.common.truth.Truth.assertThat;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMMetaFactory;

public class TestSerializeXMLDeclaration extends XMLDeclarationSerializationTestCase {
    public TestSerializeXMLDeclaration(OMMetaFactory metaFactory) {
        super(metaFactory);
    }

    @Override
    protected void runTest(OMDocument document) throws Throwable {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.serializeAndConsume(baos);

        String xmlDocument = new String(baos.toByteArray(), StandardCharsets.UTF_8);

        assertThat(xmlDocument).startsWith("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
    }
}
