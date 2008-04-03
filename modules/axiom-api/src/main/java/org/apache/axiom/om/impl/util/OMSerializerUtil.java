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

package org.apache.axiom.om.impl.util;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
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

public class OMSerializerUtil {
    private static Log log = LogFactory.getLog(OMSerializerUtil.class);
    private static boolean DEBUG_ENABLED = log.isDebugEnabled();
    private static boolean ADV_DEBUG_ENABLED = true;
    
    static long nsCounter = 0;
    
    // This property should be used by the parser to indicate whether 
    // setPrefix should be done before or after the Start Element event.
    // This property is not yet a standard, but at least one parser.
    private static final String IS_SET_PREFIX_BEFORE_PROPERTY = 
        "javax.xml.stream.XMLStreamWriter.isSetPrefixBeforeStartElement";

    /**
     * Method serializeEndpart.
     *
     * @param writer
     * @throws javax.xml.stream.XMLStreamException
     *
     */
    public static void serializeEndpart(XMLStreamWriter writer)
            throws XMLStreamException {
        writer.writeEndElement();
    }

    /**
     * Method serializeAttribute.
     *
     * @param attr
     * @param writer
     * @throws XMLStreamException
     * @deprecated use serializeStartpart instead
     */
    public static void serializeAttribute(OMAttribute attr, XMLStreamWriter writer)
            throws XMLStreamException {

        // first check whether the attribute is associated with a namespace
        OMNamespace ns = attr.getNamespace();
        String prefix = null;
        String namespaceName = null;
        if (ns != null) {

            // add the prefix if it's availble
            prefix = ns.getPrefix();
            namespaceName = ns.getNamespaceURI();
            if (prefix != null) {
                writer.writeAttribute(prefix, namespaceName,
                                      attr.getLocalName(), attr.getAttributeValue());
            } else {
                writer.writeAttribute(namespaceName, attr.getLocalName(),
                                      attr.getAttributeValue());
            }
        } else {
            String localName = attr.getLocalName();
            String attributeValue = attr.getAttributeValue();
            writer.writeAttribute(localName, attributeValue);
        }
    }

    /**
     * Method serializeNamespace.
     *
     * @param namespace
     * @param writer
     * @throws XMLStreamException
     * @deprecated Use serializeStartpart instead
     */
    public static void serializeNamespace(OMNamespace namespace, XMLStreamWriter writer)
            throws XMLStreamException {
        if (namespace == null) {
            return;
        }
        String uri = namespace.getNamespaceURI();
        String prefix = namespace.getPrefix();

        if (uri != null && !"".equals(uri)) {
            String prefixFromWriter = writer.getPrefix(uri);

            // Handling Default Namespaces First
            // Case 1 :
            //        here we are trying define a default namespace. But has this been defined in the current context.
            //        yes, there can be a default namespace, but it may have a different URI. If its a different URI
            //        then explicitly define the default namespace here.
            // Case 2 :
            //        The passed in namespace is a default ns, but there is a non-default ns declared
            //        in the current scope.
            if (("".equals(prefix) && "".equals(prefixFromWriter) &&
                    !uri.equals(writer.getNamespaceContext().getNamespaceURI(""))) ||
                    (prefix != null && "".equals(prefix) &&
                            (prefixFromWriter == null || !prefix.equals(prefixFromWriter)))) {
                // this has not been declared earlier
                writer.writeDefaultNamespace(uri);
                writer.setDefaultNamespace(uri);
            } else {
                prefix = prefix == null ? getNextNSPrefix(writer) : prefix;
                if (prefix != null && !prefix.equals(prefixFromWriter) &&
                        !checkForPrefixInTheCurrentContext(writer, uri, prefix)) {
                    writer.writeNamespace(prefix, uri);
                    writer.setPrefix(prefix, uri);
                }
            }
        } else {
            // now the nsURI passed is "" or null. Meaning we gonna work with defaultNS.
            // check whether there is a defaultNS already declared. If yes, is it the same as this ?
            String currentDefaultNSURI = writer.getNamespaceContext().getNamespaceURI("");
            if ((currentDefaultNSURI != null && !currentDefaultNSURI.equals(uri)) ||
                    uri != null && !uri.equals(currentDefaultNSURI)) {
                // this has not been declared earlier
                writer.writeDefaultNamespace(uri);
                writer.setDefaultNamespace(uri);
            }
        }
    }

