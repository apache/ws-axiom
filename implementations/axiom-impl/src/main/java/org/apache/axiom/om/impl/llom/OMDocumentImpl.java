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

package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMInformationItem;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.common.AxiomDocument;
import org.apache.axiom.om.impl.common.OMDocumentHelper;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;

import java.util.Iterator;

/** Class OMDocumentImpl */
public class OMDocumentImpl extends OMSerializableImpl implements AxiomDocument {
    /** Field charSetEncoding Default : UTF-8 */
    protected String charSetEncoding = "UTF-8";

    /** Field xmlVersion */
    protected String xmlVersion = "1.0";
    
    protected String xmlEncoding;

    protected String isStandalone;

    /**
     * Create a <code>OMDocument</code> given the <code>OMFactory</code>
     *
     * @param factory The <code>OMFactory</code> that created this instace
     */
    public OMDocumentImpl(OMFactory factory) {
        super(factory);
        coreSetState(COMPLETE);
    }

    /**
     * Create the <code>OMDocument</code> with the factory
     *
     * @param parserWrapper
     * @param factory
     */
    public OMDocumentImpl(OMXMLParserWrapper parserWrapper, OMFactory factory) {
        super(factory);
        coreSetBuilder(parserWrapper);
    }

    public void setOMDocumentElement(OMElement documentElement) {
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

    public void setComplete(boolean complete) {
        coreSetState(complete ? COMPLETE : INCOMPLETE);
    }

    public final void checkChild(OMNode child) {
        if (child instanceof OMElement) {
            if (getOMDocumentElement() != null) {
                throw new OMException("Document element already exists");
            } else {
                checkDocumentElement((OMElement)child);
            }
        }
    }

    protected void checkDocumentElement(OMElement element) {
    }

    public String getCharsetEncoding() {
        return charSetEncoding;
    }

    public void setCharsetEncoding(String charEncoding) {
        this.charSetEncoding = charEncoding;
    }

    public String isStandalone() {
        return isStandalone;
    }

    public void setStandalone(String isStandalone) {
        this.isStandalone = isStandalone;
    }

    public String getXMLVersion() {
        return xmlVersion;
    }

    public void setXMLVersion(String xmlVersion) {
        this.xmlVersion = xmlVersion;
    }

    public String getXMLEncoding() {
        return xmlEncoding;
    }

    public void setXMLEncoding(String encoding) {
        this.xmlEncoding = encoding;
    }

    public void internalSerialize(Serializer serializer, OMOutputFormat format, boolean cache) throws OutputException {
        internalSerialize(serializer, format, cache, !format.isIgnoreXMLDeclaration());
    }

    protected void internalSerialize(Serializer serializer, OMOutputFormat format,
                                     boolean cache, boolean includeXMLDeclaration) throws OutputException {
        OMDocumentHelper.internalSerialize(this, serializer, format, cache, includeXMLDeclaration);
    }

    void notifyChildComplete() {
        if (getState() == INCOMPLETE && getBuilder() == null) {
            Iterator iterator = getChildren();
            while (iterator.hasNext()) {
                OMNode node = (OMNode) iterator.next();
                if (!node.isComplete()) {
                    return;
                }
            }
            this.setComplete(true);
        }
    }
    
    public void build() {
        defaultBuild();
    }
    
    public OMInformationItem clone(OMCloneOptions options) {
        OMDocument targetDocument;
        if (options.isPreserveModel()) {
            targetDocument = createClone(options);
        } else {
            targetDocument = getOMFactory().createOMDocument();
        }
        targetDocument.setXMLVersion(xmlVersion);
        targetDocument.setXMLEncoding(xmlEncoding);
        targetDocument.setCharsetEncoding(charSetEncoding);
        targetDocument.setStandalone(isStandalone);
        for (Iterator it = getChildren(); it.hasNext(); ) {
            ((OMNodeImpl)it.next()).clone(options, targetDocument);
        }
        return targetDocument;
    }

    protected OMDocument createClone(OMCloneOptions options) {
        return getOMFactory().createOMDocument();
    }
}
