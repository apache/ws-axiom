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
package org.apache.axiom.ts.soap;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPVersion;

/**
 * Describes the characteristics of a given SOAP version. This is similar to {@link SOAPVersion},
 * but is designed specifically for the test suite.
 */
public interface SOAPSpec {
    SOAPSpec SOAP11 = new SOAPSpec() {
        public String getName() {
            return "soap11";
        }
        
        public SOAPFactory getFactory(OMMetaFactory metaFactory) {
            return metaFactory.getSOAP11Factory();
        }

        public SOAPFactory getAltFactory(OMMetaFactory metaFactory) {
            return metaFactory.getSOAP12Factory();
        }

        public String getEnvelopeNamespaceURI() {
            return SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI;
        }

        public String getFaultCodeLocalName() {
            return SOAP11Constants.SOAP_FAULT_CODE_LOCAL_NAME;
        }

        public String getFaultReasonLocalName() {
            return SOAP11Constants.SOAP_FAULT_STRING_LOCAL_NAME;
        }

        public String getFaultRoleLocalName() {
            return SOAP11Constants.SOAP_FAULT_ACTOR_LOCAL_NAME;
        }
        
        public String getFaultDetailLocalName() {
            return SOAP11Constants.SOAP_FAULT_DETAIL_LOCAL_NAME;
        }
    };

    SOAPSpec SOAP12 = new SOAPSpec() {
        public String getName() {
            return "soap12";
        }
        
        public SOAPFactory getFactory(OMMetaFactory metaFactory) {
            return metaFactory.getSOAP12Factory();
        }

        public SOAPFactory getAltFactory(OMMetaFactory metaFactory) {
            return metaFactory.getSOAP11Factory();
        }

        public String getEnvelopeNamespaceURI() {
            return SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI;
        }

        public String getFaultCodeLocalName() {
            return SOAP12Constants.SOAP_FAULT_CODE_LOCAL_NAME;
        }

        public String getFaultReasonLocalName() {
            return SOAP12Constants.SOAP_FAULT_REASON_LOCAL_NAME;
        }

        public String getFaultRoleLocalName() {
            return SOAP12Constants.SOAP_FAULT_ROLE_LOCAL_NAME;
        }
        
        public String getFaultDetailLocalName() {
            return SOAP12Constants.SOAP_FAULT_DETAIL_LOCAL_NAME;
        }
    };
    
    String getName();
    SOAPFactory getFactory(OMMetaFactory metaFactory);
    SOAPFactory getAltFactory(OMMetaFactory metaFactory);
    String getEnvelopeNamespaceURI();
    String getFaultCodeLocalName();
    String getFaultReasonLocalName();
    String getFaultRoleLocalName();
    String getFaultDetailLocalName();
}
