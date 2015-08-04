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
package org.apache.axiom.om.impl.common;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;

public aspect AxiomDocumentSupport {
    public final OMElement AxiomDocument.getOMDocumentElement() {
        return (OMElement)coreGetDocumentElement();
    }

    public final void AxiomDocument.setOMDocumentElement(OMElement documentElement) {
        if (documentElement == null) {
            throw new IllegalArgumentException("documentElement must not be null");
        }
        OMElement existingDocumentElement = getOMDocumentElement();
        if (existingDocumentElement == null) {
            addChild(documentElement);
        } else {
            OMNode nextSibling = existingDocumentElement.getNextOMSibling();
            existingDocumentElement.detach();
            if (nextSibling == null) {
                addChild(documentElement);
            } else {
                nextSibling.insertSiblingBefore(documentElement);
            }
        }
    }

    public final void AxiomDocument.internalSerialize(Serializer serializer, OMOutputFormat format, boolean cache) throws OutputException {
        internalSerialize(serializer, format, cache, !format.isIgnoreXMLDeclaration());
    }

    // Overridden in AxiomSOAPMessageSupport
    public void AxiomDocument.internalSerialize(Serializer serializer, OMOutputFormat format,
            boolean cache, boolean includeXMLDeclaration) throws OutputException {
        if (includeXMLDeclaration) {
            //Check whether the OMOutput char encoding and OMDocument char
            //encoding matches, if not use char encoding of OMOutput
            String encoding = format.getCharSetEncoding();
            if (encoding == null || "".equals(encoding)) {
                encoding = getCharsetEncoding();
            }
            String version = getXMLVersion();
            if (version == null) {
                version = "1.0";
            }
            if (encoding == null) {
                serializer.writeStartDocument(version);
            } else {
                serializer.writeStartDocument(encoding, version);
            }
        }
        serializer.serializeChildren(this, format, cache);
        serializer.writeEndDocument();
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

}
