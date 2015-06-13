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

import javax.xml.XMLConstants;

import org.apache.axiom.core.CoreNSAwareAttribute;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.OMAttributeEx;
import org.apache.axiom.om.impl.common.AxiomAttribute;
import org.apache.axiom.om.impl.common.AxiomText;
import org.w3c.dom.Attr;

public final class NSAwareAttribute extends TypedAttribute implements OMAttributeEx, AxiomAttribute, NamedNode, CoreNSAwareAttribute {
    // TODO: copy isId?
    NSAwareAttribute(String localName, OMNamespace namespace, String type, OMFactory factory) {
        super(null, factory);
        internalSetLocalName(localName);
        internalSetNamespace(namespace);
        this.type = type;
    }
    
    public NSAwareAttribute(DocumentImpl ownerDocument, String localName,
                    OMNamespace ns, String value, OMFactory factory) {
        super(ownerDocument, factory);
        internalSetLocalName(localName);
        if (value != null && value.length() != 0) {
            coreAppendChild((AxiomText)factory.createOMText(value), false);
        }
        this.type = OMConstants.XMLATTRTYPE_CDATA;
        internalSetNamespace(ns);
    }

    public String getName() {
        OMNamespace namespace = getNamespace();
        String localName = getLocalName();
        if (namespace != null) {
            if ((XMLConstants.XMLNS_ATTRIBUTE.equals(localName))) {
                return localName;
            } else if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespace.getNamespaceURI())) {
                return XMLConstants.XMLNS_ATTRIBUTE + ":" + localName;
            } else if (namespace.getPrefix().equals("")) {
                return localName;
            } else {
                return namespace.getPrefix() + ":" + localName;
            }
        } else {
            return localName;
        }
    }

    public String toString() {
        OMNamespace namespace = getNamespace();
        String localName = getLocalName();
        return (namespace == null) ? localName : namespace
                .getPrefix()
                + ":" + localName;
    }

    /**
     * An instance of <code>AttrImpl</code> can act as an <code>OMAttribute</code> and as well as an
     * <code>org.w3c.dom.Attr</code>. So we first check if the object to compare with (<code>obj</code>)
     * is of type <code>OMAttribute</code> (this includes instances of <code>OMAttributeImpl</code> or
     * <code>AttrImpl</code> (instances of this class)). If so we check for the equality
     * of namespaces first (note that if the namespace of this instance is null then for the <code>obj</code>
     * to be equal its namespace must also be null). This condition solely doesn't determine the equality.
     * So we check for the equality of names and values (note that the value can also be null in which case
     * the same argument holds as that for the namespace) of the two instances. If all three conditions are
     * met then we say the two instances are equal.
     *
     * <p>If <code>obj</code> is of type <code>org.w3c.dom.Attr</code> then we perform the same equality check
     * as before. Note that, however, the implementation of the test for equality in this case is little different
     * than before.
     *
     * <p>If <code>obj</code> is neither of type <code>OMAttribute</code> nor of type <code>org.w3c.dom.Attr</code>
     * then we return false.
     *
     * @param obj The object to compare with this instance
     * @return True if the two objects are equal or else false. The equality is checked as explained above.
     */
    public boolean equals(Object obj) {
        OMNamespace namespace = getNamespace();
        String localName = getLocalName();
        String attrValue = getValue();
        if (obj instanceof OMAttribute) { // Checks equality of an OMAttributeImpl or an AttrImpl with this instance
            OMAttribute other = (OMAttribute) obj;
            return (namespace == null ? other.getNamespace() == null :
                    namespace.equals(other.getNamespace()) &&
                    localName.equals(other.getLocalName()) &&
                    (attrValue == null ? other.getAttributeValue() == null :
                            attrValue.toString().equals(other.getAttributeValue())));
        } else if (obj instanceof Attr) {// Checks equality of an org.w3c.dom.Attr with this instance
            Attr other = (Attr)obj;
            String otherNs = other.getNamespaceURI();
            if (namespace == null) { // I don't have a namespace
                if (otherNs != null) {
                    return false; // I don't have a namespace and the other has. So return false
                } else {
                    // Both of us don't have namespaces. So check for name and value equality only
                    return (localName.equals(other.getLocalName()) &&
                            (attrValue == null ? other.getValue() == null :
                                    attrValue.toString().equals(other.getValue())));
                }
            } else { // Ok, now I've a namespace
                String ns = namespace.getNamespaceURI();
                String prefix = namespace.getPrefix();
                String otherPrefix = other.getPrefix();
                // First check for namespaceURI equality. Then check for prefix equality.
                // Then check for name and value equality
                return (ns.equals(otherNs) && (prefix == null ? otherPrefix == null : prefix.equals(otherPrefix)) &&
                        (localName.equals(other.getLocalName())) &&
                        (attrValue == null ? other.getValue() == null :
                                attrValue.toString().equals(other.getValue())));
            }
        }
        return false;
    }

    public int hashCode() {
        OMNamespace namespace = getNamespace();
        String localName = getLocalName();
        String attrValue = getValue();
        return localName.hashCode() ^ (attrValue != null ? attrValue.toString().hashCode() : 0) ^
                (namespace != null ? namespace.hashCode() : 0);
    }

    @Override
    final ParentNode shallowClone(OMCloneOptions options, ParentNode targetParent, boolean namespaceRepairing) {
        // Note: targetParent is always null here
        // TODO
        return new NSAwareAttribute(getLocalName(), getNamespace(), type, getOMFactory());
    }
}
