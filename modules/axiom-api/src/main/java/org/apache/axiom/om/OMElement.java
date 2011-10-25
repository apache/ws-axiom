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

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.xpath.AXIOMXPath;

import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;

/**
 * A particular kind of node that represents an element infoset information item.
 * <p>
 * An element has a collection of children, attributes, and namespace declarations. In contrast with
 * DOM, this interface exposes namespace declarations separately from the attributes.
 * <p>
 * Namespace declarations are either added explicitly using
 * {@link #declareNamespace(String, String)}, {@link #declareDefaultNamespace(String)} or
 * {@link #declareNamespace(OMNamespace)}, or are created implicitly as side effect of other method
 * calls:
 * <ul>
 * <li>If the element is created with a namespace and no matching namespace declaration is in scope
 * in the location in the tree where the element is created, then an appropriate namespace
 * declaration will be automatically added to the newly created element. The exact rules depend on
 * the method chosen to create the element; see for example {@link OMFactory#createOMElement(QName)}.
 * <li>If an attribute with a namespace is added, but no matching namespace declaration is in scope
 * in the element, one is automatically added. See {@link #addAttribute(OMAttribute)} for more
 * details.
 * </ul>
 * Thus, creating a new element or adding an attribute preserves the consistency of the object model
 * with respect to namespaces. However, Axiom does not enforce namespace well-formedness for all
 * possible operations on the object model. E.g. moving an element from one location in the tree to
 * another one may cause the object model to loose its namespace well-formedness. In that case it is
 * possible that the object model contains elements or attributes with namespaces for which no
 * corresponding namespace declarations are in scope.
 * <p>
 * Fortunately, loosing namespace well-formedness has only very limited impact:
 * <ul>
 * <li>If namespace well-formedness is lost, the string to {@link QName} resolution for attribute
 * values and element content may be inconsistent, i.e. {@link #resolveQName(String)},
 * {@link #getTextAsQName()} and {@link OMText#getTextAsQName()} may return incorrect results.
 * However, it should be noted that these methods are most relevant for object model instances that
 * have been loaded from existing documents or messages. These object models are guaranteed to be
 * well-formed with respect to namespaces (unless they have been modified after loading).
 * <li>During serialization, Axiom will automatically repair any namespace inconsistencies. It will
 * add necessary namespace declarations to the output document where they are missing in the object
 * model and generate modified namespace declarations where the original ones in the object model
 * are inconsistent. It will also omit redundant namespace declarations. Axiom guarantees that in
 * the output document, every element and attribute (and {@link OMText} instance with a
 * {@link QName} value) will have the same namespace URI as in the object model, thus preserving the
 * intended semantics of the document. On the other hand, the namespace prefixes used in the output
 * document may differ from the ones in the object model.
 * <li>More precisely, Axiom will always make sure that any {@link OMElement} or {@link OMAttribute}
 * node will keep the namespace URI that has been assigned to the node at creation time, unless the
 * namespace is explicitly changed using {@link #setNamespace(OMNamespace)} or
 * {@link OMAttribute#setOMNamespace(OMNamespace)}. [TODO: this is currently not entirely true; see
 * WSCOMMONS-517]
 * </ul>
 */
public interface OMElement extends OMNode, OMContainer, OMNamedInformationItem {

    /**
     * Returns a filtered list of children - just the elements.
     *
     * @return Returns an iterator over the child elements.
     * @see #getChildren()
     * @see #getChildrenWithName(javax.xml.namespace.QName)
     */
    Iterator getChildElements();
    
    /**
     * Creates a namespace in the current element scope.
     *
     * @param uri    The namespace to declare in the current scope.  The caller is expected to
     *               ensure that the URI is a valid namespace name.
     * @param prefix The prefix to associate with the given namespace. The caller is expected to
     *               ensure that this is a valid XML prefix. If <code>null</code> or the empty
     *               string is given, a prefix will be auto-generated. <b>Please note that using the
     *               empty string for this purpose is deprecated and will no longer be supported in
     *               Axiom 1.3.</b>
     * @return Returns the created namespace information item.
     * @throws IllegalArgumentException
     *             if an attempt is made to bind a prefix to the empty namespace name
     * @see #declareNamespace(OMNamespace)
     * @see #findNamespace(String, String)
     * @see #getAllDeclaredNamespaces()
     */
    OMNamespace declareNamespace(String uri, String prefix);


