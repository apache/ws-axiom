/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.axiom.soap;

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;


public interface SOAPFactory extends OMFactory {

    public String getSoapVersionURI();

    /** Eran Chinthaka (chinthaka@apache.org) */

    public SOAPMessage createSOAPMessage();

    public SOAPMessage createSOAPMessage(OMXMLParserWrapper builder);

    public SOAPMessage createSOAPMessage(SOAPEnvelope envelope, OMXMLParserWrapper parserWrapper);

    /** @return Returns SOAPEnvelope. */
    public SOAPEnvelope createSOAPEnvelope() throws SOAPProcessingException;

    public SOAPEnvelope createSOAPEnvelope(OMXMLParserWrapper builder);

    /**
     * @param envelope
     * @return Returns SOAPHeader.
     */
    public SOAPHeader createSOAPHeader(SOAPEnvelope envelope) throws SOAPProcessingException;

    public SOAPHeader createSOAPHeader() throws SOAPProcessingException;

    /**
     * @param envelope
     * @param builder
     * @return Returns SOAPHeader.
     */
    public SOAPHeader createSOAPHeader(SOAPEnvelope envelope,
                                       OMXMLParserWrapper builder);

    /**
     * @param localName
     * @param ns
     * @return Returns SOAPHeaderBlock.
     */
    public SOAPHeaderBlock createSOAPHeaderBlock(String localName,
                                                 OMNamespace ns,
                                                 SOAPHeader parent) throws SOAPProcessingException;

    public SOAPHeaderBlock createSOAPHeaderBlock(String localName,
                                                 OMNamespace ns) throws SOAPProcessingException;

    /**
     * @param localName
     * @param ns
     * @param parent
     * @param builder
     * @return Returns SOAPHeaderBlock.
     */
    public SOAPHeaderBlock createSOAPHeaderBlock(String localName,
                                                 OMNamespace ns,
                                                 SOAPHeader parent,
                                                 OMXMLParserWrapper builder)
            throws SOAPProcessingException;

    /**
     * @param parent
     * @param e
     * @return Returns SOAPFault.
     */
    public SOAPFault createSOAPFault(SOAPBody parent, Exception e) throws SOAPProcessingException;

    public SOAPFault createSOAPFault(SOAPBody parent) throws SOAPProcessingException;

    public SOAPFault createSOAPFault() throws SOAPProcessingException;

    /**
     * @param parent
     * @param builder
     * @return Returns SOAPFault.
     */
    public SOAPFault createSOAPFault(SOAPBody parent,
                                     OMXMLParserWrapper builder);

    /**
     * @param envelope
     * @return Returns SOAPBody.
     */
    public SOAPBody createSOAPBody(SOAPEnvelope envelope) throws SOAPProcessingException;

    public SOAPBody createSOAPBody() throws SOAPProcessingException;

    /**
     * @param envelope
     * @param builder
     * @return Returns SOAPBody.
     */
    public SOAPBody createSOAPBody(SOAPEnvelope envelope,
                                   OMXMLParserWrapper builder);

    /* ========================
       =  SOAPFaultCode       =
       ======================== */

    /**
     * Code eii under SOAPFault (parent)
     *
     * @param parent
     * @return Returns SOAPFaultCode.
     */
    public SOAPFaultCode createSOAPFaultCode(SOAPFault parent) throws SOAPProcessingException;

    public SOAPFaultCode createSOAPFaultCode() throws SOAPProcessingException;

    /**
     * Code eii under SOAPFault (parent)
     *
     * @param parent
     * @param builder
     * @return Returns SOAPFaultCode.
     */
    public SOAPFaultCode createSOAPFaultCode(SOAPFault parent,
                                             OMXMLParserWrapper builder);

    /*========================
   =  SOAPFaultCodeValue  =
   ======================== */

    /**
     * Value eii under Code (parent)
     *
     * @param parent
     * @return Returns SOAPFaultValue.
     */
    public SOAPFaultValue createSOAPFaultValue(SOAPFaultCode parent) throws SOAPProcessingException;

    public SOAPFaultValue createSOAPFaultValue() throws SOAPProcessingException;

    /**
     * Value eii under Code (parent)
     *
     * @param parent
     * @param builder
     * @return Returns SOAPFaultValue.
     */
    public SOAPFaultValue createSOAPFaultValue(SOAPFaultCode parent,
                                               OMXMLParserWrapper builder);

    /*========================
      =  SOAPFaultSubCode    =
      ======================== */

    /**
     * SubCode eii under Value (parent)
     *
     * @param parent
     * @return Returns SOAPFaultValue.
     */

    //added
    public SOAPFaultValue createSOAPFaultValue(SOAPFaultSubCode parent)
            throws SOAPProcessingException;

    //added
    public SOAPFaultValue createSOAPFaultValue(SOAPFaultSubCode parent,
                                               OMXMLParserWrapper builder);