    /**
     * Unfortunately there is disagreement in the user community about the semantics of setPrefix on
     * the XMLStreamWriter.  An example will explain the difference: writer.startElement("a")
     * writer.setPrefix("pre", "urn://sample") writer.startElement("b")
     * <p/>
     * Some user communities (woodstox) believe that the setPrefix is associate with the scope for
     * "a" and thus remains in scope until the end of a.  The basis for this believe is
     * XMLStreamWriter javadoc (which some would argue is incomplete).
     * <p/>
     * Some user communities believe that the setPrefix is associated with the "b" element. These
     * communities reference an example in the specification and historical usage of SAX.
     * <p/>
     * This method will return true if the setPrefix is associated with the next writeStartElement.
     *
     * @param writer
     * @return true if setPrefix should be generated before startElement
     */
    public static boolean isSetPrefixBeforeStartElement(XMLStreamWriter writer) {
        try {
            Boolean value = (Boolean)writer.getProperty(IS_SET_PREFIX_BEFORE_PROPERTY);
            // this will always be false if the property is defined
            if (value != null) {
                return value.booleanValue();
            }
        }
        catch (IllegalArgumentException e) {
            // Some parsers throw an exception for unknown properties.
        }
        // Fallback: Toggle based on sun or woodstox implementation.
        NamespaceContext nc = writer.getNamespaceContext();
        return (nc == null || 
                (nc.getClass().getName().indexOf("wstx") == -1 && 
                 nc.getClass().getName().indexOf("sun") == -1));
    }

    /**
     * Method serializeStartpart. Serialize the start tag of an element.
     *
     * @param element
     * @param writer
     * @throws XMLStreamException
     */
    public static void serializeStartpart(OMElement element,
                                          XMLStreamWriter writer) throws XMLStreamException {
        serializeStartpart(element, element.getLocalName(), writer);
    }

