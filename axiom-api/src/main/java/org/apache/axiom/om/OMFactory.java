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

package org.apache.axiom.om;

import javax.xml.namespace.QName;

import org.apache.axiom.blob.Blob;
import org.apache.axiom.ext.stax.BlobProvider;

/** Class OMFactory */
public interface OMFactory {
    /**
     * Get the {@link OMMetaFactory} from which this factory was obtained. More precisely, if the
     * {@link OMFactory} instance has been obtained from a {@link OMMetaFactory} using {@link
     * OMMetaFactory#getOMFactory()}, {@link OMMetaFactory#getSOAP11Factory()} or {@link
     * OMMetaFactory#getSOAP12Factory()}, then the return value is the same as the original {@link
     * OMMetaFactory}. Since {@link OMAbstractFactory} creates a single {@link OMMetaFactory}
     * instance per Axiom implementation, this means that this method can be used to check if two
     * {@link OMFactory} instances belong to the same Axiom implementation.
     *
     * @return the meta factory
     */
    OMMetaFactory getMetaFactory();

    /** Creates a new OMDocument. */
    OMDocument createOMDocument();

    /**
     * Create an element with the given name. If a namespace is given, a namespace declaration will
     * be added automatically to the newly created element.
     *
     * @param localName the local part of the name; must not be <code>null</code>
     * @param ns the namespace, or <code>null</code> if the element has no namespace
     * @return the newly created element
     * @throws IllegalArgumentException if an attempt is made to create a prefixed element with an
     *     empty namespace name
     */
    OMElement createOMElement(String localName, OMNamespace ns);

    /**
     * Create an element with the given name and parent. If the specified {@link OMNamespace} has a
     * namespace URI but a <code>null</code> prefix, the method will reuse an existing prefix if a
     * namespace declaration with a matching namespace URI is in scope on the parent or generate a
     * new prefix if no such namespace declaration exists.
     *
     * <p>If a new prefix is generated or if the specified prefix and namespace URI are not bound in
     * the scope of the parent element, the method will add an appropriate namespace declaration to
     * the new element. Note that this may also occur if <code>null</code> is passed as {@link
     * OMNamespace} parameter. In that case, if there is a default namespace declaration with a non
     * empty namespace URI in the scope of the parent element, a namespace declaration needs to be
     * added to the newly created element to override the default namespace.
     *
     * @param localName
     * @param ns
     * @param parent the parent to which the newly created element will be added; this may be <code>
     *     null</code>, in which case the behavior of the method is the same as {@link
     *     #createOMElement(String, OMNamespace)}
     * @return the newly created element
     * @throws OMException
     * @throws IllegalArgumentException if an attempt is made to create a prefixed element with an
     *     empty namespace name
     */
    OMElement createOMElement(String localName, OMNamespace ns, OMContainer parent)
            throws OMException;

    /**
     * Create a sourced element. If the data source implements {@link QNameAwareOMDataSource} then
     * the returned {@link OMSourcedElement} will use the information provided through this
     * interface to determine the local name, namespace URI and namespace prefix. For information
     * that is not available (either because the data source doesn't implement {@link
     * QNameAwareOMDataSource} or because some of the methods defined by that interface return
     * <code>null</code>) the element will be expanded to determine the missing information. This is
     * done lazily, i.e. only when the information is really required. E.g. this will not occur
     * during serialization of the element.
     *
     * <p>This is an optional operation which may not be supported by all factories.
     *
     * @param source the data source; must not be <code>null</code>
     * @return the newly created element
     * @throws IllegalArgumentException if <code>source</code> is <code>null</code>
     */
    OMSourcedElement createOMElement(OMDataSource source);

    /**
     * Create a sourced element with a known local name and namespace URI. If the namespace prefix
     * is known in advance, then the caller should specify it in the provided {@link OMNamespace}
     * object. The caller may pass an {@link OMNamespace} instance with a <code>null</code> prefix.
     * This indicates that the prefix is unknown and will be determined lazily by expanding the
     * element.
     *
     * <p>Note that if the provided data source implements {@link QNameAwareOMDataSource}, then the
     * information returned by {@link QNameAwareOMDataSource#getPrefix()} may be used to determine
     * the prefix. However, this is an unusual use case.
     *
     * <p>Also note that if the specified namespace URI is empty, then the element can't have a
     * prefix and it is not necessary to expand the element to determine its prefix.
     *
     * <p>This is an optional operation which may not be supported by all factories.
     *
     * @param source the data source; must not be <code>null</code>
     * @param localName the local part of the name of the element produced by the data source; must
     *     not be <code>null</code>
     * @param ns the namespace of the element produced by the data source, or <code>null</code> if
     *     the element has no namespace
     * @return the newly created element
     * @throws IllegalArgumentException if <code>source</code> is <code>null</code>
     */
    OMSourcedElement createOMElement(OMDataSource source, String localName, OMNamespace ns);

