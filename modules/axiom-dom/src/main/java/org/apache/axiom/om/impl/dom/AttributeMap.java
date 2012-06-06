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

package org.apache.axiom.om.impl.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Vector;

/** Most of the implementation is taken from org.apache.xerces.dom.NamedNodeMapImpl */
public class AttributeMap implements NamedNodeMap {
    private Vector nodes;

    private ElementImpl ownerNode;

    AttributeMap(ElementImpl ownerNode) {
        this.ownerNode = ownerNode;
    }


    /**
     * 
     */
    public Node getNamedItem(String name) {
        int i = findNamePoint(name, 0);
        return (i < 0) ? null : (Node) (nodes.elementAt(i));

    }

    /** From org.apache.xerces.dom.NamedNodeMapImpl */
    public Node item(int index) {
        return (nodes != null && index < nodes.size()) ? (Node) (nodes
                .elementAt(index)) : null;
    }

    /** From org.apache.xerces.dom.NamedNodeMapImpl */
    public int getLength() {
        return (nodes != null) ? nodes.size() : 0;
    }

    /**
     * Introduced in DOM Level 2. Retrieves a node specified by local name and namespace URI.
     *
     * @param namespaceURI The namespace URI of the node to retrieve. When it is null or an empty
     *                     string, this method behaves like getNamedItem.
     * @param localName    The local name of the node to retrieve.
     * @return Returns s Node (of any type) with the specified name, or null if the specified name
     *         did not identify any node in the map.
     */
    public Node getNamedItemNS(String namespaceURI, String localName) {

        int i = findNamePoint(namespaceURI, localName);
        return (i < 0) ? null : (Node) (nodes.elementAt(i));

    } // getNamedItemNS(String,String):Node

    public Node removeNamedItem(String name) throws DOMException {
        // TODO Set used to false

        int i = findNamePoint(name, 0);
        if (i < 0) {
            String msg = DOMMessageFormatter.formatMessage(
                    DOMMessageFormatter.DOM_DOMAIN, DOMException.NOT_FOUND_ERR, null);
            throw new DOMException(DOMException.NOT_FOUND_ERR, msg);
        }

        NodeImpl n = (NodeImpl) nodes.elementAt(i);
        nodes.removeElementAt(i);

        return n;
    }

    public Node removeNamedItemNS(String namespaceURI, String name)
            throws DOMException {

        int i = findNamePoint(namespaceURI, name);
        if (i < 0) {
            String msg = DOMMessageFormatter.formatMessage(
                    DOMMessageFormatter.DOM_DOMAIN, DOMException.NOT_FOUND_ERR, null);
            throw new DOMException(DOMException.NOT_FOUND_ERR, msg);
        }

        NodeImpl n = (NodeImpl) nodes.elementAt(i);
        nodes.removeElementAt(i);

        return n;
    }

    /** Almost a copy of the Xerces impl. */
    public Node setNamedItem(Node attribute) throws DOMException {

        ownerNode.checkSameOwnerDocument(attribute);
        if (attribute.getNodeType() != Node.ATTRIBUTE_NODE) {
            String msg = DOMMessageFormatter.formatMessage(
                    DOMMessageFormatter.DOM_DOMAIN,
                    DOMException.HIERARCHY_REQUEST_ERR,
                    null);
            throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, msg);
        }

        AttrImpl attr = (AttrImpl) attribute;
        if (attr.getOwnerElement() != null) { // If the attribute is owned then:
            if (attr.getOwnerElement() != this.ownerNode) // the owner must be
                // the owner of this
                // list
                throw new DOMException(DOMException.INUSE_ATTRIBUTE_ERR,
                                       DOMMessageFormatter.formatMessage(
                                               DOMMessageFormatter.DOM_DOMAIN,
                                               DOMException.INUSE_ATTRIBUTE_ERR, null));
            else
                return attr; // No point adding the 'same' attr again to the
            // same element
        }

        attr.setOwnerElement((ElementImpl)this.ownerNode, true); // Set the owner node

        int i = findNamePoint(attr.getNodeName(), 0);

