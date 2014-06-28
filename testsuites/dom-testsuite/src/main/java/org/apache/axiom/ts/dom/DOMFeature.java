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
package org.apache.axiom.ts.dom;

import java.util.Set;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMFeature {
    static final class Signature {
        private final Class<?> clazz;
        private final String method;
        
        Signature(Class<?> clazz, String method) {
            this.clazz = clazz;
            this.method = method;
        }
        
        boolean matches(Class<?> clazz, String method) {
            return this.method.equals(method) && this.clazz.isAssignableFrom(clazz);
        }
    }
    
    public static final DOMFeature ENTITIES = new DOMFeature(
            new Signature(DocumentType.class, "getEntities"),
            new Signature(Document.class, "createEntityReference"));
    public static final DOMFeature NOTATIONS = new DOMFeature(
            new Signature(DocumentType.class, "getNotations"));
    public static final DOMFeature TYPE_INFO = new DOMFeature(
            new Signature(Attr.class, "getSchemaTypeInfo"),
            new Signature(Element.class, "getSchemaTypeInfo"));
    public static final DOMFeature BASE_URI = new DOMFeature(
            new Signature(Node.class, "getBaseURI"));
    
    private static final DOMFeature[] allFeatures = new DOMFeature[] {
        ENTITIES,
        NOTATIONS,
        TYPE_INFO,
        BASE_URI,
    };
    
    private final Signature[] signatures;
    
    private DOMFeature(Signature... signatures) {
        this.signatures = signatures;
    }

    private boolean matches(Class<?> clazz, String method) {
        for (Signature signature : signatures) {
            if (signature.matches(clazz, method)) {
                return true;
            }
        }
        return false;
    }
    
    static void matchFeatures(String clazz, String method, Set<DOMFeature> usedFeatures) {
        for (DOMFeature feature : allFeatures) {
            try {
                if (!usedFeatures.contains(feature) && feature.matches(Class.forName(clazz.replace('/', '.')), method)) {
                    usedFeatures.add(feature);
                }
            } catch (ClassNotFoundException ex) {
                // Ignore
            }
        }
    }
}
