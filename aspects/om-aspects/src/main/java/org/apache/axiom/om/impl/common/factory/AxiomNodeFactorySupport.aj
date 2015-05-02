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
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.impl.common.AxiomCDATASection;
import org.apache.axiom.om.impl.common.AxiomCharacterData;
import org.apache.axiom.om.impl.common.AxiomElement;
import org.apache.axiom.om.impl.common.AxiomText;

public aspect AxiomNodeFactorySupport {
    public void AxiomNodeFactory.validateOMTextParent(OMContainer parent) {
    }
    
    private AxiomText AxiomNodeFactory.createAxiomText(OMContainer parent, int type, boolean fromBuilder) {
        AxiomText node;
        switch (type) {
            case OMNode.TEXT_NODE: {
                node = (AxiomCharacterData)createCharacterData();
                break;
            }
            case OMNode.SPACE_NODE: {
                AxiomCharacterData cdata = (AxiomCharacterData)createCharacterData();
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
        return node;
    }

    public final OMText AxiomNodeFactory.createOMText(OMContainer parent, String text, int type, boolean fromBuilder) {
        AxiomText node = createAxiomText(parent, type, fromBuilder);
        node.internalSetValue(text);
        return node;
    }
    
    public final OMText AxiomNodeFactory.createOMText(String s, int type) {
        return createOMText(null, s, type, false);
    }

    public final OMText AxiomNodeFactory.createOMText(String s) {
        return createOMText(null, s, OMNode.TEXT_NODE, false);
    }

    public final OMText AxiomNodeFactory.createOMText(OMContainer parent, String text, int type) {
        return createOMText(parent, text, type, false);
    }
    
    public final OMText AxiomNodeFactory.createOMText(OMContainer parent, String text) {
        return createOMText(parent, text, OMNode.TEXT_NODE, false);
    }
    
    public final OMText AxiomNodeFactory.createOMText(OMContainer parent, char[] charArray, int type) {
        return createOMText(parent, new String(charArray), type, false);
    }

    public final OMText AxiomNodeFactory.createOMText(OMContainer parent, QName text, int type) {
        if (text == null) {
            throw new IllegalArgumentException("QName text arg cannot be null!");
        }
        AxiomText node = createAxiomText(parent, type, false);
        OMNamespace ns = ((AxiomElement)parent).handleNamespace(text.getNamespaceURI(), text.getPrefix());
        node.internalSetValue(ns == null ? text.getLocalPart() : ns.getPrefix() + ":" + text.getLocalPart());
        return node;
    }
    
    public final OMText AxiomNodeFactory.createOMText(OMContainer parent, QName text) {
        return createOMText(parent, text, OMNode.TEXT_NODE);
    }

    public final OMText AxiomNodeFactory.createOMText(OMContainer parent, String s, String mimeType, boolean optimize) {
        AxiomText node = createAxiomText(parent, OMNode.TEXT_NODE, false);
        node.internalSetValue(s);
        node.internalSetMimeType(mimeType);
        node.setOptimize(optimize);
        node.setBinary(true);
        return node;
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
        AxiomText node = createAxiomText(parent, OMNode.TEXT_NODE, fromBuilder);
        node.internalSetDataHandlerObject(dataHandler);
        node.setBinary(true);
        node.setOptimize(optimize);
        return node;
    }

    public final OMText AxiomNodeFactory.createOMText(String contentID, DataHandlerProvider dataHandlerProvider, boolean optimize) {
        AxiomText node = createAxiomText(null, OMNode.TEXT_NODE, false);
        node.setContentID(contentID);
        node.internalSetDataHandlerObject(dataHandlerProvider);
        node.setBinary(true);
        node.setOptimize(optimize);
        return node;
    }
}