    /**
     * This will declare a default namespace for this element explicitly
     *
     * @param uri
     */
    OMNamespace declareDefaultNamespace(String uri);

    /**
     * This will retrieve the default namespace of this element, if available. null returned if none
     * is found.
     */
    OMNamespace getDefaultNamespace();

    /**
     * Declares a namespace with the element as its scope.
     * 
     * @param namespace
     *            The namespace to declare. If the prefix specified by the {@link OMNamespace}
     *            object is <code>null</code>, then a prefix will be generated.
     * @return The declared namespace, which will be equal to the {@link OMNamespace} object passed
     *         as parameter, except if the prefix was <code>null</code>, in which case the return
     *         value contains the generated prefix.
     * @throws IllegalArgumentException
     *             if an attempt is made to bind a prefix to the empty namespace name
     * @see #declareNamespace(String, String)
     * @see #findNamespace(String, String)
     * @see #getAllDeclaredNamespaces()
     */
    OMNamespace declareNamespace(OMNamespace namespace);

    /**
     * Add a namespace declaration that undeclares a given prefix. Prefix undeclaring is supported
     * in XML 1.1, but forbidden in XML 1.0. If an object model on which this method has been used
     * is later serialized to an XML 1.0 document, an error will occur. When serialized to an XML
     * 1.1 document, a namespace declaration in the form <tt>xmlns:p=""</tt> will be produced.
     * <p>
     * A namespace declaration with empty namespace name will be added even if no existing namespace
     * declaration for the given prefix is in scope in the context of the current element. If a
     * namespace declaration for the given prefix is already defined on this element, it will be
     * replaced.
     * <p>
     * The namespace declaration created by this method will be returned by
     * {@link #getAllDeclaredNamespaces()}. It is represented as an {@link OMNamespace} object for
     * which {@link OMNamespace#getNamespaceURI()} returns an empty string.
     * 
     * @param prefix
     *            the prefix to undeclare
     */
    void undeclarePrefix(String prefix);
    
    /**
     * Finds a namespace with the given uri and prefix, in the scope of the hierarchy.
     * <p/>
     * <p>Searches from the current element and goes up the hiararchy until a match is found. If no
     * match is found, returns <tt>null</tt>.</p>
     * <p/>
     * <p>Either <tt>prefix</tt> or <tt>uri</tt> should be null.  Results are undefined if both are
     * specified.</p>
     *
     * @param uri    The namespace to look for.  If this is specified, <tt>prefix</tt> should be
     *               null.
     * @param prefix The prefix to look for.  If this is specified, <tt>uri</tt> should be null.
     * @return Returns the matching namespace declaration, or <tt>null</tt> if none was found.
     * @see #declareNamespace(String, String)
     * @see #declareNamespace(OMNamespace)
     * @see #getAllDeclaredNamespaces()
     */
    OMNamespace findNamespace(String uri, String prefix);

    /**
     * Checks for a namespace in the context of this element with the given prefix and returns the
     * relevant namespace object, if available. If not available, returns null.
     *
     * @param prefix
     */
    // TODO: specify if null is a valid argument
    // TODO: specify the return value if prefix is the empty string and there is no default namespace
    //       (should we return null or an OMNamespace instance with namespaceURI and prefix set to ""?)
    OMNamespace findNamespaceURI(String prefix);

