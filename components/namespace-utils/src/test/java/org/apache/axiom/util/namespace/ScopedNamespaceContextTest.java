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

package org.apache.axiom.util.namespace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import org.apache.axiom.testutils.namespace.NamespaceContextTestUtils;
import org.junit.jupiter.api.Test;

public class ScopedNamespaceContextTest {
    private static Set<String> getPrefixes(NamespaceContext nc, String namespaceURI) {
        Set<String> result = new HashSet<>();
        for (Iterator<?> it = nc.getPrefixes(namespaceURI); it.hasNext(); ) {
            result.add((String) it.next());
        }
        return result;
    }

    @Test
    public void testSimple() {
        ScopedNamespaceContext nc = new ScopedNamespaceContext();
        nc.setPrefix("", "urn:ns1");
        nc.setPrefix("a", "urn:ns2");
        nc.setPrefix("b", "urn:ns3");
        assertThat(nc.getNamespaceURI("")).isEqualTo("urn:ns1");
        assertThat(nc.getNamespaceURI("a")).isEqualTo("urn:ns2");
        assertThat(nc.getNamespaceURI("b")).isEqualTo("urn:ns3");
        assertThat(nc.getPrefix("urn:ns1")).isEqualTo("");
        assertThat(nc.getPrefix("urn:ns2")).isEqualTo("a");
        assertThat(nc.getPrefix("urn:ns3")).isEqualTo("b");
        assertThat(getPrefixes(nc, "urn:ns1")).isEqualTo(Collections.singleton(""));
        assertThat(getPrefixes(nc, "urn:ns2")).isEqualTo(Collections.singleton("a"));
        assertThat(getPrefixes(nc, "urn:ns3")).isEqualTo(Collections.singleton("b"));
    }

    @Test
    public void testMultiplePrefixes() {
        ScopedNamespaceContext nc = new ScopedNamespaceContext();
        nc.setPrefix("", "urn:ns1");
        nc.setPrefix("a", "urn:ns2");
        nc.setPrefix("b", "urn:ns1");
        String prefix = nc.getPrefix("urn:ns1");
        assertThat(prefix).isIn("", "b");
        assertThat(getPrefixes(nc, "urn:ns1")).isEqualTo(new HashSet<>(Arrays.asList("", "b")));
    }

    @Test
    public void testScope() {
        ScopedNamespaceContext nc = new ScopedNamespaceContext();
        nc.setPrefix("ns1", "urn:ns1");
        assertThat(nc.getFirstBindingInCurrentScope()).isEqualTo(0);
        assertThat(nc.getBindingsCount()).isEqualTo(1);
        nc.startScope();
        nc.setPrefix("ns2", "urn:ns2");
        assertThat(nc.getFirstBindingInCurrentScope()).isEqualTo(1);
        assertThat(nc.getBindingsCount()).isEqualTo(2);
        nc.startScope();
        nc.setPrefix("ns3", "urn:ns3");
        assertThat(nc.getFirstBindingInCurrentScope()).isEqualTo(2);
        assertThat(nc.getBindingsCount()).isEqualTo(3);
        assertThat(nc.getNamespaceURI("ns1")).isEqualTo("urn:ns1");
        assertThat(nc.getNamespaceURI("ns2")).isEqualTo("urn:ns2");
        assertThat(nc.getNamespaceURI("ns3")).isEqualTo("urn:ns3");
        assertThat(nc.getPrefix(0)).isEqualTo("ns1");
        assertThat(nc.getNamespaceURI(0)).isEqualTo("urn:ns1");
        assertThat(nc.getPrefix(1)).isEqualTo("ns2");
        assertThat(nc.getNamespaceURI(1)).isEqualTo("urn:ns2");
        assertThat(nc.getPrefix(2)).isEqualTo("ns3");
        assertThat(nc.getNamespaceURI(2)).isEqualTo("urn:ns3");
        nc.endScope();
        assertThat(nc.getFirstBindingInCurrentScope()).isEqualTo(1);
        assertThat(nc.getBindingsCount()).isEqualTo(2);
        assertThat(nc.getNamespaceURI("ns1")).isEqualTo("urn:ns1");
        assertThat(nc.getNamespaceURI("ns2")).isEqualTo("urn:ns2");
        assertThat(nc.getNamespaceURI("ns3")).isEqualTo(XMLConstants.NULL_NS_URI);
        nc.endScope();
        assertThat(nc.getFirstBindingInCurrentScope()).isEqualTo(0);
        assertThat(nc.getBindingsCount()).isEqualTo(1);
        assertThat(nc.getNamespaceURI("ns1")).isEqualTo("urn:ns1");
        assertThat(nc.getNamespaceURI("ns2")).isEqualTo(XMLConstants.NULL_NS_URI);
        assertThat(nc.getNamespaceURI("ns3")).isEqualTo(XMLConstants.NULL_NS_URI);
    }

    @Test
    public void testMaskedPrefix() {
        ScopedNamespaceContext nc = new ScopedNamespaceContext();
        nc.setPrefix("p", "urn:ns1");
        nc.startScope();
        nc.setPrefix("p", "urn:ns2");
        assertThat(nc.getNamespaceURI("p")).isEqualTo("urn:ns2");
        assertThat(nc.getPrefix("urn:ns1")).isNull();
        assertThat(getPrefixes(nc, "urn:ns2")).isEqualTo(Collections.singleton("p"));
        assertThat(nc.getPrefixes("urn:ns1").hasNext()).isFalse();
        nc.endScope();
        assertThat(nc.getPrefix("urn:ns1")).isEqualTo("p");
        assertThat(getPrefixes(nc, "urn:ns1")).isEqualTo(Collections.singleton("p"));
    }

    @Test
    public void testImplicitNamespaces() {
        NamespaceContextTestUtils.checkImplicitNamespaces(new ScopedNamespaceContext());
    }
}
