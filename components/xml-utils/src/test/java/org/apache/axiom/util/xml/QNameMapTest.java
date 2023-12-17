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
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.xml.namespace.QName;

import org.junit.jupiter.api.Test;

public class QNameMapTest {
    @Test
    public void testGetWithNullNamespaceURI() {
        QNameMap<String> map = new QNameMap<String>();
        map.put(new QName(null, "name"), "value");
        assertThat(map.get(null, "name")).isEqualTo("value");
    }

    @Test
    public void testGetWithNullLocalPart() {
        assertThrows(
                IllegalArgumentException.class, () -> new QNameMap<Object>().get("urn:test", null));
    }

    @Test
    public void testReplaceExisting() {
        QNameMap<String> map = new QNameMap<String>();
        map.put(new QName("urn:test", "name"), "value1");
        map.put(new QName("urn:test", "name"), "value2");
        assertThat(map.get("urn:test", "name")).isEqualTo("value2");
    }

    @Test
    public void testHashCollision() {
        QNameMap<String> map = new QNameMap<String>();
        map.put(new QName("a", "b"), "value1");
        map.put(new QName("b", "a"), "value2");
        assertThat(map.get("a", "b")).isEqualTo("value1");
        assertThat(map.get("b", "a")).isEqualTo("value2");
    }
}