    /**
     * Returns an iterator for all of the namespaces declared on this element. Note that this is not
     * the same as the set of namespace declarations in scope. Since building a namespace context
     * and resolving namespace prefixes has some subtleties associated with it (such as masked
     * namespace declarations and prefix undeclaring), it is generally recommended to use one of the
     * following specialized methods for this purpose:
     * <ul>
     * <li>{@link #getNamespacesInScope()} to calculate the namespace context for the element.
     * <li>{@link #findNamespace(String, String)} and {@link #findNamespaceURI(String)} to resolve a
     * namespace prefix or to find a namespace prefix for a given URI.
     * <li>{@link #resolveQName(String)} to resolve a QName literal.
     * <li>{@link AXIOMXPath#AXIOMXPath(OMElement, String)} or
     * {@link AXIOMXPath#AXIOMXPath(OMAttribute)} to create an XPath expression using the namespace
     * context of a given element.
     * </ul>
     * <p>
     * It is expected that applications only rarely use {@link #getAllDeclaredNamespaces()}
     * directly.
     * <p>
     * The iterator returned by this method supports {@link Iterator#remove()} and that method can
     * be used to remove a namespace declaration from this element.
     * 
     * @return An iterator over the {@link OMNamespace} items declared on this element. Note that
     *         the iterator may be invalidated by a call to {@link #declareNamespace(OMNamespace)},
     *         {@link #declareNamespace(String, String)}, {@link #declareDefaultNamespace(String)}
     *         or any other method that modifies the namespace declarations of this element.
     */
    Iterator getAllDeclaredNamespaces() throws OMException;

    /**
     * Get an iterator that returns all namespaces in scope for this element. This method may be
     * used to determine the namespace context for this element. For any given prefix, the iterator
     * returns at most one {@link OMNamespace} object with that prefix, and this object specifies
     * the namespace URI bound to the prefix. The iterator returns an {@link OMNamespace} object
     * with an empty prefix if and only if there is a default namespace. It will never return an
     * {@link OMNamespace} object with both the prefix and the namespace URI set to the empty
     * string, even if the element or one of its ancestors has a namespace declaration of the form
     * <tt>xmlns=""</tt>.
     * <p>
     * The order in which the iterator returns the namespaces is undefined, and invoking the
     * {@link Iterator#remove()} method on the returned iterator is not supported. The iterator may
     * be a "live" object, which means that results are undefined if the document is modified (in a
     * way that would modify the namespace context for the element) while the iterator is in use.
     * 
     * @return an iterator over all namespaces in scope for this element
     */
    Iterator getNamespacesInScope();
    
    /**
     * Get the namespace context of this element, as determined by the namespace declarations
     * present on this element and its ancestors.
     * <p>
     * The method supports two different {@link NamespaceContext} implementation variants:
     * <ul>
     * <li>A "live" variant that keeps a reference to the element and that performs lookups by
     * accessing the object model. This means that any change in the object model will automatically
     * be reflected by the {@link NamespaceContext}.
     * <li>A "detached" variant that stores a snapshot of the namespace context and that doesn't
     * have any reference to the object model.
     * </ul>
     * <p>
     * Typically, creating a live {@link NamespaceContext} is cheaper, but the lookup performance of
     * a detached {@link NamespaceContext} is better. The detached variant should always be used if
     * the reference to the {@link NamespaceContext} is kept longer than the object model itself,
     * because in this case a live {@link NamespaceContext} would prevent the object model from
     * being garbage collected.
     * 
     * @param detached
     *            <code>true</code> if the method should return a detached implementation,
     *            <code>false</code> if the method should return a live object
     * @return The namespace context for this element. Note that the caller must not make any
     *         assumption about the actual implementation class returned by this method.
     */
    NamespaceContext getNamespaceContext(boolean detached);
    
    /**
     * Returns a list of OMAttributes.
     * <p/>
     * <p>Note that the iterator returned by this function will be invalidated by any
     * <tt>addAttribute</tt> call. </p>
     *
     * @return Returns an {@link Iterator} of {@link OMAttribute} items associated with the
     *         element.
     * @see #getAttribute
     * @see #addAttribute(OMAttribute)
     * @see #addAttribute(String, String, OMNamespace)
     */
    Iterator getAllAttributes();

    /**
     * Returns a named attribute if present.
     *
     * @param qname the qualified name to search for
     * @return Returns an OMAttribute with the given name if found, or null
     */
    OMAttribute getAttribute(QName qname);

    /**
     * Returns a named attribute's value, if present.
     * 
     * @param qname
     *            the qualified name to search for
     * @return The attribute value, or <code>null</code> if no matching attribute is found.
     */
    String getAttributeValue(QName qname);

