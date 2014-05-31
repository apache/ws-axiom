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
package org.apache.axiom.soap.impl.builder;

import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.SOAPFaultValue;

/**
 * Interface that is used internally by Axiom and that should not be considered being part of the
 * public API.
 */
public interface SOAP12FactoryEx extends SOAPFactoryEx {
    /**
     * Value eii under Code (parent)
     *
     * @param parent
     * @param builder
     * @return Returns SOAPFaultValue.
     */
    SOAPFaultValue createSOAPFaultValue(SOAPFaultCode parent,
                                               OMXMLParserWrapper builder);

    //added
    SOAPFaultValue createSOAPFaultValue(SOAPFaultSubCode parent,
                                               OMXMLParserWrapper builder);

    /**
     * SubCode eii under Value (parent)
     *
     * @param parent
     * @param builder
     * @return Returns SOAPFaultSubCode.
     */
    //changed
    SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultCode parent,
                                                   OMXMLParserWrapper builder);

    /**
     * SubCode eii under SubCode (parent)
     *
     * @param parent
     * @param builder
     * @return Returns SOAPFaultSubCode.
     */
    SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultSubCode parent,
                                                   OMXMLParserWrapper builder);

    /**
     * SubCode eii under SubCode (parent)
     *
     * @param parent
     * @param builder
     * @return Returns SOAPFaultText.
     */
    SOAPFaultText createSOAPFaultText(SOAPFaultReason parent,
                                             OMXMLParserWrapper builder);

    /**
     * Node eii under SOAPFault (parent)
     *
     * @param parent
     * @param builder
     * @return Returns SOAPFaultNode.
     */
    SOAPFaultNode createSOAPFaultNode(SOAPFault parent,
                                             OMXMLParserWrapper builder);
}
