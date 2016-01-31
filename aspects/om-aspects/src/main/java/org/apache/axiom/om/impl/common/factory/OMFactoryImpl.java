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

import static org.apache.axiom.util.xml.NSUtils.generatePrefix;

import javax.xml.namespace.QName;

import org.apache.axiom.core.CoreNode;
import org.apache.axiom.core.NodeFactory;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMDocType;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMEntityReference;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.common.OMNamespaceImpl;
import org.apache.axiom.om.impl.common.AxiomSemantics;
import org.apache.axiom.om.impl.intf.AxiomAttribute;
import org.apache.axiom.om.impl.intf.AxiomCDATASection;
import org.apache.axiom.om.impl.intf.AxiomCharacterDataNode;
import org.apache.axiom.om.impl.intf.AxiomComment;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.intf.AxiomDocType;
import org.apache.axiom.om.impl.intf.AxiomDocument;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.intf.AxiomEntityReference;
import org.apache.axiom.om.impl.intf.AxiomProcessingInstruction;
import org.apache.axiom.om.impl.intf.AxiomSourcedElement;
import org.apache.axiom.om.impl.intf.AxiomText;
import org.apache.axiom.om.impl.intf.OMFactoryEx;
import org.apache.axiom.om.impl.intf.TextContent;

public class OMFactoryImpl implements OMFactoryEx {
    private final OMMetaFactory metaFactory;
    private final NodeFactory nodeFactory;
    
    public OMFactoryImpl(OMMetaFactory metaFactory, NodeFactory nodeFactory) {
        this.metaFactory = metaFactory;
        this.nodeFactory = nodeFactory;
    }

    protected final <T extends CoreNode> T createNode(Class<T> type) {
        T node = nodeFactory.createNode(type);
        initNode(node);
        return node;
    }
    
    protected void initNode(CoreNode node) {
    }
    
    public final OMMetaFactory getMetaFactory() {
        return metaFactory;
    }

    public final OMNamespace createOMNamespace(String uri, String prefix) {
        return new OMNamespaceImpl(uri, prefix);
    }
    
    public final OMDocument createOMDocument() {
        return createNode(AxiomDocument.class);
    }

    public final OMDocument createOMDocument(OMXMLParserWrapper builder) {
        AxiomDocument document = createNode(AxiomDocument.class);
        document.coreSetBuilder(builder);
        return document;
    }

    public final OMDocType createOMDocType(OMContainer parent, String rootName,
            String publicId, String systemId, String internalSubset) {
        return createOMDocType(parent, rootName, publicId, systemId, internalSubset, false);
    }

    public final OMDocType createOMDocType(OMContainer parent, String rootName,
            String publicId, String systemId, String internalSubset, boolean fromBuilder) {
        AxiomDocType node = createNode(AxiomDocType.class);
        node.coreSetRootName(rootName);
        node.coreSetPublicId(publicId);
        node.coreSetSystemId(systemId);
        node.coreSetInternalSubset(internalSubset);
        if (parent != null) {
            ((AxiomContainer)parent).addChild(node, fromBuilder);
        }
        return node;
    }

    private AxiomText createAxiomText(OMContainer parent, Object content, int type, boolean fromBuilder) {
        AxiomText node;
        switch (type) {
            case OMNode.TEXT_NODE: {
                node = createNode(AxiomCharacterDataNode.class);
                break;
            }
            case OMNode.SPACE_NODE: {
                AxiomCharacterDataNode cdata = createNode(AxiomCharacterDataNode.class);
                cdata.coreSetIgnorable(true);
                node = cdata;
                break;
            }
            case OMNode.CDATA_SECTION_NODE: {
                node = createNode(AxiomCDATASection.class);
                break;
            }
            default:
                throw new IllegalArgumentException("Invalid node type");
        }
        if (parent != null) {
            ((AxiomContainer)parent).addChild(node, fromBuilder);
        }
        node.coreSetCharacterData(content, AxiomSemantics.INSTANCE);
        return node;
    }

    public final OMText createOMText(OMContainer parent, String text, int type, boolean fromBuilder) {
        return createAxiomText(parent, text, type, fromBuilder);
    }
    
    public final OMText createOMText(String s, int type) {
        return createAxiomText(null, s, type, false);
    }

    public final OMText createOMText(String s) {
        return createAxiomText(null, s, OMNode.TEXT_NODE, false);
    }

    public final OMText createOMText(OMContainer parent, String text, int type) {
        return createAxiomText(parent, text, type, false);
    }
    
    public final OMText createOMText(OMContainer parent, String text) {
        return createAxiomText(parent, text, OMNode.TEXT_NODE, false);
    }
    
    public final OMText createOMText(OMContainer parent, char[] charArray, int type) {
        return createAxiomText(parent, new String(charArray), type, false);
    }

