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
package org.apache.axiom.ts.saaj.body;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPBodyElement;
import jakarta.xml.soap.SOAPElement;
import org.junit.jupiter.api.function.Executable;

public class TestAddChildElementReification implements Executable {
    @Inject
    private MessageFactory messageFactory;

    @Override
    public void execute() throws Throwable {
        SOAPBody body = messageFactory.createMessage().getSOAPBody();
        SOAPElement child =
                body.addChildElement((SOAPElement) body.getOwnerDocument().createElementNS("urn:test", "p:test"));
        assertThat(child).isInstanceOf(SOAPBodyElement.class);
    }
}