    /**
     * Create a sourced element with a known local name, namespace URI and namespace prefix.
     *
     * <p>This is an optional operation which may not be supported by all factories.
     *
     * @param source the data source; must not be <code>null</code>
     * @param qname the name of the element produced by the data source; must not be <code>null
     *     </code>
     * @return the newly created element
     * @throws IllegalArgumentException if <code>source</code> is <code>null</code>
     */
    OMSourcedElement createOMElement(OMDataSource source, QName qname);

    /**
     * Create an element with the given name. If a namespace is given, a namespace declaration will
     * be added automatically to the newly created element.
     *
     * @param localName the local part of the name; must not be <code>null</code>
     * @param namespaceURI the namespace URI, or the empty string if the element has no namespace;
     *     must not be <code>null</code>
     * @param prefix the namespace prefix, or <code>null</code> if a prefix should be generated
     * @return the newly created OMElement.
     * @throws IllegalArgumentException if <code>namespaceURI</code> is <code>null</code> or if an
     *     attempt is made to create a prefixed element with an empty namespace name
     */
    OMElement createOMElement(String localName, String namespaceURI, String prefix);

    /**
     * Create an element with the given {@link QName} and parent. If a namespace URI is given but no
     * prefix, the method will use an appropriate prefix if a corresponding namespace declaration is
     * in scope on the parent or generate a new prefix if no corresponding namespace declaration is
     * in scope. If a new prefix is generated or if the specified prefix and namespace URI are not
     * bound in the scope of the parent element, the method will add an appropriate namespace
     * declaration to the new element.
     *
     * @param qname the {@link QName} defining the name of the element to be created
     * @param parent the parent to which the newly created element will be added; this may be <code>
     *     null</code>, in which case the behavior of the method is the same as {@link
     *     #createOMElement(QName)}
     * @return the new element
     * @throws IllegalArgumentException if an attempt is made to create a prefixed element with an
     *     empty namespace name
     */
    OMElement createOMElement(QName qname, OMContainer parent);

    /**
     * Create an element with the given {@link QName}. If a namespace URI is given but no prefix,
     * the method will automatically generate a prefix for the element. If a namespace URI is given,
     * the method will also add a namespace declaration to the element, binding the auto-generated
     * prefix or the prefix given in the {@link QName} to the given namespace URI. If neither a
     * namespace URI nor a prefix is given, no namespace declaration will be added.
     *
     * @param qname the {@link QName} defining the name of the element to be created
     * @return the new element
     * @throws IllegalArgumentException if an attempt is made to create a prefixed element with an
     *     empty namespace name
     */
    OMElement createOMElement(QName qname);

    /**
     * Create an {@link OMNamespace} instance or retrieve an existing one if the factory supports
     * pooling.
     *
     * @param uri the namespace URI; must not be <code>null</code>
     * @param prefix the prefix
     * @return the {@link OMNamespace} instance
     * @throws IllegalArgumentException if <code>uri</code> is null
     */
    OMNamespace createOMNamespace(String uri, String prefix);

    /**
     * Creates a new {@link OMText} node with the given value and appends it to the given parent
     * element.
     *
     * @param parent the parent to which the newly created text node will be added; this may be
     *     <code>null</code>, in which case the behavior of the method is the same as {@link
     *     #createOMText(String)}
     * @param text
     * @return Returns OMText.
     */
    OMText createOMText(OMContainer parent, String text);

    /**
     * Create OMText node that is a copy of the source text node
     *
     * @param parent
     * @param source
     * @return TODO
     */
    public OMText createOMText(OMContainer parent, OMText source);

    /**
     * @deprecated This method is only meaningful if it is used to create a text node that is the
     *     single child of an {@link OMElement}. However, for that purpose {@link
     *     OMElement#setText(QName)} should be used.
     */
    OMText createOMText(OMContainer parent, QName text);

