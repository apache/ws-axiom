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
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.junit.jupiter.api.function.Executable;

public class TestExtractAllHeaderBlocks implements Executable {
    @Inject
    private SOAPFactory soapFactory;

    @Override
    public void execute() throws Throwable {
        SOAPEnvelope envelope = soapFactory.createSOAPEnvelope();
        SOAPHeader header = soapFactory.createSOAPHeader(envelope);
        OMNamespace ns = soapFactory.createOMNamespace("urn:ns", "p");
        SOAPHeaderBlock h1 = header.addHeaderBlock("header1", ns);
        SOAPHeaderBlock h2 = header.addHeaderBlock("header2", ns);
        Iterator<SOAPHeaderBlock> it = header.extractAllHeaderBlocks();
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isSameAs(h1);
        assertThat(it.hasNext()).isTrue();
        assertThat(it.next()).isSameAs(h2);
        assertThat(it.hasNext()).isFalse();
    }
}
