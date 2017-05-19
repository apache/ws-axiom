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

import javax.xml.XMLConstants;

/**
 * @deprecated
 */
public interface OMConstants {
    /**
     * @deprecated This constant was used in early prototypes of Axis2 and is no longer relevant.
     */
    String ARRAY_ITEM_NSURI =
            "http://axis.apache.org/encoding/Arrays";

    /**
     * @deprecated This constant was used in early prototypes of Axis2 and is no longer relevant.
     */
    String ARRAY_ITEM_LOCALNAME = "item";

    /**
     * @deprecated This constant was used in early prototypes of Axis2 and is no longer relevant.
     */
    String ARRAY_ITEM_NS_PREFIX = "arrays";

    /**
     * @deprecated This constant was used in early prototypes of Axis2 and is no longer relevant.
     */
    String ARRAY_ITEM_QNAME =
            OMConstants.ARRAY_ITEM_NS_PREFIX + ':'
                    + OMConstants.ARRAY_ITEM_LOCALNAME;

    /** Field DEFAULT_CHAR_SET_ENCODING specifies the default character encoding scheme to be used */
    String DEFAULT_CHAR_SET_ENCODING = "utf-8";
    String DEFAULT_XML_VERSION = "1.0";

    /**
     * @deprecated Use {@link XMLConstants#XML_NS_URI} instead.
     */
    String XMLNS_URI =
            "http://www.w3.org/XML/1998/namespace";

    /**
     * @deprecated Use {@link XMLConstants#XMLNS_ATTRIBUTE_NS_URI} instead.
     */
    String XMLNS_NS_URI = "http://www.w3.org/2000/xmlns/";
    
    /**
     * @deprecated Use {@link XMLConstants#XMLNS_ATTRIBUTE} instead.
     */
    String XMLNS_NS_PREFIX = "xmlns";

    /**
     * @deprecated Use {@link XMLConstants#XML_NS_PREFIX} instead.
     */
    String XMLNS_PREFIX =
            "xml";
    
    /**
     * @deprecated
     */
    String DEFAULT_DEFAULT_NAMESPACE = "\"\"";
    
    String XMLATTRTYPE_CDATA = "CDATA";
    String XMLATTRTYPE_ID = "ID";
    String XMLATTRTYPE_IDREF = "IDREF"; 
    String XMLATTRTYPE_IDREFS = "IDREFS"; 
    String XMLATTRTYPE_NMTOKEN = "NMTOKEN"; 
    String XMLATTRTYPE_NMTOKENS = "NMTOKENS"; 
    String XMLATTRTYPE_ENTITY = "ENTITY"; 
    String XMLATTRTYPE_ENTITIES = "ENTITIES";  
    String XMLATTRTYPE_NOTATION = "NOTATION"; 

}
