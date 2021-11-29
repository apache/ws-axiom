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
package org.apache.axiom.om.impl.dom.intf.factory;

import org.apache.axiom.dom.DOMNodeFactory;
import org.apache.axiom.om.dom.DOMMetaFactory;
import org.apache.axiom.om.impl.dom.intf.DOOMCDATASection;
import org.apache.axiom.om.impl.dom.intf.DOOMComment;
import org.apache.axiom.om.impl.dom.intf.DOOMDocument;
import org.apache.axiom.om.impl.dom.intf.DOOMDocumentType;
import org.apache.axiom.om.impl.dom.intf.DOOMEntityReference;
import org.apache.axiom.om.impl.dom.intf.DOOMNSAwareAttribute;
import org.apache.axiom.om.impl.dom.intf.DOOMNSAwareElement;
import org.apache.axiom.om.impl.dom.intf.DOOMNamespaceDeclaration;
import org.apache.axiom.om.impl.dom.intf.DOOMProcessingInstruction;
import org.apache.axiom.om.impl.dom.intf.DOOMSourcedElement;
import org.apache.axiom.om.impl.dom.intf.DOOMText;
import org.apache.axiom.om.impl.intf.factory.AxiomNodeFactory;
import org.apache.axiom.weaver.annotation.FactoryMethod;
import org.apache.axiom.weaver.annotation.Inject;
import org.apache.axiom.weaver.annotation.Singleton;
import org.w3c.dom.DOMImplementation;

@Singleton
public interface DOOMNodeFactory extends AxiomNodeFactory, DOMNodeFactory, DOMMetaFactory {
    @Override
    @Inject
    DOMImplementation getDOMImplementation();

    @FactoryMethod
    @Override
    DOOMDocument createDocument();

    @FactoryMethod
    @Override
    DOOMDocumentType createDocumentTypeDeclaration();

    @FactoryMethod
    @Override
    DOOMText createCharacterDataNode();

    @FactoryMethod
    @Override
    DOOMCDATASection createCDATASection();

    @FactoryMethod
    @Override
    DOOMProcessingInstruction createProcessingInstruction();

    @FactoryMethod
    @Override
    DOOMEntityReference createEntityReference();

    @FactoryMethod
    @Override
    DOOMComment createComment();

    @FactoryMethod
    @Override
    DOOMNSAwareElement createNSAwareElement();

    @FactoryMethod
    @Override
    DOOMSourcedElement createSourcedElement();

    @FactoryMethod
    @Override
    DOOMNSAwareAttribute createNSAwareAttribute();

    @FactoryMethod
    @Override
    DOOMNamespaceDeclaration createNamespaceDeclaration();
}
