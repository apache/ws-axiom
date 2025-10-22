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
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.om.xpath.AXIOMXPath;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;

/**
 * A particular kind of node that represents an element infoset information item.
 *
 * <p>An element has a collection of children, attributes, and namespace declarations. In contrast
 * with DOM, this interface exposes namespace declarations separately from the attributes.
 *
 * <p>Namespace declarations are either added explicitly using {@link #declareNamespace(String,
 * String)}, {@link #declareDefaultNamespace(String)} or {@link #declareNamespace(OMNamespace)}, or
 * are created implicitly as side effect of other method calls:
 *
 * <ul>
 *   <li>If the element is created with a namespace and no matching namespace declaration is in
 *       scope in the location in the tree where the element is created, then an appropriate
 *       namespace declaration will be automatically added to the newly created element. The exact
 *       rules depend on the method chosen to create the element; see for example {@link
 *       OMFactory#createOMElement(QName)}.
 *   <li>If an attribute with a namespace is added, but no matching namespace declaration is in
 *       scope in the element, one is automatically added. See {@link #addAttribute(OMAttribute)}
 *       for more details.
 * </ul>
 *
 * Thus, creating a new element or adding an attribute preserves the consistency of the object model
 * with respect to namespaces. However, Axiom does not enforce namespace well-formedness for all
 * possible operations on the object model. E.g. moving an element from one location in the tree to
 * another one may cause the object model to loose its namespace well-formedness. In that case it is
 * possible that the object model contains elements or attributes with namespaces for which no
 * corresponding namespace declarations are in scope.
 *
 * <p>Fortunately, loosing namespace well-formedness has only very limited impact:
 *
 * <ul>
 *   <li>If namespace well-formedness is lost, the string to {@link QName} resolution for attribute
 *       values and element content may be inconsistent, i.e. {@link #resolveQName(String)}, {@link
 *       #getTextAsQName()} and {@link OMText#getTextAsQName()} may return incorrect results.
 *       However, it should be noted that these methods are most relevant for object model instances
 *       that have been loaded from existing documents or messages. These object models are
 *       guaranteed to be well-formed with respect to namespaces (unless they have been modified
 *       after loading).
 *   <li>During serialization, Axiom will automatically repair any namespace inconsistencies. It
 *       will add necessary namespace declarations to the output document where they are missing in
 *       the object model and generate modified namespace declarations where the original ones in
 *       the object model are inconsistent. It will also omit redundant namespace declarations.
 *       Axiom guarantees that in the output document, every element and attribute (and {@link
 *       OMText} instance with a {@link QName} value) will have the same namespace URI as in the
 *       object model, thus preserving the intended semantics of the document. On the other hand,
 *       the namespace prefixes used in the output document may differ from the ones in the object
 *       model.
 *   <li>More precisely, Axiom will always make sure that any {@link OMElement} or {@link
 *       OMAttribute} node will keep the namespace URI that has been assigned to the node at
 *       creation time, unless the namespace is explicitly changed using {@link
 *       #setNamespace(OMNamespace)} or {@link OMNamedInformationItem#setNamespace(OMNamespace,
 *       boolean)}.
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
    Iterator<OMElement> getChildElements();

    /**
     * Add a namespace declaration for the given namespace URI to this element, optionally
     * generating a prefix for that namespace.
     *
     * @param uri The namespace to declare in the current scope. The caller is expected to ensure
     *     that the URI is a valid namespace name.
     * @param prefix The prefix to associate with the given namespace. The caller is expected to
     *     ensure that this is a valid XML prefix. If <code>null</code> is given, a prefix will be
     *     auto-generated.
     * @return the created namespace information item
     * @throws IllegalArgumentException if an attempt is made to bind a prefix to the empty
     *     namespace name
     * @see #declareNamespace(OMNamespace)
     * @see #findNamespace(String, String)
     * @see #getAllDeclaredNamespaces()
     */
    OMNamespace declareNamespace(String uri, String prefix);

    /**
     * Add a namespace declaration for the default namespace to this element.
     *
     * <p>Note that this method will never change the namespace of the element itself. If an attempt
     * is made to add a namespace declaration that conflicts with the namespace information of the
     * element, an exception is thrown.
     *
     * @param uri The default namespace to declare in the current scope. The caller is expected to
     *     ensure that the URI is a valid namespace name.
     * @return the created namespace information item
     * @throws OMException if an attempt is made to add a conflicting namespace declaration
     */
    OMNamespace declareDefaultNamespace(String uri);

    /**
     * Get the default namespace in scope on this element.
     *
     * @return The default namespace or <code>null</code> if no default namespace is in scope. This
     *     method never returns an {@link OMNamespace} object with an empty namespace URI; if the
     *     element or one of its ancestors has a {@code xmlns=""} declaration, then <code>null
     *     </code> is returned. Note that if the method returns an {@link OMNamespace} object, then
     *     its prefix will obviously be the empty string.
     */
    OMNamespace getDefaultNamespace();

    /**
     * Declares a namespace with the element as its scope.
     *
     * @param namespace The namespace to declare. If the prefix specified by the {@link OMNamespace}
     *     object is <code>null</code>, then a prefix will be generated.
     * @return The declared namespace, which will be equal to the {@link OMNamespace} object passed
     *     as parameter, except if the prefix was <code>null</code>, in which case the return value
     *     contains the generated prefix.
     * @throws IllegalArgumentException if an attempt is made to bind a prefix to the empty
     *     namespace name
     * @see #declareNamespace(String, String)
     * @see #findNamespace(String, String)
     * @see #getAllDeclaredNamespaces()
     */
    OMNamespace declareNamespace(OMNamespace namespace);

    /**
     * Add a namespace declaration that undeclares a given prefix. Prefix undeclaring is supported
     * in XML 1.1, but forbidden in XML 1.0. If an object model on which this method has been used
     * is later serialized to an XML 1.0 document, an error will occur. When serialized to an XML
     * 1.1 document, a namespace declaration in the form {@code xmlns:p=""} will be produced.
     *
     * <p>A namespace declaration with empty namespace name will be added even if no existing
     * namespace declaration for the given prefix is in scope in the context of the current element.
     * If a namespace declaration for the given prefix is already defined on this element, it will
     * be replaced.
     *
     * <p>The namespace declaration created by this method will be returned by {@link
     * #getAllDeclaredNamespaces()}. It is represented as an {@link OMNamespace} object for which
     * {@link OMNamespace#getNamespaceURI()} returns an empty string.
     *
     * @param prefix the prefix to undeclare
     */
    void undeclarePrefix(String prefix);

    /**
     * Finds a namespace with the given uri and prefix, in the scope of the hierarchy.
     *
     * <p>Searches from the current element and goes up the hiararchy until a match is found. If no
     * match is found, returns {@code null}.
     *
     * <p>Either {@code prefix} or {@code uri} should be null. Results are undefined if both are
     * specified.
     *
     * @param uri The namespace to look for. If this is specified, {@code prefix} should be null.
     * @param prefix The prefix to look for. If this is specified, {@code uri} should be null.
     * @return Returns the matching namespace declaration, or {@code null} if none was found.
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
    // TODO: specify the return value if prefix is the empty string and there is no default
    // namespace
    //       (should we return null or an OMNamespace instance with namespaceURI and prefix set to
    // ""?)
    OMNamespace findNamespaceURI(String prefix);

    /**
     * Returns an iterator for all of the namespaces declared on this element. Note that this is not
     * the same as the set of namespace declarations in scope. Since building a namespace context
     * and resolving namespace prefixes has some subtleties associated with it (such as masked
     * namespace declarations and prefix undeclaring), it is generally recommended to use one of the
     * following specialized methods for this purpose:
     *
     * <ul>
     *   <li>{@link #getNamespacesInScope()} or {@link #getNamespaceContext(boolean)} to calculate
     *       the namespace context for the element.
     *   <li>{@link #findNamespace(String, String)} and {@link #findNamespaceURI(String)} to resolve
     *       a namespace prefix or to find a namespace prefix for a given URI.
     *   <li>{@link #resolveQName(String)} to resolve a QName literal.
     *   <li>{@link AXIOMXPath#AXIOMXPath(OMElement, String)} or {@link
     *       AXIOMXPath#AXIOMXPath(OMAttribute)} to create an XPath expression using the namespace
     *       context of a given element.
     * </ul>
     *
     * <p>It is expected that applications only rarely use {@link #getAllDeclaredNamespaces()}
     * directly.
     *
     * <p>The iterator returned by this method supports {@link Iterator#remove()} and that method
     * can be used to remove a namespace declaration from this element.
     *
     * @return An iterator over the {@link OMNamespace} items declared on this element. If the
     *     element has no namespace declarations, an empty iterator is returned.
     *     <p>Note that the returned iterator may be invalidated by a call to {@link
     *     #declareNamespace(OMNamespace)}, {@link #declareNamespace(String, String)}, {@link
     *     #declareDefaultNamespace(String)} or any other method that modifies the namespace
     *     declarations of this element.
     */
    Iterator<OMNamespace> getAllDeclaredNamespaces();

    /**
     * Get an iterator that returns all namespaces in scope for this element. This method may be
     * used to determine the namespace context for this element. For any given prefix, the iterator
     * returns at most one {@link OMNamespace} object with that prefix, and this object specifies
     * the namespace URI bound to the prefix. The iterator returns an {@link OMNamespace} object
     * with an empty prefix if and only if there is a default namespace. It will never return an
     * {@link OMNamespace} object with both the prefix and the namespace URI set to the empty
     * string, even if the element or one of its ancestors has a namespace declaration of the form
     * {@code xmlns=""}.
     *
     * <p>The order in which the iterator returns the namespaces is undefined, and invoking the
     * {@link Iterator#remove()} method on the returned iterator is not supported. The iterator may
     * be a "live" object, which means that results are undefined if the document is modified (in a
     * way that would modify the namespace context for the element) while the iterator is in use.
     *
     * @return an iterator over all namespaces in scope for this element
     */
    Iterator<OMNamespace> getNamespacesInScope();

    /**
     * Get the namespace context of this element, as determined by the namespace declarations
     * present on this element and its ancestors.
     *
     * <p>The method supports two different {@link NamespaceContext} implementation variants:
     *
     * <ul>
     *   <li>A "live" variant that keeps a reference to the element and that performs lookups by
     *       accessing the object model. This means that any change in the object model will
     *       automatically be reflected by the {@link NamespaceContext}.
     *   <li>A "detached" variant that stores a snapshot of the namespace context and that doesn't
     *       have any reference to the object model.
     * </ul>
     *
     * <p>Typically, creating a live {@link NamespaceContext} is cheaper, but the lookup performance
     * of a detached {@link NamespaceContext} is better. The detached variant should always be used
     * if the reference to the {@link NamespaceContext} is kept longer than the object model itself,
     * because in this case a live {@link NamespaceContext} would prevent the object model from
     * being garbage collected.
     *
     * @param detached <code>true</code> if the method should return a detached implementation,
     *     <code>false</code> if the method should return a live object
     * @return The namespace context for this element. Note that the caller must not make any
     *     assumption about the actual implementation class returned by this method.
     */
    NamespaceContext getNamespaceContext(boolean detached);

    /**
     * Returns a list of OMAttributes.
     *
     * <p>Note that the iterator returned by this function will be invalidated by any {@code
     * addAttribute} call.
     *
     * @return An iterator over the {@link OMAttribute} items associated with the element. If the
     *     element has no attributes, an empty iterator is returned.
     * @see #getAttribute
     * @see #addAttribute(OMAttribute)
     * @see #addAttribute(String, String, OMNamespace)
     */
    Iterator<OMAttribute> getAllAttributes();

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
     * @param qname the qualified name to search for
     * @return The attribute value, or <code>null</code> if no matching attribute is found.
     */
    String getAttributeValue(QName qname);

    /**
     * Adds an attribute to this element.
     *
     * <p>If the attribute already has an owner, the attribute is cloned (i.e. its name, value and
     * namespace are copied to a new attribute) and the new attribute is added to the element.
     * Otherwise the existing instance specified by the <code>attr</code> parameter is added to the
     * element. In both cases the owner of the added attribute is set to be the particular <code>
     * OMElement</code>.
     *
     * <p>If there is already an attribute with the same name and namespace URI, it will be replaced
     * and its owner set to <code>null</code>.
     *
     * <p>In the particular case where the attribute specified by <code>attr</code> is already owned
     * by the element, calling this method has no effect.
     *
     * <p>Attributes are not stored in any particular order. In particular, there is no guarantee
     * that the added attribute will be returned as the last item by the iterator produced by a
     * subsequent call to {@link #getAllAttributes()}.
     *
     * <p>If the attribute being added has a namespace, but no corresponding namespace declaration
     * is in scope for the element (i.e. declared on the element or one of its ancestors), a new
     * namespace declaration is added to the element. Note that both the namespace prefix and URI
     * are taken into account when looking for an existing namespace declaration.
     *
     * @param attr The attribute to add.
     * @return The attribute that was added to the element. As described above this may or may not
     *     be the same as <code>attr</code>, depending on whether the attribute specified by this
     *     parameter already has an owner or not.
     */
    OMAttribute addAttribute(OMAttribute attr);

    /**
     * Adds an attribute to this element.
     *
     * <p>If the element already has an attribute with the same local name and namespace URI, then
     * this existing attribute will be removed from the element, i.e. this method will always create
     * a new {@link OMAttribute} instance and never update an existing one.
     *
     * @param localName The local name for the attribute.
     * @param value The string value of the attribute. This function does not check to make sure
     *     that the given attribute value can be serialized directly as an XML value. The caller
     *     may, for example, pass a string with the character 0x01.
     * @param ns The namespace for the attribute. If no corresponding namespace declaration is in
     *     scope, then a new namespace declaration will be added to the element. The {@link
     *     OMNamespace} may have a <code>null</code> prefix, in which case the method will generate
     *     a prefix (if no namespace declaration for the given namespace URI is in scope) or use an
     *     existing one.
     * @return Returns the added attribute.
     * @throws IllegalArgumentException if an attempt is made to create a prefixed attribute with an
     *     empty namespace name or an unprefixed attribute with a namespace
     */
    OMAttribute addAttribute(String localName, String value, OMNamespace ns);

    /**
     * Removes the given attribute from this element.
     *
     * @param attr the attribute to remove
     * @throws OMException if the attribute is not owned by this element
     */
    void removeAttribute(OMAttribute attr);

    /**
     * Returns the first child element of the element.
     *
     * @return Returns the first child element of the element, or {@code null} if none was found.
     */
    OMElement getFirstElement();

    /**
     * Set the content of this element to the given text. If the element has children, then all
     * these children are detached before the content is set. If the parameter is a non empty
     * string, then the element will have a single child of type {@link OMText} after the method
     * returns. If the parameter is <code>null</code> or an empty string, then the element will have
     * no children.
     *
     * @param text the new text content for the element
     */
    void setText(String text);

    /**
     * Set the content of this element to the given {@link QName}. If no matching namespace
     * declaration for the {@link QName} is in scope, then this method will add one. If the {@link
     * QName} specifies a namespace URI but no prefix, then a prefix will be generated. If the
     * element has children, then all these children are detached before the content is set. If the
     * parameter is not <code>null</code>, then the element will have a single child of type {@link
     * OMText} after the method returns. If the parameter is <code>null</code>, then the element
     * will have no children.
     *
     * @param qname the QName value
     */
    void setText(QName qname);

    /**
     * Returns the non-empty text children as a string.
     *
     * <p>This method iterates over all the text children of the element and concatenates them to a
     * single string. Only direct children will be considered, i.e. the text is not extracted
     * recursively. For example the return value for {@code <element>A<child>B</child>C</element>}
     * will be {@code AC}.
     *
     * <p>All whitespace will be preserved.
     *
     * @return A string representing the concatenation of the child text nodes. If there are no
     *     child text nodes, an empty string is returned.
     */
    String getText();

    /**
     * Returns a stream representing the concatenation of the text nodes that are children of a this
     * element. The stream returned by this method produces exactly the same character sequence as
     * the the stream created by the following expression:
     *
     * <pre>new StringReader(element.getText())</pre>
     *
     * <p>The difference is that the stream implementation returned by this method is optimized for
     * performance and is guaranteed to have constant memory usage, provided that:
     *
     * <ol>
     *   <li>The method is not required to cache the content of the {@link OMElement}, i.e. <code>
     *       cache</code> is <code>false</code> or the element is an {@link OMSourcedElement} that
     *       is backed by a non destructive {@link OMDataSource}.
     *   <li>The underlying parser (or the implementation of the underlying {@link XMLStreamReader}
     *       in the case of an {@link OMSourcedElement}) is non coalescing. Note that this is not
     *       the default in Axiom and it may be necessary to configure the parser with {@link
     *       StAXParserConfiguration#NON_COALESCING}.
     * </ol>
     *
     * <p>When this method is used with {@code cache} set to {@code false} the caller must close the
     * returned stream before attempting to access other nodes in the tree.
     *
     * @param cache whether to enable caching when accessing the element
     * @return a stream representing the concatenation of the text nodes
     * @see #getText()
     */
    Reader getTextAsStream(boolean cache);

    /**
     * Write the content of the text nodes that are children of a given element to a {@link Writer}.
     * If <code>cache</code> is true, this method has the same effect as the following instruction:
     *
     * <pre>out.write(element.getText())</pre>
     *
     * <p>The difference is that this method is guaranteed to have constant memory usage and is
     * optimized for performance (with the same restrictions that apply to {@link
     * #getTextAsStream(boolean)}).
     *
     * <p>The method does <b>not</b> call {@link Writer#close()}.
     *
     * @param out the stream to write the content to
     * @param cache whether to enable caching when accessing the element
     * @throws OMException if an error occurs when reading from the element
     * @throws IOException if an error occurs when writing to the stream
     * @see #getText()
     */
    void writeTextTo(Writer out, boolean cache) throws IOException;

    /**
     * Resolve the content of this element to a {@link QName}. The QName is interpreted in a way
     * that is compatible with the XML schema specification. In particular, surrounding whitespace
     * is ignored.
     *
     * @return the resolved QName, or <code>null</code> if the element is empty or the QName could
     *     not be resolved
     */
    QName getTextAsQName();

    /**
     * Set the namespace for this element. In addition to changing the namespace URI and prefix of
     * the element information item, this method ensures that a corresponding namespace declaration
     * exists. If no corresponding namespace declaration is already in scope, then a new one will be
     * added to this element.
     *
     * <p>This method has the same effect as {@link OMNamedInformationItem#setNamespace(OMNamespace,
     * boolean)} with <code>declare</code> set to <code>true</code>.
     *
     * @param namespace The new namespace for this element, or <code>null</code> to remove the
     *     namespace from this element. If an {@link OMNamespace} instance with a <code>null</code>
     *     prefix is given, then a prefix will be generated automatically. In this case, the
     *     generated prefix can be determined using {@link #getNamespace()}.
     * @throws IllegalArgumentException if an attempt is made to bind a prefix to the empty
     *     namespace name
     */
    void setNamespace(OMNamespace namespace);

    /**
     * @deprecated Use {@link OMNamedInformationItem#setNamespace(OMNamespace, boolean)} with <code>
     *     declare</code> set to <code>false</code>.
     */
    void setNamespaceWithNoFindInCurrentScope(OMNamespace namespace);

    /**
     * Convenience method to serialize the element to a string with caching enabled. Caching means
     * that the object model tree for the element will be fully built in memory and can be accessed
     * after invoking this method.
     *
     * <p>This method produces the same result as {@link OMContainer#serialize(Writer)}. In
     * particular, the element is always serialized as plain XML and {@link OMText} nodes containing
     * optimized binary data are always inlined using base64 encoding. Since the output is
     * accumulated into a single string object, this may result in high memory consumption.
     * Therefore this method should be used with care.
     *
     * @return the serialized object model
     */
    @Override
    String toString();

    /**
     * Convenience method to serialize the element to a string without caching. This method will not
     * built the object model tree in memory. This means that an attempt to access the object model
     * after invoking this method may result in an error (unless the object model was already fully
     * built before, e.g. because it was created programmatically).
     *
     * <p>As for {@link #toString()}, this method may cause high memory consumption for object model
     * trees containing optimized binary data and should therefore be used with care.
     *
     * @return the serialized object model
     */
    String toStringWithConsume() throws XMLStreamException;

    /**
     * Resolves a QName literal in the namespace context defined by this element and produces a
     * corresponding {@link QName} object. The implementation uses the algorithm defined by the XML
     * Schema specification. In particular, the namespace for an unprefixed QName is the default
     * namespace (not the null namespace), i.e. QNames are resolved in the same way as element
     * names.
     *
     * @param qname the QName literal to resolve
     * @return the {@link QName} object, or <code>null</code> if the QName can't be resolved, i.e.
     *     if the prefix is not bound in the namespace context of this element
     */
    QName resolveQName(String qname);

    /**
     * Clones this element and its descendants using default options. This method has the same
     * effect as {@link #clone(OMCloneOptions)} with default options.
     *
     * @return the cloned element
     */
    OMElement cloneOMElement();

    /**
     * @deprecated
     */
    void setLineNumber(int lineNumber);

    /**
     * @deprecated
     */
    int getLineNumber();

    /** {@inheritDoc} */
    @Override
    Iterator<OMNode> getDescendants(boolean includeSelf);
}
