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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Attr;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.domts.level1.core.attrremovechild1;
import org.w3c.domts.level1.core.attrreplacechild1;
import org.w3c.domts.level1.core.attrsetvaluenomodificationallowederr;
import org.w3c.domts.level1.core.attrsetvaluenomodificationallowederrEE;
import org.w3c.domts.level1.core.characterdataappenddatanomodificationallowederr;
import org.w3c.domts.level1.core.characterdataappenddatanomodificationallowederrEE;
import org.w3c.domts.level1.core.characterdatadeletedatanomodificationallowederr;
import org.w3c.domts.level1.core.characterdatadeletedatanomodificationallowederrEE;
import org.w3c.domts.level1.core.characterdatainsertdatanomodificationallowederr;
import org.w3c.domts.level1.core.characterdatainsertdatanomodificationallowederrEE;
import org.w3c.domts.level1.core.characterdatareplacedatanomodificationallowederr;
import org.w3c.domts.level1.core.characterdatareplacedatanomodificationallowederrEE;
import org.w3c.domts.level1.core.characterdatasetdatanomodificationallowederr;
import org.w3c.domts.level1.core.characterdatasetdatanomodificationallowederrEE;
import org.w3c.domts.level1.core.documentcreateentityreferenceknown;
import org.w3c.domts.level1.core.elementremoveattributenodenomodificationallowederr;
import org.w3c.domts.level1.core.elementremoveattributenodenomodificationallowederrEE;
import org.w3c.domts.level1.core.elementremoveattributenomodificationallowederr;
import org.w3c.domts.level1.core.elementremoveattributenomodificationallowederrEE;
import org.w3c.domts.level1.core.elementsetattributenodenomodificationallowederr;
import org.w3c.domts.level1.core.elementsetattributenodenomodificationallowederrEE;
import org.w3c.domts.level1.core.elementsetattributenomodificationallowederrEE;
import org.w3c.domts.level1.core.hc_textparseintolistofelements;
import org.w3c.domts.level1.core.nodeappendchildnomodificationallowederr;
import org.w3c.domts.level1.core.nodeinsertbeforenomodificationallowederr;
import org.w3c.domts.level1.core.noderemovechildnomodificationallowederr;
import org.w3c.domts.level1.core.noderemovechildnomodificationallowederrEE;
import org.w3c.domts.level1.core.nodereplacechildnomodificationallowederr;
import org.w3c.domts.level1.core.nodereplacechildnomodificationallowederrEE;
import org.w3c.domts.level1.core.nodesetnodevaluenomodificationallowederr;
import org.w3c.domts.level1.core.nodesetnodevaluenomodificationallowederrEE;
import org.w3c.domts.level1.core.processinginstructionsetdatanomodificationallowederrEE;
import org.w3c.domts.level1.core.textparseintolistofelements;
import org.w3c.domts.level1.core.textsplittextnomodificationallowederr;
import org.w3c.domts.level1.core.textsplittextnomodificationallowederrEE;
import org.w3c.domts.level2.core.elementsetattributenodens06;
import org.w3c.domts.level2.core.importNode11;
import org.w3c.domts.level2.core.prefix08;
import org.w3c.domts.level2.core.removeAttributeNS01;
import org.w3c.domts.level2.core.removeNamedItemNS03;
import org.w3c.domts.level2.core.setAttributeNS03;
import org.w3c.domts.level2.core.setAttributeNodeNS02;
import org.w3c.domts.level2.core.setNamedItemNS04;
import org.w3c.domts.level3.core.nodecomparedocumentposition26;
import org.w3c.domts.level3.core.nodecomparedocumentposition27;
import org.w3c.domts.level3.core.nodecomparedocumentposition28;
import org.w3c.domts.level3.core.nodecomparedocumentposition29;
import org.w3c.domts.level3.core.nodegettextcontent14;
import org.w3c.domts.level3.core.nodegettextcontent17;
import org.w3c.domts.level3.core.nodeinsertbefore15;
import org.w3c.domts.level3.core.noderemovechild13;
import org.w3c.domts.level3.core.noderemovechild14;
import org.w3c.domts.level3.core.noderemovechild15;
import org.w3c.domts.level3.core.noderemovechild31;

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
            new Signature[] {
                    new Signature(DocumentType.class, "getEntities"),
            },
            new Class<?>[] {
                    attrsetvaluenomodificationallowederrEE.class,
                    characterdataappenddatanomodificationallowederr.class,
                    characterdataappenddatanomodificationallowederrEE.class,
                    characterdatadeletedatanomodificationallowederr.class,
                    characterdatadeletedatanomodificationallowederrEE.class,
                    characterdatainsertdatanomodificationallowederr.class,
                    characterdatainsertdatanomodificationallowederrEE.class,
                    characterdatareplacedatanomodificationallowederr.class,
                    characterdatareplacedatanomodificationallowederrEE.class,
                    characterdatasetdatanomodificationallowederr.class,
                    characterdatasetdatanomodificationallowederrEE.class,
                    documentcreateentityreferenceknown.class,
                    elementremoveattributenodenomodificationallowederr.class,
                    elementremoveattributenodenomodificationallowederrEE.class,
                    elementremoveattributenomodificationallowederr.class,
                    elementremoveattributenomodificationallowederrEE.class,
                    elementsetattributenodenomodificationallowederr.class,
                    elementsetattributenodenomodificationallowederrEE.class,
                    elementsetattributenomodificationallowederrEE.class,
                    nodeappendchildnomodificationallowederr.class,
                    nodeinsertbeforenomodificationallowederr.class,
                    noderemovechildnomodificationallowederr.class,
                    noderemovechildnomodificationallowederrEE.class,
                    nodereplacechildnomodificationallowederr.class,
                    nodereplacechildnomodificationallowederrEE.class,
                    nodesetnodevaluenomodificationallowederr.class,
                    nodesetnodevaluenomodificationallowederrEE.class,
                    processinginstructionsetdatanomodificationallowederrEE.class,
                    textsplittextnomodificationallowederr.class,
                    textsplittextnomodificationallowederrEE.class,
                    attrremovechild1.class,
                    attrreplacechild1.class,
                    elementsetattributenodens06.class,
                    importNode11.class,
                    prefix08.class,
                    removeAttributeNS01.class,
                    removeNamedItemNS03.class,
                    setAttributeNS03.class,
                    setAttributeNodeNS02.class,
                    setNamedItemNS04.class,
                    nodecomparedocumentposition26.class,
                    nodecomparedocumentposition27.class,
                    nodecomparedocumentposition28.class,
                    nodecomparedocumentposition29.class,
                    nodegettextcontent17.class,
                    nodeinsertbefore15.class,
                    noderemovechild13.class,
                    noderemovechild14.class,
                    noderemovechild15.class,
                    attrsetvaluenomodificationallowederr.class,
                    textparseintolistofelements.class,
                    hc_textparseintolistofelements.class,
                    noderemovechild31.class,
                    nodegettextcontent14.class,
            });
    public static final DOMFeature NOTATIONS = new DOMFeature(
            new Signature[] {
                    new Signature(DocumentType.class, "getNotations"),
            },
            new Class<?>[0]);
    public static final DOMFeature TYPE_INFO = new DOMFeature(
            new Signature[] {
                    new Signature(Attr.class, "getSchemaTypeInfo"),
                    new Signature(Element.class, "getSchemaTypeInfo"),
            },
            new Class<?>[0]);
    public static final DOMFeature BASE_URI = new DOMFeature(
            new Signature[] {
                    new Signature(Node.class, "getBaseURI"),
            },
            new Class<?>[0]);
    
    private static final DOMFeature[] allFeatures = new DOMFeature[] {
        ENTITIES,
        NOTATIONS,
        TYPE_INFO,
        BASE_URI,
    };
    
    private final Signature[] signatures;
    private final Set<Class<?>> testClasses;
    
    private DOMFeature(Signature[] signatures, Class<?>[] testClasses) {
        this.signatures = signatures;
        this.testClasses = new HashSet<Class<?>>(Arrays.asList(testClasses));
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

    static void matchFeatures(Class<?> testClass, Set<DOMFeature> usedFeatures) {
        for (DOMFeature feature : allFeatures) {
            if (feature.testClasses.contains(testClass)) {
                usedFeatures.add(feature);
            }
        }
    }
}
