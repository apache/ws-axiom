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
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;

import org.apache.axiom.ext.stax.BlobReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;

/**
 * Captures the operations related to containment shared by both a document and an element.
 *
 * <p>Exposes the ability to add, find, and iterate over the children of a document or element.
 */
public interface OMContainer extends OMSerializable {
    /**
     * Returns the builder object.
     *
     * @return Returns the builder object used to construct the underlying XML infoset on the fly.
     */
    OMXMLParserWrapper getBuilder();

    /**
     * Adds the given node as the last child of this container.
     *
     * <p>The method may throw an {@link OMException} if the node is not allowed at this position of
     * the document.
     *
     * @param omNode the node to be added to this container
     */
    // TODO: specify whether the node is removed from its original location
    // TODO: specify what happens if the node has been created by a foreign OMFactory
    void addChild(OMNode omNode);

    /**
     * Returns an iterator for child nodes matching the given QName.
     *
     * @param elementQName The QName specifying namespace and local name to match.
     * @return Returns an iterator of {@link OMElement} items that match the given QName
     */
    // TODO: specify whether a null elementQName is allowed; LLOM and DOOM seem to have different
    // behavior
    Iterator<OMElement> getChildrenWithName(QName elementQName);

    /**
     * Returns an iterator for child nodes matching the local name.
     *
     * @param localName
     * @return Returns an iterator of {@link OMElement} items that match the given localName
     */
    Iterator<OMElement> getChildrenWithLocalName(String localName);

    /**
     * Returns an iterator for child nodes matching the namespace uri.
     *
     * @param uri
     * @return Returns an iterator of {@link OMElement} items that match the given uri
     */
    Iterator<OMElement> getChildrenWithNamespaceURI(String uri);

    /**
     * Returns the first child in document order that matches the given QName. The QName filter is
     * applied in the same way as by the {@link #getChildrenWithName(QName)} method.
     *
     * @param qname The QName to use for matching.
     * @return The first child element in document order that matches the <code>qname</code>
     *     criteria, or <code>null</code> if none is found.
     * @throws OMException If an error occurs during deferred parsing.
     * @see #getChildrenWithName(QName)
     */
    OMElement getFirstChildWithName(QName qname) throws OMException;

    /**
     * Returns an iterator for the children of the container.
     *
     * @return Returns a {@link Iterator} of children, all of which implement {@link OMNode}.
     * @see #getFirstChildWithName
     * @see #getChildrenWithName
     */
    Iterator<OMNode> getChildren();

    /**
     * Get an iterator over all descendants of the container. The items are returned in document
     * order. Note that attributes and namespace declarations are not considered descendants.
     *
     * @param includeSelf <code>true</code> if the iterator should also return the container itself;
     *     <code>false</code> if the first item returned by the iterator should be the first child
     *     of the container
     * @return an iterator over the descendants of this container
     */
    Iterator<? extends OMSerializable> getDescendants(boolean includeSelf);

    /**
     * Gets the first child.
     *
     * @return Returns the first child. May return null if the container has no children.
     */
    OMNode getFirstOMChild();

    /**
     * Remove all children from this container. This method has the same effect as the following
     * code:
     *
     * <pre>
     * for (Iterator it = container.getChildren(); it.hasNext(); ) {
     *     it.next();
     *     it.remove();
     * }</pre>
     *
     * However, the implementation may do this in an optimized way. In particular, if the node is
     * incomplete, it may choose not to instantiate child node that would become unreachable anyway.
     */
    void removeChildren();

    /**
     * Serialize the node.
     *
     * <p>This method will always serialize the infoset as plain XML. In particular, any {@link
     * OMText} containing optimized binary will be inlined using base64 encoding.
     *
     * @param output the byte stream to write the serialized infoset to
     * @param cache indicates if caching should be enabled
     * @throws IOException if the stream throws an {@link IOException}
     */
    // TODO: need to specify which charset encoding the method will use, in particular for
    // OMDocument nodes
    void serialize(OutputStream output, boolean cache) throws IOException;

    /**
     * Serialize the node.
     *
     * <p>This method will always serialize the infoset as plain XML. In particular, any {@link
     * OMText} containing optimized binary will be inlined using base64 encoding.
     *
     * @param writer the character stream to write the serialized infoset to
     * @param cache indicates if caching should be enabled
     * @throws IOException if the stream throws an {@link IOException}
     */
    void serialize(Writer writer, boolean cache) throws IOException;

