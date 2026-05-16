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
package org.apache.axiom.ts.om.element;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.xml.namespace.NamespaceContext;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.ts.AxiomTestCase;

/** Tests {@link OMElement#getNamespaceContext(boolean)}. */
public class TestGetNamespaceContext extends AxiomTestCase {
    @Inject
    private OMMetaFactory metaFactory;

    private final boolean detached;

    @Inject
    public TestGetNamespaceContext(@Named("detached") boolean detached) {
        this.detached = detached;
    }

    @Override
    protected void runTest() throws Throwable {
        InputStream in = TestGetNamespaceContext.class.getResourceAsStream("namespacecontext.xml");
        OMElement root = OMXMLBuilderFactory.createOMBuilder(metaFactory.getOMFactory(), in)
                .getDocumentElement();
        OMElement inner = root.getFirstElement().getFirstElement();
        NamespaceContext context = inner.getNamespaceContext(detached);
        assertThat(context.getNamespaceURI("p")).isEqualTo("urn:test2");
        assertThat(context.getNamespaceURI("q")).isEqualTo("urn:test3");
        assertThat(context.getNamespaceURI("r")).isEqualTo("urn:test3");
        assertThat(context.getNamespaceURI("")).isEqualTo("urn:test4");
        assertThat(context.getNamespaceURI("unbound")).isEqualTo("");

        assertThat(context.getPrefix("urn:test1")).isNull();
        assertThat(context.getPrefix("urn:test2")).isEqualTo("p");
        String prefix = context.getPrefix("urn:test3");
        assertThat(prefix.equals("q") || prefix.equals("r")).isTrue();
        assertThat(context.getPrefix("urn:test4")).isEqualTo("");
        assertThat(context.getPrefix("unbound")).isNull();

        Iterator<?> it = context.getPrefixes("urn:test1");
        assertThat(it.hasNext()).isFalse();

        it = context.getPrefixes("urn:test2");
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isEqualTo("p");
        assertThat(it.hasNext()).isFalse();

        it = context.getPrefixes("urn:test3");
        Set<String> prefixes = new HashSet<>();
        while (it.hasNext()) {
            prefixes.add((String) it.next());
        }
        assertThat(prefixes.size()).isEqualTo(2);
        assertThat(prefixes.contains("q")).isTrue();
        assertThat(prefixes.contains("r")).isTrue();
    }
}
