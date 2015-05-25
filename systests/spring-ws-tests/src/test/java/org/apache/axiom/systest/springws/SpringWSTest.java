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
package org.apache.axiom.systest.springws;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.axiom.ts.springws.MessageFactoryConfigurator;
import org.apache.axiom.ts.springws.SpringWSTestSuiteBuilder;
import org.apache.axiom.ts.springws.scenario.broker.BrokerScenarioTest;
import org.apache.axiom.ts.springws.scenario.castor.CastorTest;
import org.apache.axiom.ts.springws.scenario.jaxb2.JAXB2Test;
import org.apache.axiom.ts.springws.scenario.jdom.ClientServerTest;
import org.apache.axiom.ts.springws.scenario.secureecho.SecureEchoTest;

public class SpringWSTest extends TestCase {
    public static TestSuite suite() {
        SpringWSTestSuiteBuilder builder = new SpringWSTestSuiteBuilder(
                new AxiomMessageFactoryConfigurator(),
                MessageFactoryConfigurator.SAAJ);
        
        // TODO: investigate
        builder.exclude(ClientServerTest.class);
        builder.exclude(JAXB2Test.class);
        builder.exclude(CastorTest.class);
        builder.exclude(BrokerScenarioTest.class);
        builder.exclude(SecureEchoTest.class);
        
        return builder.build();
    }
}
