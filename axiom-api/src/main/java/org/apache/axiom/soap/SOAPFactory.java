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

package org.apache.axiom.soap;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

public interface SOAPFactory extends OMFactory {

    String getSoapVersionURI();

    SOAPVersion getSOAPVersion();

    SOAPMessage createSOAPMessage();

    /**
     * Create a SOAP envelope. The returned element will have the namespace URI specified by the
     * SOAP version that this factory represents. It will have the prefix given by
     * {@link SOAPConstants#SOAP_DEFAULT_NAMESPACE_PREFIX}. It will also have a corresponding
     * namespace declaration.
     * 
     * @return the SOAP envelope
     */
    // TODO: when does this thing throw a SOAPProcessingException??
    SOAPEnvelope createSOAPEnvelope() throws SOAPProcessingException;
    
    /**
     * Create a SOAP envelope with the given namespace. This method can be used to create a SOAP
     * envelope with a custom namespace prefix.
     * 
     * @param ns
     *            the namespace information for the SOAP envelope
     * @return the SOAP envelope
     */
    // TODO: specify what happens if there is a mismatch between the provided namespace URI and the SOAP version of the factory
    // TODO: specify what happens if the prefix is null
    SOAPEnvelope createSOAPEnvelope(OMNamespace ns); 

    /**
     * Create a {@link SOAPHeader} as a child of the given {@link SOAPEnvelope}.
     * <p>
     * Note that for most use cases, it is preferable to use
     * {@link SOAPEnvelope#getOrCreateHeader()} instead of this method.
     * 
     * @param envelope
     *            the parent of the {@link SOAPHeader}
     * @return the newly created {@link SOAPHeader}
     */
    SOAPHeader createSOAPHeader(SOAPEnvelope envelope) throws SOAPProcessingException;

    SOAPHeader createSOAPHeader() throws SOAPProcessingException;

    /**
     * @param localName
     * @param ns
     * @return Returns SOAPHeaderBlock.
     */
    SOAPHeaderBlock createSOAPHeaderBlock(String localName,
                                                 OMNamespace ns,
                                                 SOAPHeader parent) throws SOAPProcessingException;

    SOAPHeaderBlock createSOAPHeaderBlock(String localName,
                                                 OMNamespace ns) throws SOAPProcessingException;
    
    /**
     * Create a {@link SOAPHeaderBlock} from an {@link OMDataSource}. The semantics of the method
     * parameters are the same as for {@link OMFactory#createOMElement(OMDataSource)}.
     * 
     * @param source
     *            the data source; must not be <code>null</code>
     * @return the newly created header block
     */
    SOAPHeaderBlock createSOAPHeaderBlock(OMDataSource source);
    
    /**
     * Create a {@link SOAPHeaderBlock} from an {@link OMDataSource} with a known local name and
     * namespace URI. The semantics of the method parameters are the same as for
     * {@link OMFactory#createOMElement(OMDataSource, String, OMNamespace)}.
     * 
     * @param localName
     *            the local part of the name of the element produced by the data source; must not be
     *            <code>null</code>
     * @param ns
     *            the namespace of the element produced by the data source, or <code>null</code> if
     *            the element has no namespace
     * @param source
     *            the data source; must not be <code>null</code>
     * @return the newly created header block
     * @throws SOAPProcessingException
     */
    SOAPHeaderBlock createSOAPHeaderBlock(String localName,
                                          OMNamespace ns,
                                          OMDataSource source) throws SOAPProcessingException;

    /**
     * Create a new {@link SOAPHeaderBlock} with the same content as the given element.
     * 
     * @param element
     *            the element to import as a header block
     * @return the header block
     */
    SOAPHeaderBlock createSOAPHeaderBlock(OMElement element);

    /**
     * @param parent
     * @param e
     * @return Returns SOAPFault.
     */
    SOAPFault createSOAPFault(SOAPBody parent, Exception e) throws SOAPProcessingException;

    SOAPFault createSOAPFault(SOAPBody parent) throws SOAPProcessingException;

    SOAPFault createSOAPFault() throws SOAPProcessingException;

    /**
     * @param envelope
     * @return Returns SOAPBody.
     */
    SOAPBody createSOAPBody(SOAPEnvelope envelope) throws SOAPProcessingException;

    SOAPBody createSOAPBody() throws SOAPProcessingException;

    /**
     * Code eii under SOAPFault (parent)
     *
     * @param parent
     * @return Returns SOAPFaultCode.
     */
    SOAPFaultCode createSOAPFaultCode(SOAPFault parent) throws SOAPProcessingException;

