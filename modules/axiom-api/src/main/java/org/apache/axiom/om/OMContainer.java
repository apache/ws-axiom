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
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;

/**
 * Captures the operations related to containment shared by both a document and an element.
 * <p/>
 * <p>Exposes the ability to add, find, and iterate over the children of a document or element.</p>
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
     * @param omNode
     *            the node to be added to this container
     */
    // TODO: specify whether the node is removed from its original location
    // TODO: specify what happens if the node has been created by a foreign OMFactory
    void addChild(OMNode omNode);

    /**
     * Returns an iterator for child nodes matching the given QName.
     * <p/>
     *
     * @param elementQName The QName specifying namespace and local name to match.
     * @return Returns an iterator of {@link OMElement} items that match the given QName
     */
    // TODO: specify whether a null elementQName is allowed; LLOM and DOOM seem to have different behavior
    Iterator getChildrenWithName(QName elementQName);
    
    /**
     * Returns an iterator for child nodes matching the local name.
     * <p/>
     *
     * @param localName 
     * @return Returns an iterator of {@link OMElement} items that match the given localName
     */
    Iterator getChildrenWithLocalName(String localName);
    
    /**
     * Returns an iterator for child nodes matching the namespace uri.
     * <p/>
     *
     * @param uri 
     * @return Returns an iterator of {@link OMElement} items that match the given uri
     */
    Iterator getChildrenWithNamespaceURI(String uri);
    

    /**
     * Returns the first child in document order that matches the given QName. The QName filter is
     * applied in the same way as by the {@link #getChildrenWithName(QName)} method.
     * 
     * @param qname
     *            The QName to use for matching.
     * @return The first child element in document order that matches the <code>qname</code>
     *         criteria, or <code>null</code> if none is found.
     * @throws OMException
     *             If an error occurs during deferred parsing.
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
    Iterator getChildren();
    
    /**
     * Get an iterator over all descendants of the container. The items are returned in document
     * order. Note that attributes and namespace declarations are not considered descendants.
     * 
     * @param includeSelf
     *            <code>true</code> if the iterator should also return the container itself;
     *            <code>false</code> if the first item returned by the iterator should be the first
     *            child of the container
     * @return an iterator over the descendants of this container
     */
    Iterator getDescendants(boolean includeSelf);

    /**
     * Gets the first child.
     *
     * @return Returns the first child.  May return null if the container has no children.
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
     * Serialize the node with caching enabled.
     * <p>
     * This method will always serialize the infoset as plain XML. In particular, any {@link OMText}
     * containing optimized binary will be inlined using base64 encoding.
     * 
     * @param output
     *            the byte stream to write the serialized infoset to
     * @throws XMLStreamException
     */
    // TODO: need to specify which charset encoding the method will use, in particular for OMDocument nodes
    void serialize(OutputStream output) throws XMLStreamException;

    /**
     * Serialize the node with caching enabled.
     * <p>
     * This method will always serialize the infoset as plain XML. In particular, any {@link OMText}
     * containing optimized binary will be inlined using base64 encoding.
     * 
     * @param writer
     *            the character stream to write the serialized infoset to
     * @throws XMLStreamException
     */
    void serialize(Writer writer) throws XMLStreamException;

    /**
     * Serialize the node with caching enabled.
     * <p>
     * The format of the output is controlled by the provided {@link OMOutputFormat} object. In
     * particular, {@link OMOutputFormat#setDoOptimize(boolean)} can be used to instruct this method
     * to produce an XOP/MTOM encoded MIME message.
     * 
     * @param output
     *            the byte stream to write the serialized infoset to
     * @param format
     *            the output format to use
     * @throws XMLStreamException
     */
    void serialize(OutputStream output, OMOutputFormat format)
            throws XMLStreamException;

    /**
     * Serialize the node with caching enabled.
     *
     * @param writer
     *            the character stream to write the serialized infoset to
     * @param format
     *            the output format to use
     * @throws XMLStreamException
     */
    void serialize(Writer writer, OMOutputFormat format)
            throws XMLStreamException;

    /**
     * Serialize the node without caching.
     * <p>
     * This method will always serialize the infoset as plain XML. In particular, any {@link OMText}
     * containing optimized binary will be inlined using base64 encoding.
     *
     * @param output
     *            the byte stream to write the serialized infoset to
     * @throws XMLStreamException
     */
    void serializeAndConsume(OutputStream output)
            throws XMLStreamException;

    /**
     * Serialize the node without caching.
     * <p>
     * This method will always serialize the infoset as plain XML. In particular, any {@link OMText}
     * containing optimized binary will be inlined using base64 encoding.
     *
     * @param writer
     *            the character stream to write the serialized infoset to
     * @throws XMLStreamException
     */
    void serializeAndConsume(Writer writer) throws XMLStreamException;

    /**
     * Serialize the node without caching.
     * <p>
     * The format of the output is controlled by the provided {@link OMOutputFormat} object. In
     * particular, {@link OMOutputFormat#setDoOptimize(boolean)} can be used to instruct this method
     * to produce an XOP/MTOM encoded MIME message.
     *
     * @param output
     *            the byte stream to write the serialized infoset to
     * @param format
     *            the output format to use
     * @throws XMLStreamException
     */
    void serializeAndConsume(OutputStream output, OMOutputFormat format)
            throws XMLStreamException;

    /**
     * Serialize the node without caching.
     *
     * @param writer
     *            the character stream to write the serialized infoset to
     * @param format
     *            the output format to use
     * @throws XMLStreamException
     */
    // TODO: need to clarify what OMOutputFormat settings are actually taken into account
    //       (obviously the method can't produce XOP/MTOM and the charset encoding is ignored)
    void serializeAndConsume(Writer writer, OMOutputFormat format)
            throws XMLStreamException;

    /**
     * Get a pull parser representation of this element with caching enabled. This method has the
     * same effect as {@link #getXMLStreamReader(boolean)} with <code>cache</code> set to
     * <code>true</code>.
     * 
     * @return an {@link XMLStreamReader} representation of this element
     */
    XMLStreamReader getXMLStreamReader();

    /**
     * Get a pull parser representation of this element with caching disabled. This method has the
     * same effect as {@link #getXMLStreamReader(boolean)} with <code>cache</code> set to
     * <code>false</code>.
     * 
     * @return an {@link XMLStreamReader} representation of this element
     */
    XMLStreamReader getXMLStreamReaderWithoutCaching();

    /**
     * Get a pull parser representation of this information item. This methods creates an
     * {@link XMLStreamReader} instance that produces a sequence of StAX events for this element and
     * its content. The sequence of events is independent of the state of this element and the value
     * of the <code>cache</code> parameter, but the side effects of calling this method and
     * consuming the reader are different:
     * <p>
     * <table border="2" rules="all" cellpadding="4" cellspacing="0">
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
     * of events, the reader will switch to delegation mode. An attempt to access the object model
     * afterwards will result in an error.</td>
     * </tr>
     * </table>
     * <p>
     * To free any resources associated with the returned reader, the caller MUST invoke the
     * {@link XMLStreamReader#close()} method.
     * <p>
     * The returned reader MAY implement the extension defined by
     * {@link org.apache.axiom.ext.stax.datahandler.DataHandlerReader} and any binary content will
     * be reported using this extension. More precisely, if the object model contains an
     * {@link OMText} instance with {@link OMText#isBinary()} returning <code>true</code> (or
     * would contain such an instance after it has been fully built), then its data will always be
     * exposed through this extension.
     * <p>
     * The caller MUST NOT make any other assumption about the returned reader, in particular about
     * its runtime type.
     * <p>
     * <b>Note</b> (non normative): For various reasons, existing code based on Axiom versions
     * prior to 1.2.9 makes assumptions on the returned reader that should no longer be considered
     * valid:
     * <ul>
     * <li>Some code assumes that the returned reader is an instance of
     * {@link org.apache.axiom.om.impl.common.OMStAXWrapper}. While it is true that Axiom internally uses
     * this class to synthesize StAX events, it may wrap this instance in another reader
     * implementation. E.g. depending on the log level, the reader will be wrapped using
     * {@link org.apache.axiom.om.util.OMXMLStreamReaderValidator}. This was already the case in
     * Axiom versions prior to 1.2.9. It should also be noted that instances of
     * {@link OMSourcedElement} (which extends the present interface) may return a reader that is
     * not implemented using {@link org.apache.axiom.om.impl.common.OMStAXWrapper}.</li>
     * <li>Some code uses the {@link OMXMLStreamReader} interface of the returned reader to switch
     * off MTOM inlining using {@link OMXMLStreamReader#setInlineMTOM(boolean)}. This has now been
     * deprecated and it is recommended to use
     * {@link org.apache.axiom.util.stax.xop.XOPEncodingStreamReader} instead.</li>
     * <li>Some existing code uses the {@link OMAttachmentAccessor} interface of the returned
     * reader to fetch attachments using {@link OMAttachmentAccessor#getDataHandler(String)}. There
     * is no reason anymore to do so:</li>
     * <ul>
     * <li>When {@link OMXMLStreamReader#setInlineMTOM(boolean)} is used to disable MTOM inlining,
     * {@link OMAttachmentAccessor#getDataHandler(String)} must be used to retrieve the binary
     * content. The fact that this method is deprecated removes the need for this.</li>
     * <li>In Axiom versions prior to 1.2.9, the sequence of events was inconsistent if the
     * underlying stream is XOP encoded and caching is disabled (see AXIOM-255). This made it
     * necessary for the caller to (partially) handle the XOP processing and to use
     * {@link OMAttachmentAccessor#getDataHandler(String)} to retrieve the binary content. Starting
     * with 1.2.9 this is no longer be the case: as specified above, the sequence of events is
     * independent of the state of the object model and the value of the <code>cache</code>
     * parameter, and all binary content is reported through the
     * {@link org.apache.axiom.ext.stax.datahandler.DataHandlerReader} extension.</li>
     * <li>Finally, it should be noted that {@link OMAttachmentAccessor#getDataHandler(String)}
     * doesn't give access to the attachments in the SwA case (neither in 1.2.9 nor in previous
     * versions).</li>
     * </ul>
     * </ul>
     * <p>
     * Code making any of these assumptions should be fixed, so that only {@link XMLStreamReader}
     * and {@link org.apache.axiom.ext.stax.datahandler.DataHandlerReader} are used (and if
     * necessary, {@link org.apache.axiom.util.stax.xop.XOPEncodingStreamReader}).
     * 
     * @param cache
     *            indicates if caching should be enabled
     * @return an {@link XMLStreamReader} representation of this information item
     */
    XMLStreamReader getXMLStreamReader(boolean cache);
    
    /**
     * Get a pull parser representation of this information item. This method is similar to
     * {@link #getXMLStreamReader(boolean)}, but accepts an {@link OMXMLStreamReaderConfiguration}
     * object that allows to specify additional options and to customize the behavior of the
     * returned reader.
     * 
     * @param cache
     *            indicates if caching should be enabled
     * @param configuration
     *            additional configuration options; see the Javadoc of
     *            {@link OMXMLStreamReaderConfiguration} for more information about the available
     *            options
     * @return an {@link XMLStreamReader} representation of this information item
     */
    XMLStreamReader getXMLStreamReader(boolean cache, OMXMLStreamReaderConfiguration configuration);
    
    /**
     * Get a {@link SAXSource} representation for this node. This method can be used to integrate
     * Axiom with APIs and third party libraries that don't support StAX. In particular it can be
     * used with the {@link Transformer} API.
     * <p>
     * The returned object supports all events defined by {@link ContentHandler} and
     * {@link LexicalHandler}. {@link DTDHandler} and {@link DeclHandler} are not supported.
     * <p>
     * If the node is an element and has a parent which is not a document, care is taken to properly
     * generate {@link ContentHandler#startPrefixMapping(String, String)} and
     * {@link ContentHandler#endPrefixMapping(String)} events also for namespace mappings declared
     * on the ancestors of the element. To understand why this is important, consider the following
     * example:
     * <pre>&lt;root xmlns:ns="urn:ns">&lt;element attr="ns:someThing"/>&lt;root></pre>
     * <p>
     * In that case, to correctly interpret the attribute value, the SAX content handler must be
     * aware of the namespace mapping for the <tt>ns</tt> prefix, even if the serialization starts
     * only at the child element.
     * 
     * @param cache
     *            Indicates if caching should be enabled. If set to <code>false</code>, the returned
     *            {@link SAXSource} may only be used once, and using it may have the side effect of
     *            consuming the original content of this node.
     * @return a {@link SAXSource} representation of this element
     */
    SAXSource getSAXSource(boolean cache);
}
