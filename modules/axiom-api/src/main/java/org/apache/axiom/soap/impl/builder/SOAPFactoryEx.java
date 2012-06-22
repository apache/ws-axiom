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
import org.apache.axiom.om.impl.builder.OMFactoryEx;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.soap.SOAPProcessingException;

/**
 * Interface that is used internally by Axiom and that should not be considered being part of the
 * public API.
 */
public interface SOAPFactoryEx extends SOAPFactory, OMFactoryEx {

    SOAPMessage createSOAPMessage(OMXMLParserWrapper builder);

    SOAPMessage createSOAPMessage(SOAPEnvelope envelope, OMXMLParserWrapper parserWrapper);

    SOAPEnvelope createSOAPEnvelope(OMXMLParserWrapper builder);

    /**
     * @param envelope
     * @param builder
     * @return Returns SOAPHeader.
     */
    SOAPHeader createSOAPHeader(SOAPEnvelope envelope,
                                       OMXMLParserWrapper builder);

    /**
     * @param localName
     * @param parent
     * @param builder
     * @return Returns SOAPHeaderBlock.
     */
    SOAPHeaderBlock createSOAPHeaderBlock(String localName,
                                                 SOAPHeader parent,
                                                 OMXMLParserWrapper builder)
            throws SOAPProcessingException;

    /**
     * @param parent
     * @param builder
     * @return Returns SOAPFault.
     */
    SOAPFault createSOAPFault(SOAPBody parent,
                                     OMXMLParserWrapper builder);

    /**
     * @param envelope
     * @param builder
     * @return Returns SOAPBody.
     */
    SOAPBody createSOAPBody(SOAPEnvelope envelope,
                                   OMXMLParserWrapper builder);

    /**
     * Code eii under SOAPFault (parent)
     *
     * @param parent
     * @param builder
     * @return Returns SOAPFaultCode.
     */
    SOAPFaultCode createSOAPFaultCode(SOAPFault parent,
                                             OMXMLParserWrapper builder);

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
     * Reason eii under SOAPFault (parent)
     *
     * @param parent
     * @param builder
     * @return Returns SOAPFaultReason.
     */
    SOAPFaultReason createSOAPFaultReason(SOAPFault parent,
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

    /**
     * Role eii under SOAPFault (parent)
     *
     * @param parent
     * @param builder
     * @return Returns SOAPFaultRole.
     */
    SOAPFaultRole createSOAPFaultRole(SOAPFault parent,
                                             OMXMLParserWrapper builder);

    /**
     * Role eii under SOAPFault (parent)
     *
     * @param parent
     * @param builder
     * @return Returns SOAPFaultDetail.
     */
    SOAPFaultDetail createSOAPFaultDetail(SOAPFault parent,
                                                 OMXMLParserWrapper builder);
}
