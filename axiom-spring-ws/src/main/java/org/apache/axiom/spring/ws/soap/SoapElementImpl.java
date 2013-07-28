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
package org.apache.axiom.spring.ws.soap;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.spring.ws.SourceExtractionStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ws.soap.SoapElement;

abstract class SoapElementImpl<T extends OMElement> implements SoapElement {
    private static final Log log = LogFactory.getLog(SoapElementImpl.class);
    
    private final SoapMessageImpl message;
    final T axiomNode;

    SoapElementImpl(SoapMessageImpl message, T axiomNode) {
        if (axiomNode == null) {
            throw new IllegalArgumentException();
        }
        this.message = message;
        this.axiomNode = axiomNode;
    }
    
    final SoapMessageImpl getMessage() {
        return message;
    }

    final SourceExtractionStrategy getSourceExtractionStrategy() {
        return message.getSourceExtractionStrategy();
    }
    
    public final QName getName() {
        return axiomNode.getQName();
    }

    public final Source getSource() {
        SourceExtractionStrategy strategy = getSourceExtractionStrategy();
        if (log.isDebugEnabled()) {
            log.debug("Returning Source for " + getClass().getSimpleName() + " using strategy " + strategy);
        }
        return strategy.getSource(axiomNode);
    }

    public void addAttribute(QName name, String value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public void removeAttribute(QName name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public String getAttributeValue(QName name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public Iterator<QName> getAllAttributes() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public final void addNamespaceDeclaration(String prefix, String namespaceUri) {
        if (prefix == null || prefix.length() == 0) {
            axiomNode.declareDefaultNamespace(namespaceUri);
        } else {
            axiomNode.declareNamespace(namespaceUri, prefix);
        }
    }
}
