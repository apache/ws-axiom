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

package org.apache.axiom.testutils.namespace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public final class NamespaceContextTestUtils {
    private NamespaceContextTestUtils() {}

    public static void checkImplicitNamespaces(NamespaceContext nc) {
        assertThat(nc.getNamespaceURI(XMLConstants.XML_NS_PREFIX)).isEqualTo(XMLConstants.XML_NS_URI);
        assertThat(nc.getNamespaceURI(XMLConstants.XMLNS_ATTRIBUTE)).isEqualTo(XMLConstants.XMLNS_ATTRIBUTE_NS_URI);

        assertThat(nc.getPrefix(XMLConstants.XML_NS_URI)).isEqualTo(XMLConstants.XML_NS_PREFIX);
        assertThat(nc.getPrefix(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)).isEqualTo(XMLConstants.XMLNS_ATTRIBUTE);

        Iterator<?> it = nc.getPrefixes(XMLConstants.XML_NS_URI);
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isEqualTo(XMLConstants.XML_NS_PREFIX);
        assertThat(it.hasNext()).isFalse();

        it = nc.getPrefixes(XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isEqualTo(XMLConstants.XMLNS_ATTRIBUTE);
        assertThat(it.hasNext()).isFalse();
    }
}
