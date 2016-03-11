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
package org.apache.axiom.om.impl.mixin;

import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.common.AxiomExceptionTranslator;
import org.apache.axiom.om.impl.common.AxiomSemantics;
import org.apache.axiom.om.impl.intf.AxiomDocument;
import org.apache.axiom.om.impl.intf.AxiomElement;

public aspect AxiomDocumentSupport {
    public final OMElement AxiomDocument.getOMDocumentElement() {
        try {
            return (OMElement)coreGetDocumentElement();
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }

    public final void AxiomDocument.setOMDocumentElement(OMElement documentElement) {
        try {
            if (documentElement == null) {
                throw new IllegalArgumentException("documentElement must not be null");
            }
            AxiomElement existingDocumentElement = (AxiomElement)coreGetDocumentElement();
            if (existingDocumentElement == null) {
                addChild(documentElement);
            } else {
                existingDocumentElement.coreReplaceWith((AxiomElement)documentElement, AxiomSemantics.INSTANCE);
            }
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }

    public final String AxiomDocument.getCharsetEncoding() {
        String inputEncoding = coreGetInputEncoding();
        return inputEncoding == null ? "UTF-8" : inputEncoding;
    }

    public final void AxiomDocument.setCharsetEncoding(String charsetEncoding) {
        coreSetInputEncoding(charsetEncoding);
    }

    public final String AxiomDocument.getXMLVersion() {
        return coreGetXmlVersion();
    }

    public final void AxiomDocument.setXMLVersion(String xmlVersion) {
        coreSetXmlVersion(xmlVersion);
    }

    public final String AxiomDocument.getXMLEncoding() {
        return coreGetXmlEncoding();
    }

    public final void AxiomDocument.setXMLEncoding(String xmlEncoding) {
        coreSetXmlEncoding(xmlEncoding);
    }

    public final String AxiomDocument.isStandalone() {
        return coreIsStandalone() ? "yes" : "no";
    }

    public final void AxiomDocument.setStandalone(String standalone) {
        coreSetStandalone("yes".equalsIgnoreCase(standalone));
    }

    public final void AxiomDocument.checkChild(OMNode child) {
        if (child instanceof OMElement) {
            if (getOMDocumentElement() != null) {
                throw new OMException("Document element already exists");
            } else {
                checkDocumentElement((OMElement)child);
            }
        }
    }

    public void AxiomDocument.checkDocumentElement(OMElement element) {
    }

    public final CoreElement AxiomDocument.getContextElement() {
        return null;
    }
}