    //changed
    public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultCode parent)
            throws SOAPProcessingException;

    public SOAPFaultSubCode createSOAPFaultSubCode() throws SOAPProcessingException;

    /**
     * SubCode eii under Value (parent)
     *
     * @param parent
     * @param builder
     * @return Returns SOAPFaultSubCode.
     */
    //changed
    public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultCode parent,
                                                   OMXMLParserWrapper builder);

    /**
     * SubCode eii under SubCode (parent)
     *
     * @param parent
     * @return Returns SOAPFaultSubCode.
     */
    public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultSubCode parent)
            throws SOAPProcessingException;

    /**
     * SubCode eii under SubCode (parent)
     *
     * @param parent
     * @param builder
     * @return Returns SOAPFaultSubCode.
     */
    public SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultSubCode parent,
                                                   OMXMLParserWrapper builder);

    /*========================
   =  SOAPFaultReason     =
   ======================== */

    /**
     * Reason eii under SOAPFault (parent)
     *
     * @param parent
     * @return Returns SOAPFaultReason.
     */
    public SOAPFaultReason createSOAPFaultReason(SOAPFault parent) throws SOAPProcessingException;

    public SOAPFaultReason createSOAPFaultReason() throws SOAPProcessingException;

    /**
     * Reason eii under SOAPFault (parent)
     *
     * @param parent
     * @param builder
     * @return Returns SOAPFaultReason.
     */
    public SOAPFaultReason createSOAPFaultReason(SOAPFault parent,
                                                 OMXMLParserWrapper builder);

    /*========================
      =  SOAPFaultReasonText     =
      ======================== */

    /**
     * SubCode eii under SubCode (parent)
     *
     * @param parent
     * @return Returns SOAPFaultText.
     */
    public SOAPFaultText createSOAPFaultText(SOAPFaultReason parent) throws SOAPProcessingException;

    public SOAPFaultText createSOAPFaultText() throws SOAPProcessingException;

    /**
     * SubCode eii under SubCode (parent)
     *
     * @param parent
     * @param builder
     * @return Returns SOAPFaultText.
     */
    public SOAPFaultText createSOAPFaultText(SOAPFaultReason parent,
                                             OMXMLParserWrapper builder);

    /*========================
   =  SOAPFaultNode       =
   ======================== */

    /**
     * Node eii under SOAPFault (parent)
     *
     * @param parent
     * @return Returns SOAPFaultNode.
     */
    public SOAPFaultNode createSOAPFaultNode(SOAPFault parent) throws SOAPProcessingException;

    public SOAPFaultNode createSOAPFaultNode() throws SOAPProcessingException;

    /**
     * Node eii under SOAPFault (parent)
     *
     * @param parent
     * @param builder
     * @return Returns SOAPFaultNode.
     */
    public SOAPFaultNode createSOAPFaultNode(SOAPFault parent,
                                             OMXMLParserWrapper builder);

    /*========================
      =  SOAPFaultRole       =
      ======================== */

    /**
     * Role eii under SOAPFault (parent)
     *
     * @param parent
     * @return Returns SOAPFaultRole.
     */
    public SOAPFaultRole createSOAPFaultRole(SOAPFault parent) throws SOAPProcessingException;

    public SOAPFaultRole createSOAPFaultRole() throws SOAPProcessingException;

    /**
     * Role eii under SOAPFault (parent)
     *
     * @param parent
     * @param builder
     * @return Returns SOAPFaultRole.
     */
    public SOAPFaultRole createSOAPFaultRole(SOAPFault parent,
                                             OMXMLParserWrapper builder);

    /*========================
      =  SOAPFaultDetail     =
      ======================== */

    /**
     * Detail eii under SOAPFault (parent)
     *
     * @param parent
     * @return Returns SOAPFaultDetail.
     */
    public SOAPFaultDetail createSOAPFaultDetail(SOAPFault parent) throws SOAPProcessingException;

    public SOAPFaultDetail createSOAPFaultDetail() throws SOAPProcessingException;

    /**
     * Role eii under SOAPFault (parent)
     *
     * @param parent
     * @param builder
     * @return Returns SOAPFaultDetail.
     */
    public SOAPFaultDetail createSOAPFaultDetail(SOAPFault parent,
                                                 OMXMLParserWrapper builder);


    /**
     * Method getDefaultEnvelope. This returns a SOAP envelope consisting with an empty Header and a
     * Body. This is just a util method which can be used everywhere.
     *
     * @return Returns SOAPEnvelope.
     */
    public SOAPEnvelope getDefaultEnvelope() throws SOAPProcessingException;

    public SOAPEnvelope getDefaultFaultEnvelope() throws SOAPProcessingException;

    public OMNamespace getNamespace();

}