    public final OMText createOMText(OMContainer parent, QName text, int type) {
        if (text == null) {
            throw new IllegalArgumentException("QName text arg cannot be null!");
        }
        OMNamespace ns = ((AxiomElement)parent).handleNamespace(text.getNamespaceURI(), text.getPrefix());
        return createAxiomText(parent, ns == null ? text.getLocalPart() : ns.getPrefix() + ":" + text.getLocalPart(), type, false);
    }
    
    public final OMText createOMText(OMContainer parent, QName text) {
        return createAxiomText(parent, text, OMNode.TEXT_NODE, false);
    }

    public final OMText createOMText(OMContainer parent, String s, String mimeType, boolean optimize) {
        return createAxiomText(parent, new TextContent(s, mimeType, optimize), OMNode.TEXT_NODE, false);
    }

    public final OMText createOMText(String s, String mimeType, boolean optimize) {
        return createOMText(null, s, mimeType, optimize);
    }

    public final OMText createOMText(OMContainer parent, OMText source) {
        // TODO: this doesn't necessarily produce a node with the expected OMFactory
        return (AxiomText)((AxiomText)source).coreClone(AxiomSemantics.CLONE_POLICY, null, (AxiomContainer)parent);
    }

    public final OMText createOMText(Object dataHandler, boolean optimize) {
        return createOMText(null, dataHandler, optimize, false);
    }

    public final OMText createOMText(OMContainer parent, Object dataHandler, boolean optimize, boolean fromBuilder) {
        return createAxiomText(parent, new TextContent(dataHandler, optimize), OMNode.TEXT_NODE, fromBuilder);
    }

    public final OMText createOMText(String contentID, DataHandlerProvider dataHandlerProvider, boolean optimize) {
        return createAxiomText(null, new TextContent(contentID, dataHandlerProvider, optimize), OMNode.TEXT_NODE, false);
    }
    
    public final OMProcessingInstruction createOMProcessingInstruction(
            OMContainer parent, String piTarget, String piData) {
        return createOMProcessingInstruction(parent, piTarget, piData, false);
    }

    public final OMProcessingInstruction createOMProcessingInstruction(
            OMContainer parent, String piTarget, String piData, boolean fromBuilder) {
        AxiomProcessingInstruction node = createNode(AxiomProcessingInstruction.class);
        node.coreSetTarget(piTarget);
        node.coreSetCharacterData(piData, AxiomSemantics.INSTANCE);
        if (parent != null) {
            ((AxiomContainer)parent).addChild(node, fromBuilder);
        }
        return node;
    }

    public final OMEntityReference createOMEntityReference(OMContainer parent, String name) {
        return createOMEntityReference(parent, name, null, false);
    }

    public final OMEntityReference createOMEntityReference(OMContainer parent, String name, String replacementText, boolean fromBuilder) {
        AxiomEntityReference node = createNode(AxiomEntityReference.class);
        node.coreSetName(name);
        node.coreSetReplacementText(replacementText);
        if (parent != null) {
            ((AxiomContainer)parent).addChild(node, fromBuilder);
        }
        return node;
    }

    public final OMComment createOMComment(OMContainer parent, String content) {
        return createOMComment(parent, content, false);
    }

    public final OMComment createOMComment(OMContainer parent, String content, boolean fromBuilder) {
        AxiomComment node = createNode(AxiomComment.class);
        node.coreSetCharacterData(content, AxiomSemantics.INSTANCE);
        if (parent != null) {
            ((AxiomContainer)parent).addChild(node, fromBuilder);
        }
        return node;
    }
    
    public final OMElement createOMElement(String localName, OMNamespace ns) {
        return createOMElement(localName, ns, null);
    }

    protected final <T extends AxiomElement> T createAxiomElement(Class<T> type,
            OMContainer parent, String localName, OMNamespace ns, OMXMLParserWrapper builder,
            boolean generateNSDecl) {
        T element = createNode(type);
        element.coreSetBuilder(builder);
        if (parent != null) {
            ((AxiomContainer)parent).addChild(element, builder != null);
        }
        element.initName(localName, ns, generateNSDecl);
        return element;
    }

    public final OMElement createOMElement(String localName, OMNamespace ns, OMContainer parent) {
        return createAxiomElement(AxiomElement.class, parent, localName, ns, null, true);
    }

    public final <T extends AxiomElement> T createAxiomElement(Class<T> type, String localName,
            OMContainer parent, OMXMLParserWrapper builder) {
        return createAxiomElement(type, parent, localName, null, builder, false);
    }
    
