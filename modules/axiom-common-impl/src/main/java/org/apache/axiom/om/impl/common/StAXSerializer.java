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
package org.apache.axiom.om.impl.common;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.om.impl.serialize.StreamingOMSerializer;
import org.apache.axiom.om.util.CommonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class StAXSerializer {
    private static final Log log = LogFactory.getLog(StAXSerializer.class);
    private static boolean ADV_DEBUG_ENABLED = true;
    
    private static long nsCounter = 0;
    
    private static final String XSI_URI = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String XSI_LOCAL_NAME = "type";
    
    private final XMLStreamWriter writer;
    
    public StAXSerializer(XMLStreamWriter writer) {
        this.writer = writer;
    }

    public XMLStreamWriter getWriter() {
        return writer;
    }

    /**
     * Method serializeEndpart.
     *
     * @throws javax.xml.stream.XMLStreamException
     *
     */
    public void serializeEndpart() throws XMLStreamException {
        writer.writeEndElement();
    }

    /**
     * Method serializeStartpart. Serialize the start tag of an element.
     *
     * @param element
     * @throws XMLStreamException
     */
    public void serializeStartpart(OMElement element) throws XMLStreamException {

        // Note: To serialize the start tag, we must follow the order dictated by the JSR-173 (StAX) specification.
        // Please keep this code in sync with the code in StreamingOMSerializer.serializeElement

        // The algorithm is:
        // ... generate writeStartElement
        //
        // ... generate setPrefix/setDefaultNamespace for each namespace declaration if the prefix is unassociated.
        // ... generate setPrefix/setDefaultNamespace if the prefix of the element is unassociated
        // ... generate setPrefix/setDefaultNamespace for each unassociated prefix of the attributes.
        //
        // ... generate writeNamespace/writerDefaultNamespace for the new namespace declarations determine during the "set" processing
        // ... generate writeAttribute for each attribute

        ArrayList writePrefixList = null;
        ArrayList writeNSList = null;

        // Get the namespace and prefix of the element
        OMNamespace eOMNamespace = element.getNamespace();
        String ePrefix = null;
        String eNamespace = null;
        if (eOMNamespace != null) {
            ePrefix = eOMNamespace.getPrefix();
            eNamespace = eOMNamespace.getNamespaceURI();
        }
        ePrefix = (ePrefix != null && ePrefix.length() == 0) ? null : ePrefix;
        eNamespace = (eNamespace != null && eNamespace.length() == 0) ? null : eNamespace;

        if (eNamespace != null) {
            if (ePrefix == null) {
                if (!isAssociated("", eNamespace)) {
                    if (writePrefixList == null) {
                        writePrefixList = new ArrayList();
                        writeNSList = new ArrayList();
                    }
                    if (! writePrefixList.contains("")) {
                        writePrefixList.add("");
                        writeNSList.add(eNamespace);
                    }
                }
                writer.writeStartElement("", element.getLocalName(), eNamespace);
            } else {
                /*
                 * If XMLStreamWriter.writeStartElement(prefix,localName,namespaceURI) associates
                 * the prefix with the namespace .. 
                 */
                if (!isAssociated(ePrefix, eNamespace)) {
                    if (writePrefixList == null) {
                        writePrefixList = new ArrayList();
                        writeNSList = new ArrayList();
                    }
                    if (! writePrefixList.contains(ePrefix)) {
                        writePrefixList.add(ePrefix);
                        writeNSList.add(eNamespace);
                    }
                }
                
                writer.writeStartElement(ePrefix, element.getLocalName(), eNamespace);
            }
        } else {
            writer.writeStartElement(element.getLocalName());
        }

        // Generate setPrefix for the namespace declarations
        Iterator it = element.getAllDeclaredNamespaces();
        while (it != null && it.hasNext()) {
            OMNamespace omNamespace = (OMNamespace) it.next();
            String prefix = null;
            String namespace = null;
            if (omNamespace != null) {
                prefix = omNamespace.getPrefix();
                namespace = omNamespace.getNamespaceURI();
            }
            prefix = (prefix != null && prefix.length() == 0) ? null : prefix;
            namespace = (namespace != null && namespace.length() == 0) ? null : namespace;


            String newPrefix = generateSetPrefix(prefix, namespace, false);
            // If this is a new association, remember it so that it can written out later
            if (newPrefix != null) {
                if (writePrefixList == null) {
                    writePrefixList = new ArrayList();
                    writeNSList = new ArrayList();
                }
                if (!writePrefixList.contains(newPrefix)) {
                    writePrefixList.add(newPrefix);
                    writeNSList.add(namespace);
                }
            }
        }

        // Generate setPrefix for the element
        // Get the prefix and namespace of the element.  "" and null are identical.
        String newPrefix = generateSetPrefix(ePrefix, eNamespace, false);
        // If this is a new association, remember it so that it can written out later
        if (newPrefix != null) {
            if (writePrefixList == null) {
                writePrefixList = new ArrayList();
                writeNSList = new ArrayList();
            }
            if (!writePrefixList.contains(newPrefix)) {
                writePrefixList.add(newPrefix);
                writeNSList.add(eNamespace);
            }
        }

        // Now Generate setPrefix for each attribute
        Iterator attrs = element.getAllAttributes();
        while (attrs != null && attrs.hasNext()) {
            OMAttribute attr = (OMAttribute) attrs.next();
            OMNamespace omNamespace = attr.getNamespace();
            String prefix = null;
            String namespace = null;
            if (omNamespace != null) {
                prefix = omNamespace.getPrefix();
                namespace = omNamespace.getNamespaceURI();
            }
            prefix = (prefix != null && prefix.length() == 0) ? null : prefix;
            namespace = (namespace != null && namespace.length() == 0) ? null : namespace;

            // Default prefix referencing is not allowed on an attribute
            if (prefix == null && namespace != null) {
                String writerPrefix = writer.getPrefix(namespace);
                writerPrefix =
                        (writerPrefix != null && writerPrefix.length() == 0) ? null : writerPrefix;
                prefix = (writerPrefix != null) ?
                        writerPrefix : getNextNSPrefix();
            }
            newPrefix = generateSetPrefix(prefix, namespace, true);
            // If the prefix is not associated with a namespace yet, remember it so that we can
            // write out a namespace declaration
            if (newPrefix != null) {
                if (writePrefixList == null) {
                    writePrefixList = new ArrayList();
                    writeNSList = new ArrayList();
                }
                if (!writePrefixList.contains(newPrefix)) {
                    writePrefixList.add(newPrefix);
                    writeNSList.add(namespace);
                }
            }
        }
        
        // Now Generate setPrefix for each prefix referenced in an xsi:type
        // For example xsi:type="p:dataType"
        // The following code will make sure that setPrefix is called for "p".
        attrs = element.getAllAttributes();
        while (attrs != null && attrs.hasNext()) {
            OMAttribute attr = (OMAttribute) attrs.next();
            OMNamespace omNamespace = attr.getNamespace();
            String prefix = null;
            String namespace = null;
            if (omNamespace != null) {
                prefix = omNamespace.getPrefix();
                namespace = omNamespace.getNamespaceURI();
            }
            prefix = (prefix != null && prefix.length() == 0) ? null : prefix;
            namespace = (namespace != null && namespace.length() == 0) ? null : namespace;
            String local = attr.getLocalName();

            if (XSI_URI.equals(namespace) &&
                    XSI_LOCAL_NAME.equals(local)) {
                String value = attr.getAttributeValue();
                if (log.isDebugEnabled()) {
                    log.debug("The value of xsi:type is " + value);
                }
                if (value != null) {
                    value = value.trim();
                    if (value.indexOf(":") > 0) {
                        String refPrefix = value.substring(0, value.indexOf(":"));
                        OMNamespace omNS = element.findNamespaceURI(refPrefix);
                        String refNamespace = (omNS == null) ? null : omNS.getNamespaceURI();
                        if (refNamespace != null && refNamespace.length() > 0) {

                            newPrefix = generateSetPrefix(refPrefix, 
                                    refNamespace, 
                                    true);
                            // If the prefix is not associated with a namespace yet, remember it so that we can
                            // write out a namespace declaration
                            if (newPrefix != null) {
                                if (log.isDebugEnabled()) {
                                    log.debug("An xmlns:" + newPrefix +"=\"" +  refNamespace +"\" will be written");
                                }
                                if (writePrefixList == null) {
                                    writePrefixList = new ArrayList();
                                    writeNSList = new ArrayList();
                                }
                                if (!writePrefixList.contains(newPrefix)) {
                                    writePrefixList.add(newPrefix);
                                    writeNSList.add(refNamespace);
                                }
                            }
                        }
                    }
                }
            }
        }

        // Now write out the list of namespace declarations in this list that we constructed
        // while doing the "set" processing.
        if (writePrefixList != null) {
            for (int i = 0; i < writePrefixList.size(); i++) {
                String prefix = (String) writePrefixList.get(i);
                String namespace = (String) writeNSList.get(i);
                if (prefix != null) {
                    if (namespace == null) {
                        writer.writeNamespace(prefix, "");
                    } else {
                        writer.writeNamespace(prefix, namespace);
                    }
                } else {
                    writer.writeDefaultNamespace(namespace);
                }
            }
        }

        // Now write the attributes
        attrs = element.getAllAttributes();
        while (attrs != null && attrs.hasNext()) {
            OMAttribute attr = (OMAttribute) attrs.next();
            OMNamespace omNamespace = attr.getNamespace();
            String prefix = null;
            String namespace = null;
            if (omNamespace != null) {
                prefix = omNamespace.getPrefix();
                namespace = omNamespace.getNamespaceURI();
            }
            prefix = (prefix != null && prefix.length() == 0) ? null : prefix;
            namespace = (namespace != null && namespace.length() == 0) ? null : namespace;

            if (prefix == null && namespace != null) {
                // Default namespaces are not allowed on an attribute reference.
                // Earlier in this code, a unique prefix was added for this case...now obtain and use it
                prefix = writer.getPrefix(namespace);
                //XMLStreamWriter doesn't allow for getPrefix to know whether you're asking for the prefix
                //for an attribute or an element. So if the namespace matches the default namespace getPrefix will return
                //the empty string, as if it were an element, in all cases (even for attributes, and even if
                //there was a prefix specifically set up for this), which is not the desired behavior.
                //Since the interface is base java, we can't fix it where we need to (by adding an attr boolean to
                //XMLStreamWriter.getPrefix), so we hack it in here...
                if (prefix == null || "".equals(prefix)) {
                    for (int i = 0; i < writePrefixList.size(); i++) {
                        if (namespace.equals((String) writeNSList.get(i))) {
                            prefix = (String) writePrefixList.get(i);
                        }
                    }
                }
            } else if (namespace != null) {
                // Use the writer's prefix if it is different, but if the writers
                // prefix is empty then do not replace because attributes do not
                // default to the default namespace like elements do.
                String writerPrefix = writer.getPrefix(namespace);
                if (!prefix.equals(writerPrefix) && writerPrefix  != null && !"".equals(writerPrefix)) {
                    prefix = writerPrefix;
                }
            }
            if (namespace != null) {
                if(prefix == null && OMConstants.XMLNS_URI.equals(namespace)){
                    prefix = OMConstants.XMLNS_PREFIX;
                }
                // Qualified attribute
                writer.writeAttribute(prefix, namespace,
                                      attr.getLocalName(),
                                      attr.getAttributeValue());
            } else {
                // Unqualified attribute
                writer.writeAttribute(attr.getLocalName(),
                                      attr.getAttributeValue());
            }
        }
    }

    public void serializeByPullStream(OMElement element, boolean cache) throws XMLStreamException {
        XMLStreamReader reader = element.getXMLStreamReader(cache);
        try {
            new StreamingOMSerializer().serialize(reader, writer);
        } finally {
            reader.close();
        }
    }

    public void serializeChildren(OMContainer container, boolean cache) throws XMLStreamException {
        if (cache) {
            IChildNode child = (IChildNode)container.getFirstOMChild();
            while (child != null) {
                child.internalSerialize(this, true);
                child = (IChildNode)child.getNextOMSibling();
            }
        } else {
            IChildNode child = (IChildNode)container.getFirstOMChild();
            while (child != null) {
                if ((!(child instanceof OMElement)) || child.isComplete() ||
                        ((OMElement)child).getBuilder() == null) {
                    child.internalSerialize(this, false);
                } else {
                    OMElement element = (OMElement) child;
                    element.getBuilder().setCache(false);
                    serializeByPullStream(element, cache);
                }
                child = (IChildNode)child.getNextOMSiblingIfAvailable();
            }
        }
    }

    /**
     * Get the next prefix name
     * @return next prefix name
     */
    private static String getNextNSPrefix() {
        
        String prefix = "axis2ns" + ++nsCounter % Long.MAX_VALUE;
        
        /**
         * Calling getNextNSPrefix is "a last gasp" approach
         * for obtaining a prefix.  In almost all cases, the
         * OM element should be provided a prefix by the source parser
         * or programatically by the user.  We only get to this
         * spot if one was not supplied.
         * 
         * The debug information is two-fold.  
         * (1) It helps users determine at what point in the code this default
         * prefix is getting built.  This will help them identify
         * where to change their code if they don't want a default
         * prefix.
         * 
         * (2) It identifies this place in the code as suspect.
         * Do we really want to keep generating new prefixes (?).
         * This could result in lots of symbol table entries for the
         * subsequent parser that reads this data.  This could hamper
         * extremely long run usages.
         * This could be a point where we want a plugin so that users can
         * decide their own strategy.  Examples from other products
         * include generating a prefix number from the namespace
         * string.
         * 
         */
        if (log.isDebugEnabled()) {
            log.debug("Obtained next prefix:" + prefix);
            if (ADV_DEBUG_ENABLED && log.isTraceEnabled()) {
                log.trace(CommonUtils.callStackToString());
            }
        }
        return prefix;
    }

    /**
     * Generate setPrefix/setDefaultNamespace if the prefix is not associated
     *
     * @param prefix
     * @param namespace
     * @param attr
     * @return prefix name if a setPrefix/setDefaultNamespace is performed
     */
    private String generateSetPrefix(String prefix, String namespace, boolean attr) throws XMLStreamException {
        prefix = (prefix == null) ? "" : prefix;
        
        
        // If the prefix and namespace are already associated, no generation is needed
        if (isAssociated(prefix, namespace)) {
            return null;
        }
        
        // Attributes without a prefix always are associated with the unqualified namespace
        // according to the schema specification.  No generation is needed.
        if (prefix.length() == 0 && namespace == null && attr) {
            return null;
        }
        
        // Generate setPrefix/setDefaultNamespace if the prefix is not associated.
        String newPrefix = null;
        if (namespace != null) {
            // Qualified Namespace
            if (prefix.length() == 0) {
                writer.setDefaultNamespace(namespace);
                newPrefix = "";
            } else {
                writer.setPrefix(prefix, namespace);
                newPrefix = prefix;
            }
        } else {
            // Unqualified Namespace
            // Disable the default namespace
            writer.setDefaultNamespace("");
            newPrefix = "";
        }
        return newPrefix;
    }
    /**
     * @param prefix 
     * @param namespace
     * @return true if the prefix is associated with the namespace in the current context
     */
    private boolean isAssociated(String prefix, String namespace) throws XMLStreamException {
        
        // The "xml" prefix is always (implicitly) associated. Returning true here makes sure that
        // we never write a declaration for the xml namespace. See AXIOM-37 for a discussion
        // of this issue.
        if ("xml".equals(prefix)) {
            return true;
        }
        
        // NOTE: Calling getNamespaceContext() on many XMLStreamWriter implementations is expensive.
        // Please use other writer methods first.
        
        // For consistency, convert null arguments.
        // This helps get around the parser implementation differences.
        // In addition, the getPrefix/getNamespace methods cannot be called with null parameters.
        prefix = (prefix == null) ? "" : prefix;
        namespace = (namespace == null) ? "" : namespace;
        
        if (namespace.length() > 0) {
            // QUALIFIED NAMESPACE
            // Get the namespace associated with the prefix
            String writerPrefix = writer.getPrefix(namespace);
            if (prefix.equals(writerPrefix)) {
                return true;
            }
            
            // It is possible that the namespace is associated with multiple prefixes,
            // So try getting the namespace as a second step.
            if (writerPrefix != null) {
                NamespaceContext nsContext = writer.getNamespaceContext();
                if(nsContext != null) {
                    String writerNS = nsContext.getNamespaceURI(prefix);
                    return namespace.equals(writerNS);
                }
            }
            return false;
        } else {
            // UNQUALIFIED NAMESPACE
            
            // Neither XML 1.0 nor XML 1.1 allow to associate a prefix with an unqualified name (see also AXIOM-372).
            if (prefix.length() > 0) {
                throw new OMException("Invalid namespace declaration: Prefixed namespace bindings may not be empty.");  
            }
            
            // Get the namespace associated with the prefix.
            // It is illegal to call getPrefix with null, but the specification is not
            // clear on what happens if called with "".  So the following code is 
            // protected
            try {
                String writerPrefix = writer.getPrefix("");
                if (writerPrefix != null && writerPrefix.length() == 0) {
                    return true;
                }
            } catch (Throwable t) {
                if (log.isDebugEnabled()) {
                    log.debug("Caught exception from getPrefix(\"\"). Processing continues: " + t);
                }
            }
            
            
            
            // Fallback to using the namespace context
            NamespaceContext nsContext = writer.getNamespaceContext();
            if (nsContext != null) {
                String writerNS = nsContext.getNamespaceURI("");
                if (writerNS != null && writerNS.length() > 0) {
                    return false;
                }
            }
            return true;
        }
    }
}
