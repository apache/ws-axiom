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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

/**
 * Represents an unexpanded entity reference in an XML document.
 *
 * <p>Different XML APIs and object models handle entity references fairly differently:
 *
 * <ul>
 *   <li>In DOM, the way entity references in an XML document are processed depends on the setting
 *       for {@link DocumentBuilderFactory#setExpandEntityReferences(boolean)
 *       expandEntityReferences}. If this property is set to <code>true</code> (default), then the
 *       parser will expand entity references and the resulting DOM tree simply contains the nodes
 *       resulting from this expansion. If this property is set to <code>false</code>, then the
 *       parser will still expand entity references, but the resulting DOM tree will contain {@link
 *       EntityReference} nodes, the children of which represent the nodes resulting from this
 *       expansion. Note that since an entity declaration may contain markup, the children of an
 *       {@link EntityReference} node may have a type other than {@link Node#TEXT_NODE}. Application
 *       code not interested in entity references will generally set {@link
 *       DocumentBuilderFactory#setExpandEntityReferences(boolean) expandEntityReferences} to <code>
 *       true</code> in order to avoid the additional programming logic required to process {@link
 *       EntityReference} nodes.
 *   <li>In SAX, the parser will always expand entity references and report the events resulting
 *       from this expansion to the {@link ContentHandler}. In addition to that, if a {@link
 *       LexicalHandler} is registered, then the parser will report the start and end of the
 *       expansion using {@link LexicalHandler#startEntity(String)} and {@link
 *       LexicalHandler#endEntity(String)}. This means that the processing of entity references in
 *       SAX is similar to DOM with {@link DocumentBuilderFactory#setExpandEntityReferences(boolean)
 *       expandEntityReferences} set to <code>false</code>. Note that in SAX there is no
 *       corresponding configuration property. This makes sense because an application not
 *       interested in entity references can simply ignore the {@link
 *       LexicalHandler#startEntity(String)} and {@link LexicalHandler#endEntity(String)} events or
 *       not register a {@link LexicalHandler} at all.
 *   <li>In StAX, the way entity references are processed depends on the setting for the {@link
 *       XMLInputFactory#IS_REPLACING_ENTITY_REFERENCES} property. If this property is set to true
 *       (default), then the parser will expand entity references and report only the events
 *       resulting from that expansion. If the property is set to false, then the parser no longer
 *       expands entity references. Instead, it will report each entity reference using a single
 *       {@link XMLStreamConstants#ENTITY_REFERENCE} event. {@link XMLStreamReader#getText()} can
 *       then be used to get the replacement value for the entity. Note that this replacement value
 *       may contain unparsed markup. One can see that the way StAX reports entity references is
 *       significantly different than DOM or SAX.
 * </ul>
 *
 * Axiom models entity references in the same way as StAX: the node corresponding to an (unexpanded)
 * entity reference only stores the name of the entity as well as the replacement value.
 */
public interface OMEntityReference extends OMNode {
    /**
     * Get the name of the referenced entity.
     *
     * @return the name of the entity
     */
    String getName();

    /**
     * Get the replacement value for this entity reference. Note that the replacement value is a
     * simple string and may therefore contain unparsed markup.
     *
     * @return the replacement value, or <code>null</code> if the replacement value is not available
     */
    String getReplacementText();
}