    /**
     * Adds an attribute to this element.
     * <p>
     * If the attribute already has an owner, the attribute is cloned (i.e. its name, value and
     * namespace are copied to a new attribute) and the new attribute is added to the element.
     * Otherwise the existing instance specified by the <code>attr</code> parameter is added to
     * the element. In both cases the owner of the added attribute is set to be the particular
     * <code>OMElement</code>.
     * <p>
     * If there is already an attribute with the same name and namespace URI, it will be replaced
     * and its owner set to <code>null</code>.
     * <p>
     * In the particular case where the attribute specified by <code>attr</code> is already owned
     * by the element, calling this method has no effect. 
     * <p>
     * Attributes are not stored in any particular order. In particular, there is no guarantee
     * that the added attribute will be returned as the last item by the iterator produced by
     * a subsequent call to {@link #getAllAttributes()}.
     * <p>
     * If the attribute being added has a namespace, but no corresponding namespace declaration is
     * in scope for the element (i.e. declared on the element or one of its ancestors), a new
     * namespace declaration is added to the element. Note that both the namespace prefix and URI
     * are taken into account when looking for an existing namespace declaration.
     *
     * @param attr The attribute to add.
     * @return The attribute that was added to the element. As described above this may or may
     *         not be the same as <code>attr</code>, depending on whether the attribute specified
     *         by this parameter already has an owner or not.  
     */
    OMAttribute addAttribute(OMAttribute attr);

    /**
     * Adds an attribute to this element.
     * <p>
     * If the element already has an attribute with the same local name and namespace URI, then this
     * existing attribute will be removed from the element, i.e. this method will always create a
     * new {@link OMAttribute} instance and never update an existing one.
     * 
     * @param localName
     *            The local name for the attribute.
     * @param value
     *            The string value of the attribute. This function does not check to make sure that
     *            the given attribute value can be serialized directly as an XML value. The caller
     *            may, for example, pass a string with the character 0x01.
     * @param ns
     *            The namespace has to be one of the in scope namespace. i.e. the passed namespace
     *            must be declared in the parent element of this attribute or ancestors of the
     *            parent element of the attribute.
     * @return Returns the added attribute.
     * @throws IllegalArgumentException
     *             if an attempt is made to create a prefixed attribute with an empty namespace name
     */
    OMAttribute addAttribute(String localName, String value, OMNamespace ns);

    /**
     * Method removeAttribute
     *
     * @param attr
     */
    void removeAttribute(OMAttribute attr);

    /**
     * Method setBuilder.
     *
     * @param wrapper
     */
    void setBuilder(OMXMLParserWrapper wrapper);

    /**
     * Returns the first child element of the element.
     *
     * @return Returns the first child element of the element, or <tt>null</tt> if none was found.
     */

    OMElement getFirstElement();


    /** @param text  */
    void setText(String text);

    /**
     * Set the content of this element to the given {@link QName}. If no matching namespace
     * declaration for the {@link QName} is in scope, then this method will add one. If the
     * {@link QName} specifies a namespace URI but no prefix, then a prefix will be generated.
     * 
     * @param qname
     *            the QName value
     */
    void setText(QName qname);

    /**
     * Returns the non-empty text children as a string.
     * <p>
     * This method iterates over all the text children of the element and concatenates
     * them to a single string. Only direct children will be considered, i.e. the text
     * is not extracted recursively. For example the return value for
     * <tt>&lt;element>A&lt;child>B&lt;/child>C&lt;/element></tt> will be <tt>AC</tt>.
     * <p>
     * All whitespace will be preserved.
     *
     * @return A string representing the concatenation of the child text nodes.
     *         If there are no child text nodes, an empty string is returned.
     */
    String getText();

    /** OMText can contain its information as a QName as well. This will return the text as a QName */
    QName getTextAsQName();
    
