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
package org.apache.axiom.truth.xml;

import static com.google.common.truth.Truth.assertThat;

import org.apache.axiom.truth.xml.spi.Event;
import org.apache.axiom.truth.xml.spi.Traverser;
import org.apache.axiom.ts.xml.XMLSample;
import org.junit.Test;

public class DOMTraverserTest {
    @Test
    public void testEntityReferenceExpansion() throws Exception {
        Traverser t =
                new CoalescingFilter(
                        new DOMXML(XMLSample.ENTITY_REFERENCE_NESTED.getDocument())
                                .createTraverser(true));
        assertThat(t.next()).isEqualTo(Event.DOCUMENT_TYPE);
        assertThat(t.next()).isEqualTo(Event.START_ELEMENT);
        assertThat(t.next()).isEqualTo(Event.TEXT);
        assertThat(t.getText().trim()).isEqualTo("A B C");
        assertThat(t.next()).isEqualTo(Event.END_ELEMENT);
        assertThat(t.next()).isNull();
    }
}
