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
import static com.google.common.truth.Truth.assertThat;

import javax.activation.DataHandler;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.testutils.activation.TestDataSource;
import org.apache.synapse.util.PayloadHelper;
import org.junit.Test;

public class DataHandlerTest {
    private void test(String feature) {
        DataHandler dh = new DataHandler(new TestDataSource('x', 1000));
        SOAPEnvelope envelope = OMAbstractFactory.getMetaFactory(feature).getSOAP11Factory().createDefaultSOAPMessage().getSOAPEnvelope();
        PayloadHelper.setBinaryPayload(envelope, dh);
        assertThat(PayloadHelper.getBinaryPayload(envelope)).isSameAs(dh);
    }

    @Test
    public void testLLOM() {
        test(OMAbstractFactory.FEATURE_DEFAULT);
    }

    @Test
    public void testDOOM() {
        test(OMAbstractFactory.FEATURE_DOM);
    }
}