    /**
     * Serialize the node.
     *
     * <p>The format of the output is controlled by the provided {@link OMOutputFormat} object. In
     * particular, {@link OMOutputFormat#setDoOptimize(boolean)} can be used to instruct this method
     * to produce an XOP/MTOM encoded MIME message.
     *
     * @param output the byte stream to write the serialized infoset to
     * @param format the output format to use
     * @param cache indicates if caching should be enabled
     * @throws IOException if the stream throws an {@link IOException}
     */
    void serialize(OutputStream output, OMOutputFormat format, boolean cache) throws IOException;

    /**
     * Serialize the node.
     *
     * @param writer the character stream to write the serialized infoset to
     * @param format the output format to use
     * @param cache indicates if caching should be enabled
     * @throws IOException if the stream throws an {@link IOException}
     */
    // TODO: need to clarify what OMOutputFormat settings are actually taken into account
    //       (obviously the method can't produce XOP/MTOM and the charset encoding is ignored)
    void serialize(Writer writer, OMOutputFormat format, boolean cache) throws IOException;

    /**
     * @deprecated Use {@link #serialize(OutputStream, boolean)} instead.
     */
    void serialize(OutputStream output) throws XMLStreamException;

    /**
     * @deprecated Use {@link #serialize(Writer, boolean)} instead.
     */
    void serialize(Writer writer) throws XMLStreamException;

    /**
     * @deprecated Use {@link #serialize(OutputStream, OMOutputFormat, boolean)} instead.
     */
    void serialize(OutputStream output, OMOutputFormat format) throws XMLStreamException;

    /**
     * @deprecated Use {@link #serialize(Writer, OMOutputFormat, boolean)} instead.
     */
    void serialize(Writer writer, OMOutputFormat format) throws XMLStreamException;

    /**
     * @deprecated Use {@link #serialize(OutputStream, boolean)} instead.
     */
    void serializeAndConsume(OutputStream output) throws XMLStreamException;

    /**
     * @deprecated Use {@link #serialize(Writer, boolean)} instead.
     */
    void serializeAndConsume(Writer writer) throws XMLStreamException;

    /**
     * @deprecated Use {@link #serialize(OutputStream, OMOutputFormat, boolean)} instead.
     */
    void serializeAndConsume(OutputStream output, OMOutputFormat format) throws XMLStreamException;

    /**
     * @deprecated Use {@link #serialize(Writer, OMOutputFormat, boolean)} instead.
     */
    void serializeAndConsume(Writer writer, OMOutputFormat format) throws XMLStreamException;

    /**
     * Get a pull parser representation of this element with caching enabled. This method has the
     * same effect as {@link #getXMLStreamReader(boolean)} with <code>cache</code> set to <code>true
     * </code>.
     *
     * @return an {@link XMLStreamReader} representation of this element
     */
    XMLStreamReader getXMLStreamReader();

    /**
     * Get a pull parser representation of this element with caching disabled. This method has the
     * same effect as {@link #getXMLStreamReader(boolean)} with <code>cache</code> set to <code>
     * false</code>.
     *
     * @return an {@link XMLStreamReader} representation of this element
     */
    XMLStreamReader getXMLStreamReaderWithoutCaching();

    /**
     * Get a pull parser representation of this information item. This methods creates an {@link
     * XMLStreamReader} instance that produces a sequence of StAX events for this container and its
     * content. The sequence of events is independent of the state of this element and the value of
     * the <code>cache</code> parameter, but the side effects of calling this method and consuming
     * the reader are different:
     *
     * <p>
     *
     * <table border="1">
     * <caption>Side effects of consuming events from the reader returned by
     * {@link #getXMLStreamReader(boolean)}</caption>
     * <tr>
     * <th>State</th>
     * <th><code>cache</code></th>
     * <th>Side effects</th>
     * </tr>
     * <tr>
     * <td rowspan="2">The element is fully built (or was created programmatically).</td>
     * <td><code>true</code></td>
     * <td rowspan="2">No side effects. The reader will synthesize StAX events from the object
     * model.</td>
     * </tr>
     * <tr>
     * <td><code>false</code></td>
     * </tr>
     * <tr>
     * <td rowspan="2">The element is partially built, i.e. deferred parsing is taking place.</td>
     * <td><code>true</code></td>
     * <td>When a StAX event is requested from the reader, it will built the information item (if
     * necessary) and synthesize the StAX event. If the caller completely consumes the reader, the
     * element will be completely built. Otherwise it will be partially built.</td>
     * </tr>
     * <tr>
     * <td><code>false</code></td>
     * <td>The reader will delegate to the underlying parser starting from the event corresponding
     * to the last information item that has been built. In other words, after synthesizing a number
     * of events, the reader will switch to delegation mode (also called "pull through" mode).
     * This will consume the part of the object model from which the reader was requested.
     * An attempt to access that part of the object model afterwards will result in a
     * {@link NodeUnavailableException}. Other parts of the object model can be accessed in a normal
     * way once the reader has been completely consumed or closed.</td>
     * </tr>
     * </table>
     *
     * <p>To free any resources associated with the returned reader, the caller MUST invoke the
     * {@link XMLStreamReader#close()} method.
     *
     * <p>The returned reader MAY implement the extension defined by {@link BlobReader} and any
     * binary content will be reported using this extension. More precisely, if the object model
     * contains an {@link OMText} instance with {@link OMText#isBinary()} returning <code>true
     * </code> (or would contain such an instance after it has been fully built), then its data will
     * always be exposed through this extension.
     *
     * <p>The caller MUST NOT make any other assumption about the returned reader, in particular
     * about its runtime type.
     *
     * @param cache indicates if caching should be enabled
     * @return an {@link XMLStreamReader} representation of this information item
     */
    XMLStreamReader getXMLStreamReader(boolean cache);

