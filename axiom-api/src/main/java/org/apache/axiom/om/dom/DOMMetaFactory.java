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
package org.apache.axiom.om.dom;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocType;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMEntityReference;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMInformationItem;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMText;
import org.apache.axiom.soap.SOAPFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * Extension interface for {@link OMMetaFactory} implementations that support {@link
 * OMAbstractFactory#FEATURE_DOM}.
 *
 * <p>Axiom implementations supporting this feature MUST conform to the Axiom API as well as the DOM
 * API, and nodes created by the implementation MUST implement both the Axiom interfaces and the DOM
 * interfaces corresponding, as specified by the following table:
 *
 * <p>
 *
 * <table border="1">
 * <caption>Mapping between Axiom and DOM interfaces</caption>
 * <tr>
 * <th>Axiom interface</th>
 * <th>DOM interface</th>
 * </tr>
 * <tr>
 * <td>{@link OMDocument}</td>
 * <td>{@link Document}</td>
 * </tr>
 * <tr>
 * <td>{@link OMDocType}</td>
 * <td>{@link DocumentType}</td>
 * </tr>
 * <tr>
 * <td>{@link OMElement}</td>
 * <td>{@link Element} [1]</td>
 * </tr>
 * <tr>
 * <td>{@link OMAttribute}</td>
 * <td>{@link Attr} [2]</td>
 * </tr>
 * <tr>
 * <td>{@link OMText} with type {@link OMNode#TEXT_NODE} or {@link OMNode#SPACE_NODE}</td>
 * <td>{@link Text}</td>
 * </tr>
 * <tr>
 * <td>{@link OMText} with type {@link OMNode#CDATA_SECTION_NODE}</td>
 * <td>{@link CDATASection}</td>
 * </tr>
 * <tr>
 * <td>{@link OMComment}</td>
 * <td>{@link Comment}</td>
 * </tr>
 * <tr>
 * <td>{@link OMProcessingInstruction}</td>
 * <td>{@link ProcessingInstruction}</td>
 * </tr>
 * <tr>
 * <td>{@link OMEntityReference}</td>
 * <td>{@link EntityReference}</td>
 * </tr>
 * <tr>
 * <td>-</td>
 * <td>{@link DocumentFragment}</td>
 * </tr>
 * </table>
 *
 * <dl>
 *   <dt>[1]
 *   <dd>Only applies to elements created using DOM 2 methods such as {@link
 *       Document#createElementNS(String, String)}.
 *   <dt>[2]
 *   <dd>Only applies to attributes created using DOM 2 methods such as {@link
 *       Document#createAttributeNS(String, String)} and that don't represent namespace
 *       declarations. Axiom doesn't use {@link OMAttribute} to represent namespace declarations,
 *       and {@link OMNamespace} instances representing namespace declarations are not expected to
 *       implement {@link Attr}.
 * </dl>
 *
 * <p>The Axiom API is designed such that nodes are created using a factory ({@link OMFactory} or
 * {@link SOAPFactory}) that is expected to be a singleton and stateless. On the other hand, in the
 * DOM API, the {@link Document} instance plays the role of node factory, and each node (explicitly
 * or implicitly) keeps a reference to the {@link Document} instance from which it was created (the
 * <i>owner document</i>). To address this difference in a consistent way and to make it possible to
 * use both the Axiom API and the DOM API on the same object model instance, the implementation MUST
 * conform to the following rules:
 *
 * <ol>
 *   <li>Nodes created using the Axiom API and for which a parent node is specified will have as
 *       their owner document the owner document of the parent. Note that this is simply a
 *       consequence of the fact that DOM is designed such that two nodes that are part of the same
 *       tree must have the same owner document.
 *   <li>Nodes created using the Axiom API and for which no parent node is specified will get a new
 *       owner document. This applies to methods in {@link OMFactory} that don't have an {@link
 *       OMContainer} parameter or that are invoked with a <code>null</code> {@link OMContainer} as
 *       well as to methods such as {@link OMElement#cloneOMElement()}.
 *   <li>When the Axiom API is used to add a node A as a child of another node B, then the owner
 *       document of B becomes the new owner document of A and all its descendants. In DOM parlance,
 *       this means that node A is automatically adopted by the owner document of B. This implies
 *       that no method defined by the Axiom API will ever trigger a {@link
 *       DOMException#WRONG_DOCUMENT_ERR} error.
 *   <li>When a node is detached from its parent using the Axiom API, it will get a new owner
 *       document. This rule exists for consistency because together with the other rules it implies
 *       that every tree has a distinct owner document as long as only the Axiom API is used to
 *       manipulate the nodes. That rule applies to the following methods:
 *       <ul>
 *         <li>{@link OMNode#detach()}
 *         <li>{@link OMElement#removeAttribute(OMAttribute)}
 *         <li>{@link OMElement#setText(String)} and {@link OMElement#setText(QName)} (in the case
 *             where the side effect of the invocation is to detach preexisting nodes)
 *         <li>{@link OMElement#addAttribute(OMAttribute)} and {@link OMElement#addAttribute(String,
 *             String, OMNamespace)} (in the case where the new attribute replaces an existing one,
 *             which will be removed from its owner)
 *       </ul>
 *   <li>{@link Document} instances created using the {@link DocumentBuilderFactory} and {@link
 *       DOMImplementation} APIs as well as the {@link Document} instances implicitly created (as
 *       owner documents) by the Axiom API will have as their {@link OMFactory} (as reported by
 *       {@link OMInformationItem#getOMFactory()}) the instance returned by {@link
 *       OMMetaFactory#getOMFactory()}. Any additional nodes created using the DOM API will inherit
 *       the {@link OMFactory} of the owner document.
 * </ol>
 *
 * <p>The implementation SHOULD instantiate the implicitly created owner documents lazily (typically
 * when explicitly requested using DOM's {@link Node#getOwnerDocument()} API) to avoid creating a
 * large number of temporary {@link Document} instances when the Axiom API is used. Note however
 * that this has no impact on the behavior visible to the application code.
 *
 * <p>As indicated in the table above, although {@link Attr} and {@link DocumentFragment} nodes are
 * parent nodes in the DOM API, they MUST NOT implement the {@link OMContainer} interface. Only
 * {@link OMDocument} and {@link OMElement} instances can implement that interface.
 * <!-- TODO: describe the implications for the getParent method -->
 */
public interface DOMMetaFactory extends OMMetaFactory {
    /**
     * Create a new {@link DocumentBuilderFactory}. Since Axiom doesn't support non namespace aware
     * processing, the returned factory is always configured with <code>namespaceAware</code> set to
     * <code>true</code> (in contrast to the default settings used by {@link
     * DocumentBuilderFactory#newInstance()}).
     *
     * @return the factory instance
     */
    DocumentBuilderFactory newDocumentBuilderFactory();

    /**
     * Get the {@link DOMImplementation} instance.
     *
     * @return the {@link DOMImplementation} instance
     */
    DOMImplementation getDOMImplementation();
}
