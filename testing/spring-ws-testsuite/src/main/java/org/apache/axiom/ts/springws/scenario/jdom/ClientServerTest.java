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
package org.apache.axiom.ts.springws.scenario.jdom;

import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.springws.scenario.ScenarioConfig;
import org.apache.axiom.ts.springws.scenario.ScenarioTestCase;
import org.jdom2.input.SAXBuilder;
import org.jdom2.transform.JDOMResult;
import org.jdom2.transform.JDOMSource;
import org.springframework.ws.client.core.WebServiceTemplate;

public class ClientServerTest extends ScenarioTestCase {
    public ClientServerTest(ScenarioConfig config, SOAPSpec spec) {
        super(config, spec);
    }

    @Override
    protected void runTest() throws Throwable {
        JDOMSource source =
                new JDOMSource(
                        new SAXBuilder()
                                .build(ClientServerTest.class.getResourceAsStream("request.xml"))
                                .getRootElement());
        JDOMResult result = new JDOMResult();
        context.getBean(WebServiceTemplate.class).sendSourceAndReceiveToResult(source, result);
        assertEquals(
                8.0d, Double.parseDouble(result.getDocument().getRootElement().getText()), 1e-6);
    }
}
