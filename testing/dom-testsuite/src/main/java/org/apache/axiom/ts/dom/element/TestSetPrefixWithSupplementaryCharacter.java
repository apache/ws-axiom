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
package org.apache.axiom.ts.dom.element;

import static org.assertj.core.api.Assertions.assertThat;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.ts.dom.DOMTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TestSetPrefixWithSupplementaryCharacter extends DOMTestCase {
    public TestSetPrefixWithSupplementaryCharacter(DocumentBuilderFactory dbf) {
        super(dbf);
    }

    @Override
    protected void runTest() throws Throwable {
        String prefix = new String(new int[] {0x10000}, 0, 1);
        Document doc = dbf.newDocumentBuilder().newDocument();
        Element elem = doc.createElementNS("http://example.org", "foobar");
        elem.setPrefix(prefix);
        assertThat(elem.getPrefix()).isEqualTo(prefix);
    }
}