    /**
     * Set the namespace for this element. In addition to changing the namespace URI and prefix of
     * the element information item, this method ensures that a corresponding namespace declaration
     * exists. If no corresponding namespace declaration is already in scope, then a new one will be
     * added to this element.
     * 
     * @param namespace
     *            The new namespace for this element, or <code>null</code> to remove the namespace
     *            from this element. If an {@link OMNamespace} instance with a <code>null</code>
     *            prefix is given, then a prefix will be generated automatically. In this case, the
     *            generated prefix can be determined using {@link #getNamespace()}.
     * @throws IllegalArgumentException
     *             if an attempt is made to bind a prefix to the empty namespace name
     */
    void setNamespace(OMNamespace namespace);

    /**
     * This will not search the namespace in the scope nor will declare in the current element, as
     * in setNamespace(OMNamespace). This will just assign the given namespace to the element.
     *
     * @param namespace
     */
    void setNamespaceWithNoFindInCurrentScope(OMNamespace namespace);

    /**
     * This is a convenience method only. This will basically serialize the given OMElement to a
     * String but will build the OMTree in the memory
     */
    String toString();

    /**
     * This is a convenience method only. This basically serializes the given OMElement to a String
     * but will NOT build the OMTree in the memory. So you are at your own risk of losing
     * information.
     */
    String toStringWithConsume() throws XMLStreamException;


    /**
     * Resolves a QName literal in the namespace context defined by this element and produces a
     * corresponding {@link QName} object. The implementation uses the algorithm defined by the XML
     * Schema specification. In particular, the namespace for an unprefixed QName is the default
     * namespace (not the null namespace), i.e. QNames are resolved in the same way as element
     * names.
     * 
     * @param qname
     *            the QName literal to resolve
     * @return the {@link QName} object, or <code>null</code> if the QName can't be resolved, i.e.
     *         if the prefix is not bound in the namespace context of this element
     */
    QName resolveQName(String qname);

    /**
     * Clones this element. Since both elements are build compleletely, you will lose the differed
     * building capability.
     *
     * @return Returns OMElement.
     */
    OMElement cloneOMElement();

    void setLineNumber(int lineNumber);

    int getLineNumber();

    /**
     * Serializes the node with caching.
     *
     * @param output
     * @throws XMLStreamException
     */
    // Note: This method is inherited from both OMNode and OMContainer, but it is deprecated in
    //       OMNode. We redeclare it here to make sure that people don't get a deprecation
    //       warning when using the method on an OMElement.
    void serialize(OutputStream output) throws XMLStreamException;

    /**
     * Serializes the node with caching.
     *
     * @param writer
     * @throws XMLStreamException
     */
    // Note: This method is inherited from both OMNode and OMContainer, but it is deprecated in
    //       OMNode. We redeclare it here to make sure that people don't get a deprecation
    //       warning when using the method on an OMElement.
    void serialize(Writer writer) throws XMLStreamException;

    /**
     * Serializes the node with caching.
     *
     * @param output
     * @param format
     * @throws XMLStreamException
     */
    // Note: This method is inherited from both OMNode and OMContainer, but it is deprecated in
    //       OMNode. We redeclare it here to make sure that people don't get a deprecation
    //       warning when using the method on an OMElement.
    void serialize(OutputStream output, OMOutputFormat format)
            throws XMLStreamException;

    /**
     * Serializes the node with caching.
     *
     * @param writer
     * @param format
     * @throws XMLStreamException
     */
    // Note: This method is inherited from both OMNode and OMContainer, but it is deprecated in
    //       OMNode. We redeclare it here to make sure that people don't get a deprecation
    //       warning when using the method on an OMElement.
    void serialize(Writer writer, OMOutputFormat format)
            throws XMLStreamException;

    /**
     * Serializes the node without caching.
     *
     * @param output
     * @throws XMLStreamException
     */
    // Note: This method is inherited from both OMNode and OMContainer, but it is deprecated in
    //       OMNode. We redeclare it here to make sure that people don't get a deprecation
    //       warning when using the method on an OMElement.
    void serializeAndConsume(OutputStream output)
            throws XMLStreamException;

