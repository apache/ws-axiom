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
package org.apache.axiom.dom;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMStringList;

// TODO: should eventually have package access
public final class DOMConfigurationImpl implements DOMConfiguration {
    private static final String CANONICAL_FORM_PARAM = "canonical-form";
    private static final String CDATA_SECTIONS_PARAM ="cdata-sections";
    private static final String CHECK_CHAR_NORMALIZATION_PARAM  = "check-character-normalization";
    private static final String COMMENTS_PARAM = "comments";
    private static final String DATATYPE_NORMALIZATION_PARAM = "datatype-normalization"; 
    private static final String ELEMENT_CONTENT_WHITESPACE_PARAM = "element-content-whitespace";
    private static final String ENTITIES_PARAM = "entities";
    private static final String ERROR_HANDLER_PARAM = "error-handler";
    private static final String INFOSET_PARAM = "infoset";
    private static final String NAMESPACES_PARAM = "namespaces";
    private static final String NAMESPACE_DECLARATIONS_PARAM = "namespace-declarations";
    private static final String NORMALIZE_CHARACTERS_PARAM = "normalize-characters";
    private static final String SCHEMA_LOCATION_PARAM = "schema-location";
    private static final String SCHEMA_TYPE_PARAM = "schema-type";
    private static final String SPLIT_CDATA_SECTIONS_PARAM = "split-cdata-sections";
    private static final String VALIDATE_PARAM = "validate";
    private static final String VALIDATE_IF_SCHEMA_PARAM = "validate-if-schema";
    private static final String WELLFORMED_PARAM = "well-formed";

    public static final int CANONICAL_FORM             = 1<<0;
    public static final int CDATA_SECTIONS             = 1<<1;
    public static final int CHECK_CHAR_NORMALIZATION   = 1<<2;
    public static final int COMMENTS                   = 1<<3;
    public static final int DATATYPE_NORMALIZATION     = 1<<4;
    public static final int ELEMENT_CONTENT_WHITESPACE = 1<<5;
    public static final int ENTITIES                   = 1<<6;
    public static final int NAMESPACES                 = 1<<7;
    public static final int NAMESPACE_DECLARATIONS     = 1<<8;
    public static final int NORMALIZE_CHARACTERS       = 1<<9;
    public static final int SPLIT_CDATA_SECTIONS       = 1<<10;
    public static final int VALIDATE                   = 1<<11;
    public static final int VALIDATE_IF_SCHEMA         = 1<<12;
    public static final int WELLFORMED                 = 1<<13;

    private static final int INFOSET_TRUE_PARAMS = NAMESPACES | COMMENTS | WELLFORMED | NAMESPACE_DECLARATIONS;
    private static final int INFOSET_FALSE_PARAMS = ENTITIES | DATATYPE_NORMALIZATION | CDATA_SECTIONS;
    private static final int INFOSET_MASK = INFOSET_TRUE_PARAMS | INFOSET_FALSE_PARAMS;
    
    /**
     * Defines parameters that can't be changed (because the non default values are not supported).
     */
    private static final int FIXED_MASK = CANONICAL_FORM | CDATA_SECTIONS | CHECK_CHAR_NORMALIZATION | COMMENTS
            | DATATYPE_NORMALIZATION | ELEMENT_CONTENT_WHITESPACE | ENTITIES | NAMESPACE_DECLARATIONS
            | NORMALIZE_CHARACTERS | VALIDATE | VALIDATE_IF_SCHEMA;
    
    private static final Map<String,Integer> paramMap = new HashMap<String,Integer>();
    
    static {
        paramMap.put(CANONICAL_FORM_PARAM, Integer.valueOf(CANONICAL_FORM));
        paramMap.put(CDATA_SECTIONS_PARAM, Integer.valueOf(CDATA_SECTIONS));
        paramMap.put(CHECK_CHAR_NORMALIZATION_PARAM, Integer.valueOf(CHECK_CHAR_NORMALIZATION));
        paramMap.put(COMMENTS_PARAM, Integer.valueOf(COMMENTS));
        paramMap.put(DATATYPE_NORMALIZATION_PARAM, Integer.valueOf(DATATYPE_NORMALIZATION));
        paramMap.put(ELEMENT_CONTENT_WHITESPACE_PARAM, Integer.valueOf(ELEMENT_CONTENT_WHITESPACE));
        paramMap.put(ENTITIES_PARAM, Integer.valueOf(ENTITIES));
        paramMap.put(NAMESPACES_PARAM, Integer.valueOf(NAMESPACES));
        paramMap.put(NAMESPACE_DECLARATIONS_PARAM, Integer.valueOf(NAMESPACE_DECLARATIONS));
        paramMap.put(NORMALIZE_CHARACTERS_PARAM, Integer.valueOf(NORMALIZE_CHARACTERS));
        paramMap.put(SPLIT_CDATA_SECTIONS_PARAM, Integer.valueOf(SPLIT_CDATA_SECTIONS));
        paramMap.put(VALIDATE_PARAM, Integer.valueOf(VALIDATE));
        paramMap.put(VALIDATE_IF_SCHEMA_PARAM, Integer.valueOf(VALIDATE_IF_SCHEMA));
        paramMap.put(WELLFORMED_PARAM, Integer.valueOf(WELLFORMED));
    }
    
    private int params = CDATA_SECTIONS | COMMENTS | ELEMENT_CONTENT_WHITESPACE | ENTITIES
            | NAMESPACES | NAMESPACE_DECLARATIONS | SPLIT_CDATA_SECTIONS | WELLFORMED;
    
    @Override
    public void setParameter(String name, Object value) throws DOMException {
        Integer mask = (Integer)paramMap.get(name);
        if (mask != null) {
            if (value instanceof Boolean) {
                if ((mask.intValue() & FIXED_MASK) == 0) {
                    if (((Boolean)value).booleanValue()) {
                        params |= mask.intValue();
                    } else {
                        params &= ~mask.intValue();
                    }
                } else {
                    // TODO
                }
            } else {
                // TODO
            }
        } else {
            // TODO
        }
    }
    
    public boolean isEnabled(int param) {
        return (params & param) != 0;
    }
    
    @Override
    public Object getParameter(String name) throws DOMException {
        Integer mask = (Integer)paramMap.get(name);
        if (mask != null) {
            return Boolean.valueOf((params & mask.intValue()) != 0);
        } else {
            // TODO
            return null;
        }
    }

    @Override
    public boolean canSetParameter(String name, Object value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public DOMStringList getParameterNames() {
        // TODO Auto-generated method stub
        return null;
    }
}
