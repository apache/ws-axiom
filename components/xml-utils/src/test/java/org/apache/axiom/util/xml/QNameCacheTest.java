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
package org.apache.axiom.util.xml;

import static org.assertj.core.api.Assertions.assertThat;

import javax.xml.namespace.QName;

import org.junit.jupiter.api.Test;

public class QNameCacheTest {
    @Test
    public void testGetQName() {
        QName qname = QNameCache.getQName("urn:ns1", "somename", "ns1");
        assertThat(qname.getNamespaceURI()).isEqualTo("urn:ns1");
        assertThat(qname.getLocalPart()).isEqualTo("somename");
        assertThat(qname.getPrefix()).isEqualTo("ns1");
    }

    @Test
    public void testCached() {
        QName[] qnames = new QName[2];
        for (int i = 0; i < 2; i++) {
            qnames[i] = QNameCache.getQName("urn:test", "test", "p");
        }
        assertThat(qnames[1]).isSameAs(qnames[0]);
    }

    @Test
    public void testPrefixIsRelevant() {
        QName qname1 = QNameCache.getQName("urn:ns2", "foo", "p");
        QName qname2 = QNameCache.getQName("urn:ns2", "foo", "");
        assertThat(qname2).isNotSameAs(qname1);
    }
}
