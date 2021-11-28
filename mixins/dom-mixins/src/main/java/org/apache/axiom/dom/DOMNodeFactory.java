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
package org.apache.axiom.dom;

import org.apache.axiom.weaver.annotation.FactoryMethod;
import org.apache.axiom.weaver.annotation.Singleton;
import org.w3c.dom.DOMImplementation;

@Singleton
public interface DOMNodeFactory extends DOMImplementation {
    @FactoryMethod
    DOMDocument createDocument();

    @FactoryMethod
    DOMDocumentType createDocumentType();

    @FactoryMethod
    DOMText createText();

    @FactoryMethod
    DOMCDATASection createCDATASection();

    @FactoryMethod
    DOMNSUnawareElement createNSUnawareElement();

    @FactoryMethod
    DOMNSUnawareAttribute createNSUnawareAttribute();

    @FactoryMethod
    DOMNSAwareElement createNSAwareElement();

    @FactoryMethod
    DOMNSAwareAttribute createNSAwareAttribute();

    @FactoryMethod
    DOMNamespaceDeclaration createNamespaceDeclaration();

    @FactoryMethod
    DOMProcessingInstruction createProcessingInstruction();

    @FactoryMethod
    DOMEntityReference createEntityReference();

    @FactoryMethod
    DOMComment createComment();

    @FactoryMethod
    DOMDocumentFragment createDocumentFragment();
}
