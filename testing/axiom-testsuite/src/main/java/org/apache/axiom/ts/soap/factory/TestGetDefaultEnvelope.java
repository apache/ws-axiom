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
package org.apache.axiom.ts.soap.factory;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import java.util.Iterator;
import junit.framework.TestCase;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.ts.soap.SOAPSpec;

/** Checks the content of the SOAP envelope returned by {@link SOAPFactory#getDefaultEnvelope()}. */
public class TestGetDefaultEnvelope extends TestCase {
    @Inject
    private SOAPSpec spec;

    @Inject
    private SOAPFactory soapFactory;

    @Override
    protected void runTest() throws Throwable {
        SOAPEnvelope env = soapFactory.getDefaultEnvelope();

        // Check correct SOAP version
        assertThat(env.getNamespaceURI()).isEqualTo(spec.getEnvelopeNamespaceURI());

        // getDefaultEnvelope doesn't create a SOAPMessage/OMDocument
        assertThat(env.getParent()).isNull();

        // Check the children
        Iterator<OMNode> it = env.getChildren();
        assertThat(it.hasNext()).isTrue();
        OMNode child = it.next();
        assertThat(child).isInstanceOf(SOAPHeader.class);
        assertThat(((SOAPHeader) child).getFirstOMChild()).isNull();
        child = it.next();
        assertThat(child).isInstanceOf(SOAPBody.class);
        assertThat(((SOAPBody) child).getFirstOMChild()).isNull();
        assertThat(it.hasNext()).isFalse();
    }
}
