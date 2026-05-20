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

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.inject.Inject;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.junit.jupiter.api.function.Executable;

/**
 * Tests the behavior of {@link SOAPHeader#addHeaderBlock(String, OMNamespace)} when passing an
 * {@link OMNamespace} object with an empty namespace URI.
 */
public class TestAddHeaderBlockWithoutNamespace2 implements Executable {
    @Inject
    private SOAPFactory soapFactory;

    @Override
    public void execute() throws Throwable {
        SOAPEnvelope envelope = soapFactory.createSOAPEnvelope();
        SOAPHeader header = soapFactory.createSOAPHeader(envelope);
        OMNamespace ns = soapFactory.createOMNamespace("", "");
        assertThatThrownBy(() -> header.addHeaderBlock("test", ns)).isInstanceOf(OMException.class);
    }
}