    /**
     * Serializes the node without caching.
     *
     * @param writer
     * @throws XMLStreamException
     */
    // Note: This method is inherited from both OMNode and OMContainer, but it is deprecated in
    //       OMNode. We redeclare it here to make sure that people don't get a deprecation
    //       warning when using the method on an OMElement.
    void serializeAndConsume(Writer writer) throws XMLStreamException;

    /**
     * Serializes the node without caching.
     *
     * @param output
     * @param format
     * @throws XMLStreamException
     */
    // Note: This method is inherited from both OMNode and OMContainer, but it is deprecated in
    //       OMNode. We redeclare it here to make sure that people don't get a deprecation
    //       warning when using the method on an OMElement.
    void serializeAndConsume(OutputStream output, OMOutputFormat format)
            throws XMLStreamException;

    /**
     * Serializes the node without caching.
     *
     * @param writer
     * @param format
     * @throws XMLStreamException
     */
    // Note: This method is inherited from both OMNode and OMContainer, but it is deprecated in
    //       OMNode. We redeclare it here to make sure that people don't get a deprecation
    //       warning when using the method on an OMElement.
    void serializeAndConsume(Writer writer, OMOutputFormat format)
            throws XMLStreamException;
    
    /**
     * Get a pull parser representation of this element, optionally preserving the namespace context
     * determined by the ancestors of the element. This method is similar to
     * {@link OMContainer#getXMLStreamReader(boolean)} but supports an additional feature that
     * allows to strictly preserve the namespace context. When this feature is enabled, the
     * {@link XMLStreamReader#getNamespaceCount()}, {@link XMLStreamReader#getNamespacePrefix(int)}
     * and {@link XMLStreamReader#getNamespaceURI(int)} will report additional namespace
     * declarations for the {@link XMLStreamConstants#START_ELEMENT} event corresponding to the
     * element on which this method is called, i.e. the root element of the resulting stream. These
     * namespace declarations correspond to namespaces declared by the ancestors of the element and
     * that are visible in the context of the element.
     * <p>
     * More precisely, if this feature is enabled, then the namespace declarations reported for the
     * first {@link XMLStreamConstants#START_ELEMENT} event in the returned stream will be the same
     * as the declarations that would be returned by {@link #getNamespacesInScope()}, with the
     * exception that a <tt>xmlns=""</tt> declaration present on the element will be preserved.
     * <p>
     * This feature is useful for code that relies on the namespace declarations reported by the
     * {@link XMLStreamReader} to reconstruct the namespace context (instead of using the namespace
     * context provided by {@link XMLStreamReader#getNamespaceContext()}). An example helps to
     * illustrate how this works. Consider the following XML message:
     * 
     * <pre>
     * &lt;soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
     *                   xmlns:xsd="http://www.w3.org/2001/XMLSchema"
     *                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
     *   &lt;soapenv:Body>
     *     &lt;ns:echo xmlns:ns="urn:test">
     *       &lt;in xsi:type="xsd:string">test&lt;/in>
     *     &lt;/ns:echo>
     *   &lt;/soapenv:Body>
     * &lt;/soapenv:Envelope>
     * </pre>
     * <p>
     * When {@link OMContainer#getXMLStreamReader(boolean)} is invoked on the {@link OMElement}
     * corresponding to <tt>ns:echo</tt>, only the namespace declaration for the <tt>ns</tt> prefix
     * will be reported. This may cause a problem when the caller attempts to resolve the QName
     * value <tt>xsd:string</tt> of the <tt>xsi:type</tt> attribute. If namespace context
     * preservation is enabled, then the {@link XMLStreamReader} returned by this method will
     * generate additional namespace declarations for the <tt>soapenv</tt>, <tt>xsd</tt> and
     * <tt>xsi</tt> prefixes. They are reported for the {@link XMLStreamConstants#START_ELEMENT}
     * event representing the <tt>ns:echo</tt> element.
     * 
     * @param cache
     *            indicates if caching should be enabled
     * @param preserveNamespaceContext
     *            indicates if additional namespace declarations should be generated to preserve the
     *            namespace context of the element
     * @return an {@link XMLStreamReader} representation of this element
     */
    XMLStreamReader getXMLStreamReader(boolean cache, boolean preserveNamespaceContext);
}