    /**
     * @param parent
     * @param text
     * @param type the node type: {@link OMNode#TEXT_NODE}, {@link OMNode#CDATA_SECTION_NODE} or
     *     {@link OMNode#SPACE_NODE}
     * @return Returns OMText.
     */
    OMText createOMText(OMContainer parent, String text, int type);

    /**
     * @deprecated
     */
    OMText createOMText(OMContainer parent, char[] charArary, int type);

    /**
     * @deprecated Creating a text node containing a QName and having a type other than {@link
     *     OMNode#TEXT_NODE} is not meaningful.
     */
    OMText createOMText(OMContainer parent, QName text, int type);

    /**
     * @param s
     * @return Returns OMText.
     */
    OMText createOMText(String s);

    /**
     * @param s
     * @param type the node type: {@link OMNode#TEXT_NODE}, {@link OMNode#CDATA_SECTION_NODE} or
     *     {@link OMNode#SPACE_NODE}
     * @return Returns OMText.
     */
    OMText createOMText(String s, int type);

    /**
     * @deprecated
     */
    OMText createOMText(String s, String mimeType, boolean optimize);

    OMText createOMText(Blob blob, boolean optimize);

    /**
     * @deprecated
     */
    OMText createOMText(OMContainer parent, String s, String mimeType, boolean optimize);

    /**
     * Create a binary {@link OMText} node supporting deferred loading of the content.
     *
     * @param contentID the content ID identifying the binary content; may be <code>null</code>
     * @param blobProvider used to load the {@link Blob} when requested from the returned {@link
     *     OMText} node
     * @param optimize determines whether the binary content should be optimized
     * @return TODO
     */
    OMText createOMText(String contentID, BlobProvider blobProvider, boolean optimize);

    /**
     * Create an attribute with the given name and value. If the provided {@link OMNamespace} object
     * has a <code>null</code> prefix, then a prefix will be generated, except if the namespace URI
     * is the empty string, in which case the result is the same as if a <code>null</code> {@link
     * OMNamespace} was given.
     *
     * @param localName
     * @param ns
     * @param value
     * @return the newly created attribute
     * @throws IllegalArgumentException if an attempt is made to create a prefixed attribute with an
     *     empty namespace name or an unprefixed attribute with a namespace
     */
    OMAttribute createOMAttribute(String localName, OMNamespace ns, String value);

    /**
     * Creates DTD ({@code DOCTYPE} declaration) node.
     *
     * @param parent the parent to which the newly created text node will be added; this may be
     *     <code>null</code>
     * @param rootName the root name, i.e. the name immediately following the {@code DOCTYPE}
     *     keyword
     * @param publicId the public ID of the external subset, or <code>null</code> if there is no
     *     external subset or no public ID has been specified for the external subset
     * @param systemId the system ID of the external subset, or <code>null</code> if there is no
     *     external subset
     * @param internalSubset the internal subset, or <code>null</code> if there is none
     * @return the newly created {@link OMDocType} node
     */
    OMDocType createOMDocType(
            OMContainer parent,
            String rootName,
            String publicId,
            String systemId,
            String internalSubset);

    /**
     * Creates a PI.
     *
     * @param parent
     * @param piTarget
     * @param piData
     * @return Returns OMProcessingInstruction.
     */
    OMProcessingInstruction createOMProcessingInstruction(
            OMContainer parent, String piTarget, String piData);

    /**
     * Creates a comment.
     *
     * @param parent
     * @param content
     * @return Returns OMComment.
     */
    OMComment createOMComment(OMContainer parent, String content);

    /**
     * Create an entity reference.
     *
     * @param parent the parent to which the newly created entity reference node will be added; this
     *     may be <code>null</code>
     * @param name the name of the entity
     * @return the newly created {@link OMEntityReference} node
     */
    OMEntityReference createOMEntityReference(OMContainer parent, String name);

    /**
     * Create a copy of the given information item using this factory. This method can be used to
     * import information items created by other Axiom implementations. A copy will be created even
     * if the original information item was created by this factory. Both {@link OMSourcedElement}
     * instances and model specific elements are always copied to plain {@link OMElement} instances.
     *
     * @param informationItem the information item to copy
     * @return the imported information item
     */
    OMInformationItem importInformationItem(OMInformationItem informationItem);
}
