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
package org.apache.axiom.om.impl.intf.factory;

import org.apache.axiom.core.NodeFactory2;
import org.apache.axiom.om.impl.intf.AxiomAttribute;
import org.apache.axiom.om.impl.intf.AxiomCDATASection;
import org.apache.axiom.om.impl.intf.AxiomCharacterDataNode;
import org.apache.axiom.om.impl.intf.AxiomComment;
import org.apache.axiom.om.impl.intf.AxiomDocType;
import org.apache.axiom.om.impl.intf.AxiomDocument;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.intf.AxiomEntityReference;
import org.apache.axiom.om.impl.intf.AxiomNamespaceDeclaration;
import org.apache.axiom.om.impl.intf.AxiomProcessingInstruction;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;
import org.apache.axiom.soap.impl.intf.AxiomSOAPMessage;
import org.apache.axiom.soap.impl.intf.soap11.AxiomSOAP11Body;
import org.apache.axiom.soap.impl.intf.soap11.AxiomSOAP11Envelope;
import org.apache.axiom.soap.impl.intf.soap11.AxiomSOAP11Fault;
import org.apache.axiom.soap.impl.intf.soap11.AxiomSOAP11FaultCode;
import org.apache.axiom.soap.impl.intf.soap11.AxiomSOAP11FaultDetail;
import org.apache.axiom.soap.impl.intf.soap11.AxiomSOAP11FaultReason;
import org.apache.axiom.soap.impl.intf.soap11.AxiomSOAP11FaultRole;
import org.apache.axiom.soap.impl.intf.soap11.AxiomSOAP11Header;
import org.apache.axiom.soap.impl.intf.soap11.AxiomSOAP11HeaderBlock;
import org.apache.axiom.soap.impl.intf.soap12.AxiomSOAP12Body;
import org.apache.axiom.soap.impl.intf.soap12.AxiomSOAP12Envelope;
import org.apache.axiom.soap.impl.intf.soap12.AxiomSOAP12Fault;
import org.apache.axiom.soap.impl.intf.soap12.AxiomSOAP12FaultCode;
import org.apache.axiom.soap.impl.intf.soap12.AxiomSOAP12FaultDetail;
import org.apache.axiom.soap.impl.intf.soap12.AxiomSOAP12FaultNode;
import org.apache.axiom.soap.impl.intf.soap12.AxiomSOAP12FaultReason;
import org.apache.axiom.soap.impl.intf.soap12.AxiomSOAP12FaultRole;
import org.apache.axiom.soap.impl.intf.soap12.AxiomSOAP12FaultSubCode;
import org.apache.axiom.soap.impl.intf.soap12.AxiomSOAP12FaultText;
import org.apache.axiom.soap.impl.intf.soap12.AxiomSOAP12FaultValue;
import org.apache.axiom.soap.impl.intf.soap12.AxiomSOAP12Header;
import org.apache.axiom.soap.impl.intf.soap12.AxiomSOAP12HeaderBlock;
import org.apache.axiom.weaver.annotation.FactoryMethod;
import org.apache.axiom.weaver.annotation.Inject;
import org.apache.axiom.weaver.annotation.Singleton;

@Singleton
public interface AxiomNodeFactory {
    @Inject
    NodeFactory2 getCoreNodeFactory();

    @FactoryMethod
    AxiomDocument createDocument();

    @FactoryMethod
    AxiomDocType createDocType();

    @FactoryMethod
    AxiomCharacterDataNode createCharacterDataNode();

    @FactoryMethod
    AxiomCDATASection createCDATASection();

    @FactoryMethod
    AxiomProcessingInstruction createProcessingInstruction();

    @FactoryMethod
    AxiomEntityReference createEntityReference();

    @FactoryMethod
    AxiomComment createComment();

    @FactoryMethod
    AxiomElement createElement();

    @FactoryMethod
    AxiomSourcedElement createSourcedElement();

    @FactoryMethod
    AxiomAttribute createAttribute();

    @FactoryMethod
    AxiomNamespaceDeclaration createNamespaceDeclaration();

    @FactoryMethod
    AxiomSOAPMessage createSOAPMessage();

    @FactoryMethod
    AxiomSOAP11Envelope createSOAP11Envelope();

    @FactoryMethod
    AxiomSOAP11Header createSOAP11Header();

    @FactoryMethod
    AxiomSOAP11HeaderBlock createSOAP11HeaderBlock();

    @FactoryMethod
    AxiomSOAP11Body createSOAP11Body();

    @FactoryMethod
    AxiomSOAP11Fault createSOAP11Fault();

    @FactoryMethod
    AxiomSOAP11FaultCode createSOAP11FaultCode();

    @FactoryMethod
    AxiomSOAP11FaultReason createSOAP11FaultReason();

    @FactoryMethod
    AxiomSOAP11FaultRole createSOAP11FaultRole();

    @FactoryMethod
    AxiomSOAP11FaultDetail createSOAP11FaultDetail();

    @FactoryMethod
    AxiomSOAP12Envelope createSOAP12Envelope();

    @FactoryMethod
    AxiomSOAP12Header createSOAP12Header();

    @FactoryMethod
    AxiomSOAP12HeaderBlock createSOAP12HeaderBlock();

    @FactoryMethod
    AxiomSOAP12Body createSOAP12Body();

    @FactoryMethod
    AxiomSOAP12Fault createSOAP12Fault();

    @FactoryMethod
    AxiomSOAP12FaultCode createSOAP12FaultCode();

    @FactoryMethod
    AxiomSOAP12FaultReason createSOAP12FaultReason();

    @FactoryMethod
    AxiomSOAP12FaultRole createSOAP12FaultRole();

    @FactoryMethod
    AxiomSOAP12FaultDetail createSOAP12FaultDetail();

    @FactoryMethod
    AxiomSOAP12FaultValue createSOAP12FaultValue();

    @FactoryMethod
    AxiomSOAP12FaultSubCode createSOAP12FaultSubCode();

    @FactoryMethod
    AxiomSOAP12FaultText createSOAP12FaultText();

    @FactoryMethod
    AxiomSOAP12FaultNode createSOAP12FaultNode();
}