        AttrImpl previous = null;
        if (i >= 0) { // There's an attribute already with this attr's name
            previous = (AttrImpl) nodes.elementAt(i);
            nodes.setElementAt(attr, i);
            previous.setOwnerElement(null, true);

            // make sure it won't be mistaken with defaults in case it's reused
            previous.isSpecified(true);
        } else {
            i = -1 - i; // Insert point (may be end of list)
            if (null == nodes) {
                nodes = new Vector(5, 10);
            }
            nodes.insertElementAt(attr, i);
        }

        // - Not sure whether we really need this
        // // notify document
        // ownerNode.getOwnerDocument().setAttrNode(attr, previous);

        return previous;

    }

    public Node setNamedItemNS(Node attribute) throws DOMException {
        ownerNode.checkSameOwnerDocument(attribute);
        return setAttribute(attribute, true);
    }
    
    /** Almost a copy of the Xerces impl. */
    Node setAttribute(Node attribute, boolean useDomSemantics) throws DOMException {
        if (attribute.getNodeType() != Node.ATTRIBUTE_NODE) {
            String msg = DOMMessageFormatter.formatMessage(
                    DOMMessageFormatter.DOM_DOMAIN, DOMException.HIERARCHY_REQUEST_ERR,
                    null);
            throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, msg);
        }

        AttrImpl attr = (AttrImpl) attribute;
        if (attr.getOwnerElement() != null) { // If the attribute is owned then:
            //the owner must be the owner of this list
            if (attr.getOwnerElement() != this.ownerNode)
                throw new DOMException(DOMException.INUSE_ATTRIBUTE_ERR,
                                       DOMMessageFormatter.formatMessage(
                                               DOMMessageFormatter.DOM_DOMAIN,
                                               DOMException.INUSE_ATTRIBUTE_ERR, null));
            else
                return attr; // No point adding the 'same' attr again to the
            // same element
        }
        //Set the owner node
        attr.setOwnerElement((ElementImpl)this.ownerNode, useDomSemantics);

        int i = findNamePoint(attr.getNamespaceURI(), attr.getLocalName());
        AttrImpl previous = null;

        if (i >= 0) {
            previous = (AttrImpl) nodes.elementAt(i);
            nodes.setElementAt(attr, i);
            previous.setOwnerElement(null, useDomSemantics);
            // make sure it won't be mistaken with defaults in case it's reused
            previous.isSpecified(true);
        } else {
            // If we can't find by namespaceURI, localName, then we find by
            // nodeName so we know where to insert.
            i = findNamePoint(attr.getNodeName(), 0);
            if (i >= 0) {
                previous = (AttrImpl) nodes.elementAt(i);
                nodes.insertElementAt(attr, i);
            } else {
                i = -1 - i; // Insert point (may be end of list)
                if (null == nodes) {
                    nodes = new Vector(5, 10);
                }
                nodes.insertElementAt(attr, i);
            }
        }

        return previous;
    }

    /**
     * BORROWED from Xerces impl. Cloning a NamedNodeMap is a DEEP OPERATION; it always clones all
     * the nodes contained in the map.
     */

    public AttributeMap cloneMap(ElementImpl ownerNode) {
        AttributeMap newmap = new AttributeMap(ownerNode);
        newmap.cloneContent(this);
        return newmap;
    } // cloneMap():AttributeMap

    /** BORROWED from Xerces impl. */
    protected void cloneContent(AttributeMap srcmap) {
        Vector srcnodes = srcmap.nodes;
        if (srcnodes != null) {
            int size = srcnodes.size();
            if (size != 0) {
                if (nodes == null) {
                    nodes = new Vector(size);
                }
                nodes.setSize(size);
                for (int i = 0; i < size; ++i) {
                    AttrImpl n = (AttrImpl) srcnodes.elementAt(i);
                    AttrImpl clone = (AttrImpl) n.cloneNode(true);
                    clone.isSpecified(n.isSpecified());
                    nodes.setElementAt(clone, i);
                    clone.setOwnerElement(ownerNode, true);
                }
            }
        }
    } // cloneContent():AttributeMap

    /**
     * From org.apache.xerces.dom.NamedNodeMapImpl
     * <p/>
     * Subroutine: Locates the named item, or the point at which said item should be added.
     *
     * @param name Name of a node to look up.
     * @return If positive or zero, the index of the found item. If negative, index of the
     *         appropriate point at which to insert the item, encoded as -1-index and hence
     *         reconvertable by subtracting it from -1. (Encoding because I don't want to recompare
     *         the strings but don't want to burn bytes on a datatype to hold a flagged value.)
     */
    protected int findNamePoint(String name, int start) {

        // Binary search
        int i = 0;
        if (nodes != null) {
            int first = start;
            int last = nodes.size() - 1;

            while (first <= last) {
                i = (first + last) / 2;
                int test = name.compareTo(((Node) (nodes.elementAt(i)))
                        .getNodeName());
                if (test == 0) {
                    return i; // Name found
                } else if (test < 0) {
                    last = i - 1;
                } else {
                    first = i + 1;
                }
            }

            if (first > i) {
                i = first;
            }
        }

        return -1 - i; // not-found has to be encoded.

    } // findNamePoint(String):int

    /** This findNamePoint is for DOM Level 2 Namespaces. */
    protected int findNamePoint(String namespaceURI, String name) {

        if (nodes == null)
            return -1;
        if (name == null)
            return -1;

        // This is a linear search through the same nodes Vector.
        // The Vector is sorted on the DOM Level 1 nodename.
        // The DOM Level 2 NS keys are namespaceURI and Localname,
        // so we must linear search thru it.
        // In addition, to get this to work with nodes without any namespace
        // (namespaceURI and localNames are both null) we then use the nodeName
        // as a secondary key.
        for (int i = 0; i < nodes.size(); i++) {
            NodeImpl a = (NodeImpl) nodes.elementAt(i);
            String aNamespaceURI = a.getNamespaceURI();
            String aLocalName = a.getLocalName();
            if (namespaceURI == null) {
                if (aNamespaceURI == null && (name.equals(aLocalName) ||
                        (aLocalName == null && name.equals(a.getNodeName()))))
                    return i;
            } else {
                if (namespaceURI.equals(aNamespaceURI)
                        && name.equals(aLocalName))
                    return i;
            }
        }
        return -1;
    }

    // Compare 2 nodes in the map. If a precedes b, return true, otherwise
    // return false
    protected boolean precedes(Node a, Node b) {

        if (nodes != null) {
            for (int i = 0; i < nodes.size(); i++) {
                Node n = (Node) nodes.elementAt(i);
                if (n == a)
                    return true;
                if (n == b)
                    return false;
            }
        }

        return false;
    }

    /** NON-DOM: Remove attribute at specified index. */
    protected void removeItem(int index) {
        if (nodes != null && index < nodes.size()) {
            nodes.removeElementAt(index);
        }
    }

    protected Object getItem(int index) {
        if (nodes != null) {
            return nodes.elementAt(index);
        }
        return null;
    }

    protected int addItem(Node arg) {
        int i = findNamePoint(arg.getNamespaceURI(), arg.getLocalName());
        if (i >= 0) {
            nodes.setElementAt(arg, i);
        } else {
            // If we can't find by namespaceURI, localName, then we find by
            // nodeName so we know where to insert.
            i = findNamePoint(arg.getNodeName(), 0);
            if (i >= 0) {
                nodes.insertElementAt(arg, i);
            } else {
                i = -1 - i; // Insert point (may be end of list)
                if (null == nodes) {
                    nodes = new Vector(5, 10);
                }
                nodes.insertElementAt(arg, i);
            }
        }
        return i;
    }

    /**
     * NON-DOM: copy content of this map into the specified vector
     *
     * @param list Vector to copy information into.
     * @return Returns a copy of this node named map.
     */
    protected Vector cloneMap(Vector list) {
        if (list == null) {
            list = new Vector(5, 10);
        }
        list.setSize(0);
        if (nodes != null) {
            for (int i = 0; i < nodes.size(); i++) {
                list.insertElementAt(nodes.elementAt(i), i);
            }
        }

        return list;
    }

    protected int getNamedItemIndex(String namespaceURI, String localName) {
        return findNamePoint(namespaceURI, localName);
    }

    /** NON-DOM remove all elements from this map. */
    public void removeAll() {
        if (nodes != null) {
            nodes.removeAllElements();
        }
    }
    
    void remove(AttrImpl attr, boolean useDomSemantics) {
        if (nodes.remove(attr)) {
            attr.setOwnerElement(null, useDomSemantics);
        }
    }
}
