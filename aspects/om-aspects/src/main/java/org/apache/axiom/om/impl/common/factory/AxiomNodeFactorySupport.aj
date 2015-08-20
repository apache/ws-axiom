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
package org.apache.axiom.om.impl.common.factory;

import javax.xml.namespace.QName;

import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocType;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.impl.common.AxiomCDATASection;
import org.apache.axiom.om.impl.common.AxiomCharacterDataNode;
import org.apache.axiom.om.impl.common.AxiomContainer;
import org.apache.axiom.om.impl.common.AxiomDocType;
import org.apache.axiom.om.impl.common.AxiomDocument;
import org.apache.axiom.om.impl.common.AxiomElement;
import org.apache.axiom.om.impl.common.AxiomProcessingInstruction;
import org.apache.axiom.om.impl.common.AxiomText;
import org.apache.axiom.om.impl.common.Policies;
import org.apache.axiom.om.impl.common.TextContent;

public aspect AxiomNodeFactorySupport {
    public final OMDocument AxiomNodeFactory.createOMDocument() {
        return (AxiomDocument)createDocument();
    }

    public final OMDocument AxiomNodeFactory.createOMDocument(OMXMLParserWrapper builder) {
        AxiomDocument document = (AxiomDocument)createDocument();
        document.coreSetBuilder(builder);
        return document;
    }

    public final OMDocType AxiomNodeFactory.createOMDocType(OMContainer parent, String rootName,
            String publicId, String systemId, String internalSubset) {
        return createOMDocType(parent, rootName, publicId, systemId, internalSubset, false);
    }

    public final OMDocType AxiomNodeFactory.createOMDocType(OMContainer parent, String rootName,
            String publicId, String systemId, String internalSubset, boolean fromBuilder) {
        AxiomDocType node = (AxiomDocType)createDocumentTypeDeclaration();
        node.coreSetRootName(rootName);
        node.coreSetPublicId(publicId);
        node.coreSetSystemId(systemId);
        node.coreSetInternalSubset(internalSubset);
        if (parent != null) {
            ((OMContainerEx)parent).addChild(node, fromBuilder);
        }
        return node;
    }

    public void AxiomNodeFactory.validateOMTextParent(OMContainer parent) {
    }
    
    private AxiomText AxiomNodeFactory.createAxiomText(OMContainer parent, Object content, int type, boolean fromBuilder) {
        AxiomText node;
        switch (type) {
            case OMNode.TEXT_NODE: {
                node = (AxiomCharacterDataNode)createCharacterDataNode();
                break;
            }
            case OMNode.SPACE_NODE: {
                AxiomCharacterDataNode cdata = (AxiomCharacterDataNode)createCharacterDataNode();
                cdata.coreSetIgnorable(true);
                node = cdata;
                break;
            }
            case OMNode.CDATA_SECTION_NODE: {
                node = (AxiomCDATASection)createCDATASection();
                break;
            }
            default:
                throw new IllegalArgumentException("Invalid node type");
        }
        if (parent != null) {
            validateOMTextParent(parent);
            ((OMContainerEx)parent).addChild(node, fromBuilder);
        }
        node.coreSetCharacterData(content, Policies.DETACH_POLICY);
        return node;
    }

    public final OMText AxiomNodeFactory.createOMText(OMContainer parent, String text, int type, boolean fromBuilder) {
        return createAxiomText(parent, text, type, fromBuilder);
    }
    
    public final OMText AxiomNodeFactory.createOMText(String s, int type) {
        return createAxiomText(null, s, type, false);
    }

    public final OMText AxiomNodeFactory.createOMText(String s) {
        return createAxiomText(null, s, OMNode.TEXT_NODE, false);
    }

    public final OMText AxiomNodeFactory.createOMText(OMContainer parent, String text, int type) {
        return createAxiomText(parent, text, type, false);
    }
    
    public final OMText AxiomNodeFactory.createOMText(OMContainer parent, String text) {
        return createAxiomText(parent, text, OMNode.TEXT_NODE, false);
    }
    
    public final OMText AxiomNodeFactory.createOMText(OMContainer parent, char[] charArray, int type) {
        return createAxiomText(parent, new String(charArray), type, false);
    }

    public final OMText AxiomNodeFactory.createOMText(OMContainer parent, QName text, int type) {
        if (text == null) {
            throw new IllegalArgumentException("QName text arg cannot be null!");
        }
        OMNamespace ns = ((AxiomElement)parent).handleNamespace(text.getNamespaceURI(), text.getPrefix());
        return createAxiomText(parent, ns == null ? text.getLocalPart() : ns.getPrefix() + ":" + text.getLocalPart(), type, false);
    }
    
    public final OMText AxiomNodeFactory.createOMText(OMContainer parent, QName text) {
        return createAxiomText(parent, text, OMNode.TEXT_NODE, false);
    }

    public final OMText AxiomNodeFactory.createOMText(OMContainer parent, String s, String mimeType, boolean optimize) {
        return createAxiomText(parent, new TextContent(s, mimeType, optimize), OMNode.TEXT_NODE, false);
    }

    public final OMText AxiomNodeFactory.createOMText(String s, String mimeType, boolean optimize) {
        return createOMText(null, s, mimeType, optimize);
    }

    public final OMText AxiomNodeFactory.createOMText(OMContainer parent, OMText source) {
        // TODO: this doesn't necessarily produce a node with the expected OMFactory
        AxiomText node = ((AxiomText)source).doClone();
        if (parent != null) {
            ((OMContainerEx)parent).addChild(node, false);
        }
        return node;
    }

    public final OMText AxiomNodeFactory.createOMText(Object dataHandler, boolean optimize) {
        return createOMText(null, dataHandler, optimize, false);
    }

    public final OMText AxiomNodeFactory.createOMText(OMContainer parent, Object dataHandler, boolean optimize, boolean fromBuilder) {
        return createAxiomText(parent, new TextContent(dataHandler, optimize), OMNode.TEXT_NODE, fromBuilder);
    }

    public final OMText AxiomNodeFactory.createOMText(String contentID, DataHandlerProvider dataHandlerProvider, boolean optimize) {
        return createAxiomText(null, new TextContent(contentID, dataHandlerProvider, optimize), OMNode.TEXT_NODE, false);
    }
    
    public final OMProcessingInstruction AxiomNodeFactory.createOMProcessingInstruction(
            OMContainer parent, String piTarget, String piData) {
        return createOMProcessingInstruction(parent, piTarget, piData, false);
    }

    public final OMProcessingInstruction AxiomNodeFactory.createOMProcessingInstruction(
            OMContainer parent, String piTarget, String piData, boolean fromBuilder) {
        AxiomProcessingInstruction node = (AxiomProcessingInstruction)createProcessingInstruction();
        node.coreSetTarget(piTarget);
        node.coreSetCharacterData(piData, Policies.DETACH_POLICY);
        if (parent != null) {
            ((OMContainerEx)parent).addChild(node, fromBuilder);
        }
        return node;
    }

    public final OMElement AxiomNodeFactory.createOMElement(String localName, OMNamespace ns) {
        return createOMElement(localName, ns, null);
    }

    public final OMElement AxiomNodeFactory.createOMElement(String localName, OMNamespace ns, OMContainer parent) {
        AxiomElement element = (AxiomElement)createNSAwareElement();
        if (parent != null) {
            parent.addChild(element);
        }
        element.initName(localName, ns, true);
        return element;
    }

    public final OMElement AxiomNodeFactory.createOMElement(String localName, OMContainer parent,
            OMXMLParserWrapper builder) {
        AxiomElement element = (AxiomElement)createNSAwareElement();
        element.coreSetBuilder(builder);
        if (parent != null) {
            ((AxiomContainer)parent).addChild(element, true);
        }
        element.initName(localName, null, false);
        return element;
    }
}