    /**
     * Method serializeStartpart. Serialize the start tag of an element.
     *
     * @param element
     * @param localName (in some cases, the caller wants to force a different localName)
     * @param writer
     * @throws XMLStreamException
     */
    public static void serializeStartpart(OMElement element, String localName,
                                          XMLStreamWriter writer)
            throws XMLStreamException {

        // Note: To serialize the start tag, we must follow the order dictated by the JSR-173 (StAX) specification.
        // Please keep this code in sync with the code in StreamingOMSerializer.serializeElement

        // The algorithm is:
        // ... generate setPrefix/setDefaultNamespace for each namespace declaration if the prefix is unassociated.
        // ... generate setPrefix/setDefaultNamespace if the prefix of the element is unassociated
        // ... generate setPrefix/setDefaultNamespace for each unassociated prefix of the attributes.
        //
        // ... generate writeStartElement (See NOTE_A)
        //
        // ... generate writeNamespace/writerDefaultNamespace for the new namespace declarations determine during the "set" processing
        // ... generate writeAttribute for each attribute

        // NOTE_A: To confuse matters, some StAX vendors (including woodstox), believe that the setPrefix bindings
        // should occur after the writeStartElement.  If this is the case, the writeStartElement is generated first.

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

        // Write the startElement if required
        boolean setPrefixFirst = isSetPrefixBeforeStartElement(writer);
        
        if (!setPrefixFirst) {
            if (eNamespace != null) {
                if (ePrefix == null) {
                    if (!isAssociated("", eNamespace, writer)) {
                        if (writePrefixList == null) {
                            writePrefixList = new ArrayList();
                            writeNSList = new ArrayList();
                        }
                        if (! writePrefixList.contains("")) {
                            writePrefixList.add("");
                            writeNSList.add(eNamespace);
                        }
                    }
                    writer.writeStartElement("", localName, eNamespace);
                } else {
                    /*
                     * If XMLStreamWriter.writeStartElement(prefix,localName,namespaceURI) associates
                     * the prefix with the namespace .. 
                     */
                    if (!isAssociated(ePrefix, eNamespace, writer)) {
                        if (writePrefixList == null) {
                            writePrefixList = new ArrayList();
                            writeNSList = new ArrayList();
                        }
                        if (! writePrefixList.contains(ePrefix)) {
                            writePrefixList.add(ePrefix);
                            writeNSList.add(eNamespace);
                        }
                    }
                    
                    writer.writeStartElement(ePrefix, localName, eNamespace);
                }
            } else {
                writer.writeStartElement(localName);
            }
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


            String newPrefix = generateSetPrefix(prefix, namespace, writer, false, setPrefixFirst);
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
        String newPrefix = generateSetPrefix(ePrefix, eNamespace, writer, false, setPrefixFirst);
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
            newPrefix = generateSetPrefix(prefix, namespace, writer, true, setPrefixFirst);
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

        // Write the startElement if required
        if (setPrefixFirst) {
            if (eNamespace != null) {
                if (ePrefix == null) {
                    writer.writeStartElement("", localName, eNamespace);
                } else {
                    writer.writeStartElement(ePrefix, localName, eNamespace);
                }
            } else {
                writer.writeStartElement(localName);
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

    private static boolean checkForPrefixInTheCurrentContext(XMLStreamWriter writer,
                                                             String nameSpaceName, String prefix)
            throws XMLStreamException {
        Iterator prefixesIter = writer.getNamespaceContext().getPrefixes(nameSpaceName);
        while (prefixesIter.hasNext()) {
            String prefix_w = (String) prefixesIter.next();
            if (prefix_w.equals(prefix)) {
                // if found do not declare the ns
                return true;
            }
        }
        return false;
    }

    /**
     * serializeNamespaces
     *
     * @param element
     * @param writer
     * @throws XMLStreamException
     * @deprecated Use serializeStartpart instead
     */
    public static void serializeNamespaces
            (OMElement
                    element,
             XMLStreamWriter writer) throws XMLStreamException {
        Iterator namespaces = element.getAllDeclaredNamespaces();
        if (namespaces != null) {
            while (namespaces.hasNext()) {
                serializeNamespace((OMNamespace) namespaces.next(), writer);
            }
        }
    }

    /**
     * Serialize attributes
     *
     * @param element
     * @param writer
     * @throws XMLStreamException
     * @deprecated Consider using serializeStartpart instead
     */
    public static void serializeAttributes
            (OMElement
                    element,
             XMLStreamWriter writer) throws XMLStreamException {
        Iterator attributes = element.getAllAttributes();
        if (attributes != null && attributes.hasNext()) {
            while (attributes.hasNext()) {
                serializeAttribute((OMAttribute) attributes.next(),
                                   writer);
            }
        }
    }

    /**
     * Method serializeNormal.
     *
     * @param writer
     * @param cache
     * @throws XMLStreamException
     */
    public static void serializeNormal
            (OMElement
                    element, XMLStreamWriter writer, boolean cache)
            throws XMLStreamException {

        if (cache) {
            element.build();
        }

        serializeStartpart(element, writer);
        OMNode firstChild = element.getFirstOMChild();
        if (firstChild != null) {
            if (cache) {
                (firstChild).serialize(writer);
            } else {
                (firstChild).serializeAndConsume(writer);
            }
        }
        serializeEndpart(writer);
    }

    public static void serializeByPullStream
            (OMElement
                    element, XMLStreamWriter writer) throws XMLStreamException {
        serializeByPullStream(element, writer, false);
    }

    public static void serializeByPullStream
            (OMElement
                    element, XMLStreamWriter writer, boolean cache) throws XMLStreamException {
        StreamingOMSerializer streamingOMSerializer = new StreamingOMSerializer();
        if (cache) {
            streamingOMSerializer.serialize(element.getXMLStreamReader(),
                                            writer);
        } else {
            XMLStreamReader xmlStreamReaderWithoutCaching =
                    element.getXMLStreamReaderWithoutCaching();
            streamingOMSerializer.serialize(xmlStreamReaderWithoutCaching,
                                            writer);
        }
    }

    /**
     * Get the next prefix name
     * @return next prefix name
     */
    public static String getNextNSPrefix() {
        
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
        if (DEBUG_ENABLED) {
            log.debug("Obtained next prefix:" + prefix);
            if (ADV_DEBUG_ENABLED) {
                log.debug(CommonUtils.callStackToString());
            }
        }
        return prefix;
    }

    public static String getNextNSPrefix(XMLStreamWriter writer) {
        String prefix = getNextNSPrefix();
        while (writer.getNamespaceContext().getNamespaceURI(prefix) != null) {
            prefix = getNextNSPrefix();
        }

        return prefix;
    }

    /**
     * Generate setPrefix/setDefaultNamespace if the prefix is not associated
     *
     * @param prefix
     * @param namespace
     * @param writer
     * @param attr
     * @return prefix name if a setPrefix/setDefaultNamespace is performed
     */
    public static String generateSetPrefix(String prefix, String namespace, XMLStreamWriter writer,
                                           boolean attr, boolean isSetPrefixFirst) throws XMLStreamException {
        prefix = (prefix == null) ? "" : prefix;
        
        
        // If the prefix and namespace are already associated, no generation is needed
        if (isAssociated(prefix, namespace, writer)) {
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
     * @param writer
     * @return true if the prefix is associated with the namespace in the current context
     */
    public static boolean isAssociated(String prefix, String namespace, XMLStreamWriter writer) 
        throws XMLStreamException {
        
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
            
            // Cannot associate a prefix with an unqualifed name.
            // However sometimes axiom creates a fake prefix name if xmns="" is not in effect.
            // So return true
            if (prefix.length() > 0) {
                return true;  
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
                if (DEBUG_ENABLED) {
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