    /**
     * Get a pull parser representation of this information item. This method is similar to {@link
     * #getXMLStreamReader(boolean)}, but accepts an {@link OMXMLStreamReaderConfiguration} object
     * that allows to specify additional options and to customize the behavior of the returned
     * reader.
     *
     * @param cache indicates if caching should be enabled
     * @param configuration additional configuration options; see the Javadoc of {@link
     *     OMXMLStreamReaderConfiguration} for more information about the available options
     * @return an {@link XMLStreamReader} representation of this information item
     */
    XMLStreamReader getXMLStreamReader(boolean cache, OMXMLStreamReaderConfiguration configuration);

    /**
     * Get a {@link SAXSource} representation for this node. This method can be used to integrate
     * Axiom with APIs and third party libraries that don't support StAX. In particular it can be
     * used with the {@link Transformer} API.
     *
     * <p>The returned object supports all events defined by {@link ContentHandler} and {@link
     * LexicalHandler}. {@link DTDHandler} and {@link DeclHandler} are not supported.
     *
     * <p>If the node is an element and has a parent which is not a document, care is taken to
     * properly generate {@link ContentHandler#startPrefixMapping(String, String)} and {@link
     * ContentHandler#endPrefixMapping(String)} events also for namespace mappings declared on the
     * ancestors of the element. To understand why this is important, consider the following
     * example:
     *
     * <pre>&lt;root xmlns:ns="urn:ns"&gt;&lt;element attr="ns:someThing"/&gt;&lt;root&gt;</pre>
     *
     * <p>In that case, to correctly interpret the attribute value, the SAX content handler must be
     * aware of the namespace mapping for the {@code ns} prefix, even if the serialization starts
     * only at the child element.
     *
     * <p>No other form of namespace repairing is performed.
     *
     * @param cache Indicates if caching should be enabled. If set to <code>false</code>, the
     *     returned {@link SAXSource} may only be used once, and using it may have the side effect
     *     of consuming the original content of this node.
     * @return a {@link SAXSource} representation of this element
     */
    SAXSource getSAXSource(boolean cache);

    /**
     * Get a {@link SAXResult} object that can be used to append child nodes to this container. Note
     * that existing child nodes will not be removed. In order to replace the content of the
     * container, call {@link #removeChildren()} first.
     *
     * <p>The SAX content handler linked to the returned {@link SAXResult} supports {@link
     * ContentHandler}, {@link LexicalHandler}, {@link DeclHandler} and {@link DTDHandler}. DTD
     * related events are processed in the following way:
     *
     * <ul>
     *   <li>A {@link LexicalHandler#startDTD(String, String, String)} events will create an {@link
     *       OMDocType} if the container is an {@link OMDocument}. If the container is an {@link
     *       OMElement}, the event will be ignored silently.
     *   <li>Entity references are always replaced, i.e. no {@link OMEntityReference} objects are
     *       created for {@link LexicalHandler#startEntity(String)} events.
     * </ul>
     *
     * <p>Nodes created by the {@link ContentHandler} linked to the returned {@link SAXResult} will
     * have the same characteristics as programmatically created nodes; in particular they will have
     * no associated builder.
     *
     * @return the {@link SAXResult} object
     * @see OMXMLBuilderFactory#createOMBuilder(SAXSource, boolean)
     * @see OMXMLBuilderFactory#createOMBuilder(OMFactory, SAXSource, boolean)
     */
    SAXResult getSAXResult();

    /**
     * Get an XOP encoded pull parser representation of this information item.
     *
     * @param cache indicates if caching should be enabled
     * @return an XOP encoded representation of this information item
     */
    XOPEncoded<XMLStreamReader> getXOPEncodedStreamReader(boolean cache);
}
