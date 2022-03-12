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

import java.util.Iterator;

import org.apache.axiom.core.Axis;
import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.Mappers;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSerializable;
import org.apache.axiom.om.impl.common.AxiomExceptionTranslator;
import org.apache.axiom.om.impl.common.AxiomSemantics;
import org.apache.axiom.om.impl.intf.AxiomDocument;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.intf.AxiomSerializable;
import org.apache.axiom.weaver.annotation.Mixin;

@Mixin
public abstract class AxiomDocumentMixin implements AxiomDocument {
    @Override
    public final OMElement getOMDocumentElement() {
        try {
            return (OMElement) coreGetDocumentElement();
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }

    @Override
    public final void setOMDocumentElement(OMElement documentElement) {
        try {
            if (documentElement == null) {
                throw new IllegalArgumentException("documentElement must not be null");
            }
            AxiomElement existingDocumentElement = (AxiomElement) coreGetDocumentElement();
            if (existingDocumentElement == null) {
                addChild(documentElement);
            } else {
                existingDocumentElement.coreReplaceWith(
                        (AxiomElement) documentElement, AxiomSemantics.INSTANCE);
            }
        } catch (CoreModelException ex) {
            throw AxiomExceptionTranslator.translate(ex);
        }
    }

    @Override
    public final String getCharsetEncoding() {
        String inputEncoding = coreGetInputEncoding();
        return inputEncoding == null ? "UTF-8" : inputEncoding;
    }

    @Override
    public final void setCharsetEncoding(String charsetEncoding) {
        coreSetInputEncoding(charsetEncoding);
    }

    @Override
    public final String getXMLVersion() {
        return coreGetXmlVersion();
    }

    @Override
    public final void setXMLVersion(String xmlVersion) {
        coreSetXmlVersion(xmlVersion);
    }

    @Override
    public final String getXMLEncoding() {
        return coreGetXmlEncoding();
    }

    @Override
    public final void setXMLEncoding(String xmlEncoding) {
        coreSetXmlEncoding(xmlEncoding);
    }

    @Override
    public final String isStandalone() {
        Boolean standalone = coreGetStandalone();
        if (standalone == null) {
            return null;
        } else {
            return standalone ? "yes" : "no";
        }
    }

    @Override
    public final void setStandalone(String standalone) {
        coreSetStandalone("yes".equalsIgnoreCase(standalone));
    }

    @Override
    public final void checkChild(OMNode child) {
        if (child instanceof OMElement) {
            if (getOMDocumentElement() != null) {
                throw new OMException("Document element already exists");
            } else {
                checkDocumentElement((OMElement) child);
            }
        }
    }

    public void checkDocumentElement(OMElement element) {}

    @Override
    public final CoreElement getContextElement() {
        return null;
    }

    @Override
    public Iterator<OMSerializable> getDescendants(boolean includeSelf) {
        return coreGetNodes(
                includeSelf ? Axis.DESCENDANTS_OR_SELF : Axis.DESCENDANTS,
                AxiomSerializable.class,
                Mappers.<OMSerializable>identity(),
                AxiomSemantics.INSTANCE);
    }
}
