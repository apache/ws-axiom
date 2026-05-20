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
package org.apache.axiom.ts.soap.header;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import java.util.Iterator;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.junit.jupiter.api.function.Executable;

public class TestAddHeaderBlock implements Executable {
    @Inject
    private SOAPFactory soapFactory;

    @Override
    public void execute() throws Throwable {
        SOAPEnvelope soapEnvelope = soapFactory.createSOAPEnvelope();
        SOAPHeader soapHeader = soapFactory.createSOAPHeader(soapEnvelope);
        OMNamespace namespace = soapFactory.createOMNamespace("http://www.example.org", "test");
        SOAPHeaderBlock headerBlock1 = soapHeader.addHeaderBlock("echoOk1", namespace);
        SOAPHeaderBlock headerBlock2 = soapHeader.addHeaderBlock("echoOk2", namespace);
        Iterator<OMNode> iterator = soapHeader.getChildren();
        assertThat(iterator.next()).isSameAs(headerBlock1);
        assertThat(headerBlock1.getParent()).isSameAs(soapHeader);
        assertThat(headerBlock1).isNotNull();
        assertThat(headerBlock1.getLocalName()).isEqualTo("echoOk1");
        assertThat(headerBlock1.getNamespace().getNamespaceURI()).isEqualTo("http://www.example.org");

        assertThat(iterator.next()).isSameAs(headerBlock2);
        assertThat(headerBlock2.getParent()).isSameAs(soapHeader);
        assertThat(headerBlock2).isNotNull();
        assertThat(headerBlock2.getLocalName()).isEqualTo("echoOk2");
        assertThat(headerBlock2.getNamespace().getNamespaceURI()).isEqualTo("http://www.example.org");

        assertThat(iterator.hasNext()).isFalse();
    }
}
