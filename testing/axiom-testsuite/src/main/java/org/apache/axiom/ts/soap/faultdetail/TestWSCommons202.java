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
package org.apache.axiom.ts.soap.faultdetail;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.junit.jupiter.api.function.Executable;

public class TestWSCommons202 implements Executable {
    @Inject
    private SOAPFactory soapFactory;

    @Override
    public void execute() throws Throwable {
        SOAPFaultDetail soapFaultDetail = soapFactory.createSOAPFaultDetail();
        soapFaultDetail.setText("a");

        assertThat(soapFaultDetail.getText().trim()).isEqualTo("a");
        assertThat(soapFaultDetail.toString()).doesNotContain("aa");

        OMElement omElement = soapFactory.createOMElement("DummyElement", null);
        soapFaultDetail.addChild(omElement);
        omElement.setText("Some text is here");

        assertThat(soapFaultDetail.toString()).contains("Some text is here");
    }
}