    SOAPFaultCode createSOAPFaultCode() throws SOAPProcessingException;

    /**
     * Value eii under Code (parent)
     *
     * @param parent
     * @return Returns SOAPFaultValue.
     */
    SOAPFaultValue createSOAPFaultValue(SOAPFaultCode parent) throws SOAPProcessingException;

    SOAPFaultValue createSOAPFaultValue() throws SOAPProcessingException;

    /**
     * SubCode eii under Value (parent)
     *
     * @param parent
     * @return Returns SOAPFaultValue.
     */
    SOAPFaultValue createSOAPFaultValue(SOAPFaultSubCode parent)
            throws SOAPProcessingException;

    SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultCode parent)
            throws SOAPProcessingException;

    SOAPFaultSubCode createSOAPFaultSubCode() throws SOAPProcessingException;

    /**
     * SubCode eii under SubCode (parent)
     *
     * @param parent
     * @return Returns SOAPFaultSubCode.
     */
    SOAPFaultSubCode createSOAPFaultSubCode(SOAPFaultSubCode parent)
            throws SOAPProcessingException;

    /**
     * Reason eii under SOAPFault (parent)
     *
     * @param parent
     * @return Returns SOAPFaultReason.
     */
    SOAPFaultReason createSOAPFaultReason(SOAPFault parent) throws SOAPProcessingException;

    SOAPFaultReason createSOAPFaultReason() throws SOAPProcessingException;

    /**
     * SubCode eii under SubCode (parent)
     *
     * @param parent
     * @return Returns SOAPFaultText.
     */
    SOAPFaultText createSOAPFaultText(SOAPFaultReason parent) throws SOAPProcessingException;

    SOAPFaultText createSOAPFaultText() throws SOAPProcessingException;

    /**
     * Node eii under SOAPFault (parent)
     *
     * @param parent
     * @return Returns SOAPFaultNode.
     */
    SOAPFaultNode createSOAPFaultNode(SOAPFault parent) throws SOAPProcessingException;

    SOAPFaultNode createSOAPFaultNode() throws SOAPProcessingException;

    /**
     * Role eii under SOAPFault (parent)
     *
     * @param parent
     * @return Returns SOAPFaultRole.
     */
    SOAPFaultRole createSOAPFaultRole(SOAPFault parent) throws SOAPProcessingException;

    SOAPFaultRole createSOAPFaultRole() throws SOAPProcessingException;

    /**
     * Detail eii under SOAPFault (parent)
     *
     * @param parent
     * @return Returns SOAPFaultDetail.
     */
    SOAPFaultDetail createSOAPFaultDetail(SOAPFault parent) throws SOAPProcessingException;

    SOAPFaultDetail createSOAPFaultDetail() throws SOAPProcessingException;


    /**
     * Create a default SOAP envelope with an empty header and an empty body. Note that the method
     * will not create an associated {@link SOAPMessage} or {@link OMDocument} instance and the
     * parent of the returned {@link SOAPEnvelope} is <code>null</code>.
     * <p>
     * <b>Note:</b> This method is typically used in conjunction with
     * {@link SOAPEnvelope#getHeader()}. In order to avoid generating unnecessary empty SOAP
     * headers, you should consider using {@link #createDefaultSOAPMessage()} together with
     * {@link SOAPEnvelope#getOrCreateHeader()} instead. This method may be deprecated and/or
     * removed in future Axiom versions.
     * 
     * @return the default SOAP envelope
     */
    SOAPEnvelope getDefaultEnvelope() throws SOAPProcessingException;

    /**
     * Create a default SOAP message with an envelope with an empty body.
     * <p>
     * Since no SOAP header is added to the envelope, this method should be used in conjunction with
     * {@link SOAPEnvelope#getOrCreateHeader()} (if SOAP header blocks need to be added).
     * 
     * @return the default SOAP envelope
     */
    SOAPMessage createDefaultSOAPMessage();
    
    SOAPEnvelope getDefaultFaultEnvelope() throws SOAPProcessingException;

    /**
     * Get the envelope namespace for the SOAP version used by this factory. The returned
     * {@link OMNamespace} instance has the following properties:
     * <ul>
     * <li>The prefix is set to {@link SOAPConstants#SOAP_DEFAULT_NAMESPACE_PREFIX} (which is the
     * same as the prefix used by methods such as {@link #createSOAPEnvelope()}).
     * <li>The namespace URI is the same as returned by {@link #getSoapVersionURI()}.
     * </ul>
     * 
     * @return the envelope namespace for the SOAP version used by this factory
     */
    OMNamespace getNamespace();
}
