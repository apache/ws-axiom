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
package org.apache.axiom.om.impl.common.serializer;

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSerializable;

public abstract class Serializer {
    private static final String XSI_URI = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String XSI_LOCAL_NAME = "type";
    
    private final OMElement contextElement;
    private final ArrayList writePrefixList = new ArrayList();
    private final ArrayList writeNSList = new ArrayList();
    
    public Serializer(OMSerializable contextNode) {
        if (contextNode instanceof OMNode) {
            OMContainer parent = ((OMNode)contextNode).getParent();
            if (parent instanceof OMElement) {
                contextElement = (OMElement)parent; 
            } else {
                contextElement = null;
            }
        } else {
            contextElement = null;
        }
    }

    public final void serializeStartpart(OMElement element) throws OutputException {
        OMNamespace ns = element.getNamespace();
        if (ns == null) {
            beginStartElement("", "", element.getLocalName());
        } else {
            beginStartElement(ns.getPrefix(), ns.getNamespaceURI(), element.getLocalName());
        }
        for (Iterator it = element.getAllDeclaredNamespaces(); it.hasNext(); ) {
            ns = (OMNamespace)it.next();
            generateSetPrefix(ns.getPrefix(), ns.getNamespaceURI(), false);
        }
        for (Iterator it = element.getAllAttributes(); it.hasNext(); ) {
            OMAttribute attr = (OMAttribute)it.next();
            ns = attr.getNamespace();
            if (ns == null) {
                processAttribute("", "", attr.getLocalName(), attr.getAttributeValue());
            } else {
                processAttribute(ns.getPrefix(), ns.getNamespaceURI(), attr.getLocalName(), attr.getAttributeValue());
            }
        }
        finishStartElement();
    }
    
    public final void copyEvent(XMLStreamReader reader) throws XMLStreamException, OutputException {
        int eventType = reader.getEventType();
        switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                beginStartElement(normalize(reader.getPrefix()), normalize(reader.getNamespaceURI()), reader.getLocalName());
                for (int i=0, count=reader.getNamespaceCount(); i<count; i++) {
                    generateSetPrefix(normalize(reader.getNamespacePrefix(i)), normalize(reader.getNamespacePrefix(i)), false);
                }
                for (int i=0, count=reader.getAttributeCount(); i<count; i++) {
                    processAttribute(
                            normalize(reader.getAttributePrefix(i)),
                            normalize(reader.getAttributeNamespace(i)),
                            reader.getAttributeLocalName(i),
                            reader.getAttributeValue(i));
                }
                finishStartElement();
                break;
            case XMLStreamReader.CHARACTERS:
            case XMLStreamReader.SPACE:
            case XMLStreamReader.CDATA:
                writeText(eventType, reader.getText());
                break;
        }
    }
    
    private static String normalize(String s) {
        return s == null ? "" : s;
    }
    
    private void beginStartElement(String prefix, String namespaceURI, String localName) throws OutputException {
        writeStartElement(prefix, namespaceURI, localName);
        generateSetPrefix(prefix, namespaceURI, false);
    }
    
    private void processAttribute(String prefix, String namespaceURI, String localName, String value) throws OutputException {
        generateSetPrefix(prefix, namespaceURI, true);
        if (contextElement != null && namespaceURI.equals(XSI_URI) && localName.equals(XSI_LOCAL_NAME)) {
            String trimmedValue = value.trim();
            if (trimmedValue.indexOf(":") > 0) {
                String refPrefix = trimmedValue.substring(0, trimmedValue.indexOf(":"));
                OMNamespace ns = contextElement.findNamespaceURI(refPrefix);
                if (ns != null) {
                    generateSetPrefix(refPrefix, ns.getNamespaceURI(), true);
                }
            }
        }
        writeAttribute(prefix, namespaceURI, localName, value);
    }
    
    private void finishStartElement() throws OutputException {
        for (int i = 0; i < writePrefixList.size(); i++) {
            writeNamespace((String)writePrefixList.get(i), (String)writeNSList.get(i));
        }
        writePrefixList.clear();
        writeNSList.clear();
    }
    
    /**
     * Generate setPrefix/setDefaultNamespace if the prefix is not associated
     *
     * @param prefix the namespace prefix; must not be <code>null</code>
     * @param namespaceURI the namespace URI; must not be <code>null</code>
     * @param attr
     * @return prefix name if a setPrefix/setDefaultNamespace is performed
     */
    private void generateSetPrefix(String prefix, String namespaceURI, boolean attr) throws OutputException {
        // If the prefix and namespace are already associated, no generation is needed
        if (isAssociated(prefix, namespaceURI)) {
            return;
        }
        
        // Attributes without a prefix always are associated with the unqualified namespace
        // according to the schema specification.  No generation is needed.
        if (prefix.length() == 0 && namespaceURI.length() == 0 && attr) {
            return;
        }
        
        // Generate setPrefix/setDefaultNamespace if the prefix is not associated.
        setPrefix(prefix, namespaceURI);
        // If this is a new association, remember it so that it can written out later
        if (!writePrefixList.contains(prefix)) {
            writePrefixList.add(prefix);
            writeNSList.add(namespaceURI);
        }
    }
    
    protected abstract boolean isAssociated(String prefix, String namespace) throws OutputException;
    
    protected abstract void setPrefix(String prefix, String namespaceURI) throws OutputException;
    
    protected abstract void writeStartElement(String prefix, String namespaceURI, String localName) throws OutputException;
    
    protected abstract void writeNamespace(String prefix, String namespaceURI) throws OutputException;
    
    protected abstract void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws OutputException;
    
    public abstract void writeText(int type, String data) throws OutputException;
}
