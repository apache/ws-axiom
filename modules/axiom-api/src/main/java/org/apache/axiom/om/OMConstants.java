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

/** Interface OMConstants */
public interface OMConstants {

    // OMBuilder constants
    /** Field PUSH_TYPE_BUILDER */
    static final short PUSH_TYPE_BUILDER = 0;

    /** Field PULL_TYPE_BUILDER */
    static final short PULL_TYPE_BUILDER = 1;

    /** Field ARRAY_ITEM_NSURI */
    static final String ARRAY_ITEM_NSURI =
            "http://axis.apache.org/encoding/Arrays";

    /** Field ARRAY_ITEM_LOCALNAME */
    static final String ARRAY_ITEM_LOCALNAME = "item";

    /** Field ARRAY_ITEM_NS_PREFIX */
    static final String ARRAY_ITEM_NS_PREFIX = "arrays";

    /** Field ARRAY_ITEM_QNAME */
    static final String ARRAY_ITEM_QNAME =
            OMConstants.ARRAY_ITEM_NS_PREFIX + ':'
                    + OMConstants.ARRAY_ITEM_LOCALNAME;

    /** Field DEFAULT_CHAR_SET_ENCODING specifies the default character encoding scheme to be used */
    static final String DEFAULT_CHAR_SET_ENCODING = "utf-8";
    static final String DEFAULT_XML_VERSION = "1.0";


    static final String XMLNS_URI =
            "http://www.w3.org/XML/1998/namespace";

    static final String XMLNS_NS_URI = "http://www.w3.org/2000/xmlns/";
    final static String XMLNS_NS_PREFIX = "xmlns";

    static final String XMLNS_PREFIX =
            "xml";
    
    /**
     * {@link javax.xml.stream.XMLStreamReader} property used to check if a given
     * {@link javax.xml.stream.XMLStreamConstants#CHARACTERS} event represents
     * base64 encoded binary data exposed as a {@link javax.activation.DataHandler}.
     * 
     * @see org.apache.axiom.om.impl.builder.StAXOMBuilder
     */
    String IS_BINARY = "Axiom.IsBinary";
    
    /**
     * {@link javax.xml.stream.XMLStreamReader} property used to retrieve the
     * {@link javax.activation.DataHandler} for a
     * {@link javax.xml.stream.XMLStreamConstants#CHARACTERS} event representing
     * base64 encoded binary data.
     * 
     * @see org.apache.axiom.om.impl.builder.StAXOMBuilder
     */
    String DATA_HANDLER = "Axiom.DataHandler";
    
    /**
     * {@link javax.xml.stream.XMLStreamReader} property indicating that the
     * reader is capable of exposing base64 encoded binary content as
     * {@link javax.activation.DataHandler} objects.
     * 
     * @see org.apache.axiom.om.impl.builder.StAXOMBuilder
     */
    String IS_DATA_HANDLERS_AWARE = "IsDatahandlersAwareParsing"; 

    /** No its not a mistake. This is the default nsURI of the default namespace of a node */
    static final String DEFAULT_DEFAULT_NAMESPACE = "\"\"";
    
	static final String XMLATTRTYPE_CDATA = "CDATA";
	static final String XMLATTRTYPE_ID = "ID";
	static final String XMLATTRTYPE_IDREF = "IDREF"; 
	static final String XMLATTRTYPE_IDREFS = "IDREFS"; 
	static final String XMLATTRTYPE_NMTOKEN = "NMTOKEN"; 
	static final String XMLATTRTYPE_NMTOKENS = "NMTOKENS"; 
	static final String XMLATTRTYPE_ENTITY = "ENTITY"; 
	static final String XMLATTRTYPE_ENTITIES = "ENTITIES";  
	static final String XMLATTRTYPE_NOTATION = "NOTATION"; 

}
