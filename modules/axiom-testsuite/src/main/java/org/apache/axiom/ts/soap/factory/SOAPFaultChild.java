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
package org.apache.axiom.ts.soap.factory;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.ts.AxiomTestCase;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.strategy.Strategy;

public interface SOAPFaultChild extends Strategy {
    SOAPFaultChild CODE = new SOAPFaultChild() {
        public Class getType() {
            return SOAPFaultCode.class;
        }

        public void addTestProperties(AxiomTestCase testCase) {
            testCase.addTestProperty("type", "SOAPFaultCode");
        }

        public QName getQName(SOAPSpec spec) {
            return spec.getFaultCodeQName();
        }
        
        public int getOrder() {
            return 1;
        }

        public OMElement create(SOAPFactory factory) {
            return factory.createSOAPFaultCode();
        }
        
        public OMElement create(SOAPFactory factory, SOAPFault parent) {
            return factory.createSOAPFaultCode(parent);
        }

        public void set(SOAPFault fault, OMElement element) {
            fault.setCode((SOAPFaultCode)element);
        }
    };

    SOAPFaultChild REASON = new SOAPFaultChild() {
        public Class getType() {
            return SOAPFaultReason.class;
        }

        public void addTestProperties(AxiomTestCase testCase) {
            testCase.addTestProperty("type", "SOAPFaultReason");
        }

        public QName getQName(SOAPSpec spec) {
            return spec.getFaultReasonQName();
        }
        
        public int getOrder() {
            return 2;
        }

        public OMElement create(SOAPFactory factory) {
            return factory.createSOAPFaultReason();
        }
        
        public OMElement create(SOAPFactory factory, SOAPFault parent) {
            return factory.createSOAPFaultReason(parent);
        }

        public void set(SOAPFault fault, OMElement element) {
            fault.setReason((SOAPFaultReason)element);
        }
    };

    SOAPFaultChild ROLE = new SOAPFaultChild() {
        public Class getType() {
            return SOAPFaultRole.class;
        }

        public void addTestProperties(AxiomTestCase testCase) {
            testCase.addTestProperty("type", "SOAPFaultRole");
        }

        public QName getQName(SOAPSpec spec) {
            return spec.getFaultRoleQName();
        }
        
        public int getOrder() {
            return 3;
        }

        public OMElement create(SOAPFactory factory) {
            return factory.createSOAPFaultRole();
        }
        
        public OMElement create(SOAPFactory factory, SOAPFault parent) {
            return factory.createSOAPFaultRole(parent);
        }

        public void set(SOAPFault fault, OMElement element) {
            fault.setRole((SOAPFaultRole)element);
        }
    };

    SOAPFaultChild DETAIL = new SOAPFaultChild() {
        public Class getType() {
            return SOAPFaultDetail.class;
        }

        public void addTestProperties(AxiomTestCase testCase) {
            testCase.addTestProperty("type", "SOAPFaultDetail");
        }

        public QName getQName(SOAPSpec spec) {
            return spec.getFaultDetailQName();
        }
        
        public int getOrder() {
            return 4;
        }

        public OMElement create(SOAPFactory factory) {
            return factory.createSOAPFaultDetail();
        }
        
        public OMElement create(SOAPFactory factory, SOAPFault parent) {
            return factory.createSOAPFaultDetail(parent);
        }

        public void set(SOAPFault fault, OMElement element) {
            fault.setDetail((SOAPFaultDetail)element);
        }
    };

    Class getType();
    QName getQName(SOAPSpec spec);
    int getOrder();
    OMElement create(SOAPFactory factory);
    OMElement create(SOAPFactory factory, SOAPFault parent);
    void set(SOAPFault fault, OMElement element);
}
