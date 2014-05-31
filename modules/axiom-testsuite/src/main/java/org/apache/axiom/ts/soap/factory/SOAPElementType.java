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
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.testutils.suite.Dimension;
import org.apache.axiom.testutils.suite.MatrixTestCase;
import org.apache.axiom.ts.soap.SOAPSpec;

public interface SOAPElementType extends Dimension {
    SOAPElementType ENVELOPE = new SOAPElementType() {
        public void addTestParameters(MatrixTestCase testCase) {
            testCase.addTestParameter("type", "SOAPEnvelope");
        }

        public Class getType() {
            return SOAPEnvelope.class;
        }

        public QName getQName(SOAPSpec spec) {
            return spec.getEnvelopeQName();
        }

        public OMElement create(SOAPFactory factory) {
            return factory.createSOAPEnvelope();
        }
    };
    
    SOAPElementType HEADER = new SOAPElementType() {
        public void addTestParameters(MatrixTestCase testCase) {
            testCase.addTestParameter("type", "SOAPHeader");
        }

        public Class getType() {
            return SOAPHeader.class;
        }

        public QName getQName(SOAPSpec spec) {
            return spec.getHeaderQName();
        }

        public OMElement create(SOAPFactory factory) {
            return factory.createSOAPHeader();
        }
    };
    
    SOAPElementType BODY = new SOAPElementType() {
        public void addTestParameters(MatrixTestCase testCase) {
            testCase.addTestParameter("type", "SOAPBody");
        }

        public Class getType() {
            return SOAPBody.class;
        }

        public QName getQName(SOAPSpec spec) {
            return spec.getBodyQName();
        }

        public OMElement create(SOAPFactory factory) {
            return factory.createSOAPBody();
        }
    };
    
    SOAPElementType VALUE = new SOAPElementType() {
        public void addTestParameters(MatrixTestCase testCase) {
            testCase.addTestParameter("type", "SOAPFaultValue");
        }

        public Class getType() {
            return SOAPFaultValue.class;
        }

        public QName getQName(SOAPSpec spec) {
            return spec.getFaultValueQName();
        }

        public OMElement create(SOAPFactory factory) {
            return factory.createSOAPFaultValue();
        }
    };
    
    SOAPElementType SUB_CODE = new SOAPElementType() {
        public void addTestParameters(MatrixTestCase testCase) {
            testCase.addTestParameter("type", "SOAPFaultSubCode");
        }

        public Class getType() {
            return SOAPFaultSubCode.class;
        }

        public QName getQName(SOAPSpec spec) {
            return spec.getFaultSubCodeQName();
        }

        public OMElement create(SOAPFactory factory) {
            return factory.createSOAPFaultSubCode();
        }
    };
    
    SOAPElementType TEXT = new SOAPElementType() {
        public void addTestParameters(MatrixTestCase testCase) {
            testCase.addTestParameter("type", "SOAPFaultText");
        }

        public Class getType() {
            return SOAPFaultText.class;
        }

        public QName getQName(SOAPSpec spec) {
            return spec.getFaultTextQName();
        }

        public OMElement create(SOAPFactory factory) {
            return factory.createSOAPFaultText();
        }
    };
    
    Class getType();
    
    /**
     * Get the qualified name for this element type in the given SOAP version.
     * 
     * @param spec
     *            the SOAP version
     * @return the element name, or <code>null</code> if the element type doesn't exist in the given
     *         SOAP version
     */
    QName getQName(SOAPSpec spec);
    
    OMElement create(SOAPFactory factory);
}
