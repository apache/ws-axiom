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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class CalculatorEndpoint {
    private static final Log log = LogFactory.getLog(CalculatorEndpoint.class);

    private static final String NAMESPACE_URI = "urn:calculator";
    private static final Namespace NAMESPACE = Namespace.getNamespace("c", NAMESPACE_URI);

    private XPathExpression<Element> operandExpression =
            XPathFactory.instance().compile("c:Operand", Filters.element(), null, NAMESPACE);

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AddRequest")
    @ResponsePayload
    public Element add(@RequestPayload Element addRequest) throws Exception {
        log.debug("Endpoint invoked");
        double sum = 0d;
        for (Element operand : operandExpression.evaluate(addRequest)) {
            sum += Double.parseDouble(operand.getTextNormalize());
        }
        Element response = new Element("AddResponse", NAMESPACE);
        response.setText(String.valueOf(sum));
        return response;
    }
}