    public final OMElement createOMElement(QName qname, OMContainer parent) {
        AxiomElement element = createNode(AxiomElement.class);
        if (parent != null) {
            parent.addChild(element);
        }
        element.internalSetLocalName(qname.getLocalPart());
        String prefix = qname.getPrefix();
        String namespaceURI = qname.getNamespaceURI();
        if (namespaceURI.length() > 0) {
            // The goal here is twofold:
            //  * check if the namespace needs to be declared;
            //  * locate an existing OMNamespace object, so that we can avoid creating a new one.
            OMNamespace ns = element.findNamespace(namespaceURI, prefix.length() == 0 ? null : prefix);
            if (ns == null) {
                if ("".equals(prefix)) {
                    prefix = generatePrefix(namespaceURI);
                }
                ns = element.declareNamespace(namespaceURI, prefix);
            }
            element.internalSetNamespace(ns);
        } else if (prefix.length() > 0) {
            throw new IllegalArgumentException("Cannot create a prefixed element with an empty namespace name");
        } else {
            if (element.getDefaultNamespace() != null) {
                element.declareDefaultNamespace("");
            }
            element.internalSetNamespace(null);
        }
        return element;
    }

    public final OMElement createOMElement(QName qname) {
        return createOMElement(qname, null);
    }
    
    public final OMElement createOMElement(String localName, String namespaceURI, String prefix) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("namespaceURI must not be null");
        } else if (namespaceURI.length() == 0) {
            if (prefix != null && prefix.length() > 0) {
                throw new IllegalArgumentException("Cannot create a prefixed element with an empty namespace name");
            }
            return createOMElement(localName, null);
        } else {
            return createOMElement(localName, createOMNamespace(namespaceURI, prefix));
        }
    }

    public final OMSourcedElement createOMElement(OMDataSource source) {
        AxiomSourcedElement element = createNode(AxiomSourcedElement.class);
        element.init(source);
        return element;
    }

    public final OMSourcedElement createOMElement(OMDataSource source, String localName, OMNamespace ns) {
        AxiomSourcedElement element = createNode(AxiomSourcedElement.class);
        element.init(localName, ns, source);
        return element;
    }

    public final OMSourcedElement createOMElement(OMDataSource source, QName qname) {
        AxiomSourcedElement element = createNode(AxiomSourcedElement.class);
        element.init(qname, source);
        return element;
    }

    public final OMAttribute createOMAttribute(String localName, OMNamespace ns, String value) {
        if (ns != null && ns.getPrefix() == null) {
            String namespaceURI = ns.getNamespaceURI();
            if (namespaceURI.length() == 0) {
                ns = null;
            } else {
                ns = new OMNamespaceImpl(namespaceURI, generatePrefix(namespaceURI));
            }
        }
        if (ns != null) {
            if (ns.getNamespaceURI().length() == 0) {
                if (ns.getPrefix().length() > 0) {
                    throw new IllegalArgumentException("Cannot create a prefixed attribute with an empty namespace name");
                } else {
                    ns = null;
                }
            } else if (ns.getPrefix().length() == 0) {
                throw new IllegalArgumentException("Cannot create an unprefixed attribute with a namespace");
            }
        }
        AxiomAttribute attr = createNode(AxiomAttribute.class);
        attr.internalSetLocalName(localName);
        attr.coreSetCharacterData(value, AxiomSemantics.INSTANCE);
        attr.internalSetNamespace(ns);
        attr.coreSetType(OMConstants.XMLATTRTYPE_CDATA);
        return attr;
    }

    // <old-and-buggy-code>
    public final OMNode importNode(OMNode child) {
        int type = child.getType();
        switch (type) {
            case OMNode.ELEMENT_NODE: {
                OMElement childElement = (OMElement) child;
                OMElement newElement = (OMXMLBuilderFactory.createStAXOMBuilder(this, childElement
                        .getXMLStreamReader())).getDocumentElement();
                newElement.buildWithAttachments();
                return newElement;
            }
            case OMNode.TEXT_NODE: {
                OMText importedText = (OMText) child;
                OMText newText;
                if (importedText.isBinary()) {
                    boolean isOptimize = importedText.isOptimized();
                    newText = createOMText(importedText
                            .getDataHandler(), isOptimize);
                } else {
                    newText = createOMText(null, importedText
                            .getText()/*, importedText.getOMNodeType()*/);
                }
                return newText;
            }

            case OMNode.PI_NODE: {
                OMProcessingInstruction importedPI = (OMProcessingInstruction) child;
                return createOMProcessingInstruction(null,
                                                                  importedPI.getTarget(),
                                                                  importedPI.getValue());
            }
            case OMNode.COMMENT_NODE: {
                OMComment importedComment = (OMComment) child;
                return createOMComment(null, importedComment.getValue());
            }
            case OMNode.DTD_NODE : {
                OMDocType importedDocType = (OMDocType) child;
                return createOMDocType(null, importedDocType.getRootName(),
                        importedDocType.getPublicId(), importedDocType.getSystemId(),
                        importedDocType.getInternalSubset());
            }
            default: {
                throw new UnsupportedOperationException(
                        "Not Implemented Yet for the given node type");
            }
        }
    }
    // </old-and-buggy-code>
}
