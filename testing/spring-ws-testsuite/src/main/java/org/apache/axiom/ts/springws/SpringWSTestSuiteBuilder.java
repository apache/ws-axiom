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

import org.apache.axiom.testutils.suite.MatrixTestSuiteBuilder;
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

public class SpringWSTestSuiteBuilder extends MatrixTestSuiteBuilder {
    private final MessageFactoryConfigurator messageFactoryConfigurator;
    private final MessageFactoryConfigurator altMessageFactoryConfigurator;

    public SpringWSTestSuiteBuilder(
            MessageFactoryConfigurator messageFactoryConfigurator,
            MessageFactoryConfigurator altMessageFactoryConfigurator) {
        this.messageFactoryConfigurator = messageFactoryConfigurator;
        this.altMessageFactoryConfigurator = altMessageFactoryConfigurator;
    }

    @Override
    protected void addTests() {
        addSimpleTests(messageFactoryConfigurator, SOAPSpec.SOAP11);
        addSimpleTests(messageFactoryConfigurator, SOAPSpec.SOAP12);
        addTest(new TestCreateWebServiceMessageFromInputStreamMTOM(messageFactoryConfigurator));
        addScenarioTests(
                new ScenarioConfig(altMessageFactoryConfigurator, messageFactoryConfigurator),
                SOAPSpec.SOAP11);
        addScenarioTests(
                new ScenarioConfig(altMessageFactoryConfigurator, messageFactoryConfigurator),
                SOAPSpec.SOAP12);
        if (altMessageFactoryConfigurator != messageFactoryConfigurator) {
            addScenarioTests(
                    new ScenarioConfig(messageFactoryConfigurator, altMessageFactoryConfigurator),
                    SOAPSpec.SOAP11);
            addScenarioTests(
                    new ScenarioConfig(messageFactoryConfigurator, altMessageFactoryConfigurator),
                    SOAPSpec.SOAP12);
        }
    }

    private void addSimpleTests(MessageFactoryConfigurator mfc, SOAPSpec spec) {
        addTest(new TestCreateWebServiceMessage(mfc, spec));
        addTest(new TestCreateWebServiceMessageFromInputStream(mfc, spec));
        addTest(new TestCreateWebServiceMessageFromInputStreamVersionMismatch(mfc, spec));
    }

    private void addScenarioTests(ScenarioConfig config, SOAPSpec spec) {
        addTest(new ClientServerTest(config, spec));
        addTest(new WSAddressingDOMTest(config, spec));
        addTest(new JAXB2Test(config, spec));
        addTest(new BrokerScenarioTest(config, spec));
        addTest(new ValidationTest(config, spec));
        addTest(new SecureEchoTest(config, spec));
        addTest(new SoapActionTest(config, spec));
    }
}
