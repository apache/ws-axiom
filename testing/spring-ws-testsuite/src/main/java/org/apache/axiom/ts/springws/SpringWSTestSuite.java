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
package org.apache.axiom.ts.springws;

import org.apache.axiom.testing.multiton.Multiton;
import org.apache.axiom.testutils.suite.DimensionFanOutNode;
import org.apache.axiom.testutils.suite.InjectorNode;
import org.apache.axiom.testutils.suite.MatrixTest;
import org.apache.axiom.testutils.suite.MatrixTestNode;
import org.apache.axiom.testutils.suite.ParameterFanOutNode;
import org.apache.axiom.testutils.suite.ParentNode;
import org.apache.axiom.testutils.suite.SelectorNode;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.springws.scenario.ScenarioConfig;
import org.apache.axiom.ts.springws.scenario.broker.BrokerScenarioTest;
import org.apache.axiom.ts.springws.scenario.jaxb2.JAXB2Test;
import org.apache.axiom.ts.springws.scenario.jdom.ClientServerTest;
import org.apache.axiom.ts.springws.scenario.secureecho.SecureEchoTest;
import org.apache.axiom.ts.springws.scenario.soapaction.SoapActionTest;
import org.apache.axiom.ts.springws.scenario.validation.ValidationTest;
import org.apache.axiom.ts.springws.scenario.wsadom.WSAddressingDOMTest;
import org.apache.axiom.ts.springws.soap.messagefactory.TestCreateWebServiceMessage;
import org.apache.axiom.ts.springws.soap.messagefactory.TestCreateWebServiceMessageFromInputStream;
import org.apache.axiom.ts.springws.soap.messagefactory.TestCreateWebServiceMessageFromInputStreamMTOM;
import org.apache.axiom.ts.springws.soap.messagefactory.TestCreateWebServiceMessageFromInputStreamVersionMismatch;

import com.google.common.collect.ImmutableList;

public class SpringWSTestSuite {
    public static MatrixTestNode create(
            MessageFactoryConfigurator messageFactoryConfigurator,
            MessageFactoryConfigurator altMessageFactoryConfigurator) {
        ImmutableList.Builder<ScenarioConfig> configs = ImmutableList.builder();
        configs.add(new ScenarioConfig(altMessageFactoryConfigurator, messageFactoryConfigurator));
        if (altMessageFactoryConfigurator != messageFactoryConfigurator) {
            configs.add(
                    new ScenarioConfig(messageFactoryConfigurator, altMessageFactoryConfigurator));
        }

        return new ParameterFanOutNode<>(
                SOAPSpec.class,
                Multiton.getInstances(SOAPSpec.class),
                "soapVersion",
                spec -> spec.getAdapter(SOAPSpecAdapter.class).getSoapVersion(),
                new ParentNode(
                        new InjectorNode(
                                binder ->
                                        binder.bind(MessageFactoryConfigurator.class)
                                                .toInstance(messageFactoryConfigurator),
                                new ParentNode(
                                        new MatrixTest(TestCreateWebServiceMessage.class),
                                        new MatrixTest(
                                                TestCreateWebServiceMessageFromInputStream.class),
                                        new MatrixTest(
                                                TestCreateWebServiceMessageFromInputStreamVersionMismatch
                                                        .class),
                                        new SelectorNode(
                                                "soapVersion",
                                                "SOAP_12",
                                                new MatrixTest(
                                                        TestCreateWebServiceMessageFromInputStreamMTOM
                                                                .class)))),
                        new DimensionFanOutNode<>(
                                ScenarioConfig.class,
                                configs.build(),
                                new ParentNode(
                                        new MatrixTest(ClientServerTest.class),
                                        new MatrixTest(WSAddressingDOMTest.class),
                                        new MatrixTest(JAXB2Test.class),
                                        new MatrixTest(BrokerScenarioTest.class),
                                        new MatrixTest(ValidationTest.class),
                                        new MatrixTest(SecureEchoTest.class),
                                        new MatrixTest(SoapActionTest.class)))));
    }
}
