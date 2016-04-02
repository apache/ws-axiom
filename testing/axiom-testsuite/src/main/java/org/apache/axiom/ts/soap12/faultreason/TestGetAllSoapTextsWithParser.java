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
package org.apache.axiom.ts.soap12.faultreason;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.ts.soap.SOAPSample;
import org.apache.axiom.ts.soap.SampleBasedSOAPTestCase;

public class TestGetAllSoapTextsWithParser extends SampleBasedSOAPTestCase {
    public TestGetAllSoapTextsWithParser(OMMetaFactory metaFactory) {
        super(metaFactory, SOAPSample.SOAP12_FAULT);
    }

    @Override
    protected void runTest(SOAPEnvelope envelope) throws Throwable {
        List<SOAPFaultText> texts = envelope.getBody().getFault().getReason().getAllSoapTexts();
        assertThat(texts).hasSize(2);
        assertThat(texts.get(0).getLang()).isEqualTo("en");
        assertThat(texts.get(0).getText()).isEqualTo("Sender Timeout");
        assertThat(texts.get(1).getLang()).isEqualTo("de");
        assertThat(texts.get(1).getText()).isEqualTo("Senderseitige Zeitüberschreitung");
    }
}
