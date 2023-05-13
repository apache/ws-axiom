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
package org.apache.axiom.ts.dom.w3c;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Attr;
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

    public static final DOMFeature ENTITIES =
            new DOMFeature(
                    new Signature[] {
                        new Signature(DocumentType.class, "getEntities"),
                    },
                    new String[] {
                        "org.w3c.domts.level1.core.attrremovechild1",
                        "org.w3c.domts.level1.core.attrreplacechild1",
                        "org.w3c.domts.level1.core.attrsetvaluenomodificationallowederr",
                        "org.w3c.domts.level1.core.attrsetvaluenomodificationallowederrEE",
                        "org.w3c.domts.level1.core.characterdataappenddatanomodificationallowederr",
                        "org.w3c.domts.level1.core.characterdataappenddatanomodificationallowederrEE",
                        "org.w3c.domts.level1.core.characterdatadeletedatanomodificationallowederr",
                        "org.w3c.domts.level1.core.characterdatadeletedatanomodificationallowederrEE",
                        "org.w3c.domts.level1.core.characterdatainsertdatanomodificationallowederr",
                        "org.w3c.domts.level1.core.characterdatainsertdatanomodificationallowederrEE",
                        "org.w3c.domts.level1.core.characterdatareplacedatanomodificationallowederr",
                        "org.w3c.domts.level1.core.characterdatareplacedatanomodificationallowederrEE",
                        "org.w3c.domts.level1.core.characterdatasetdatanomodificationallowederr",
                        "org.w3c.domts.level1.core.characterdatasetdatanomodificationallowederrEE",
                        "org.w3c.domts.level1.core.documentcreateentityreferenceknown",
                        "org.w3c.domts.level1.core.elementremoveattributenodenomodificationallowederr",
                        "org.w3c.domts.level1.core.elementremoveattributenodenomodificationallowederrEE",
                        "org.w3c.domts.level1.core.elementremoveattributenomodificationallowederr",
                        "org.w3c.domts.level1.core.elementremoveattributenomodificationallowederrEE",
                        "org.w3c.domts.level1.core.elementsetattributenodenomodificationallowederr",
                        "org.w3c.domts.level1.core.elementsetattributenodenomodificationallowederrEE",
                        "org.w3c.domts.level1.core.elementsetattributenomodificationallowederrEE",
                        "org.w3c.domts.level1.core.hc_textparseintolistofelements",
                        "org.w3c.domts.level1.core.nodeappendchildnomodificationallowederr",
                        "org.w3c.domts.level1.core.nodeinsertbeforenomodificationallowederr",
                        "org.w3c.domts.level1.core.noderemovechildnomodificationallowederr",
                        "org.w3c.domts.level1.core.noderemovechildnomodificationallowederrEE",
                        "org.w3c.domts.level1.core.nodereplacechildnomodificationallowederr",
                        "org.w3c.domts.level1.core.nodereplacechildnomodificationallowederrEE",
                        "org.w3c.domts.level1.core.nodesetnodevaluenomodificationallowederr",
                        "org.w3c.domts.level1.core.nodesetnodevaluenomodificationallowederrEE",
                        "org.w3c.domts.level1.core.processinginstructionsetdatanomodificationallowederrEE",
                        "org.w3c.domts.level1.core.textparseintolistofelements",
                        "org.w3c.domts.level1.core.textsplittextnomodificationallowederr",
                        "org.w3c.domts.level1.core.textsplittextnomodificationallowederrEE",
                        "org.w3c.domts.level2.core.elementsetattributenodens06",
                        "org.w3c.domts.level2.core.importNode11",
                        "org.w3c.domts.level2.core.prefix08",
                        "org.w3c.domts.level2.core.removeAttributeNS01",
                        "org.w3c.domts.level2.core.removeNamedItemNS03",
                        "org.w3c.domts.level2.core.setAttributeNS03",
                        "org.w3c.domts.level2.core.setAttributeNodeNS02",
                        "org.w3c.domts.level2.core.setNamedItemNS04",
                        "org.w3c.domts.level3.core.nodecomparedocumentposition26",
                        "org.w3c.domts.level3.core.nodecomparedocumentposition27",
                        "org.w3c.domts.level3.core.nodecomparedocumentposition28",
                        "org.w3c.domts.level3.core.nodecomparedocumentposition29",
                        "org.w3c.domts.level3.core.nodegettextcontent14",
                        "org.w3c.domts.level3.core.nodegettextcontent17",
                        "org.w3c.domts.level3.core.nodeinsertbefore15",
                        "org.w3c.domts.level3.core.noderemovechild13",
                        "org.w3c.domts.level3.core.noderemovechild14",
                        "org.w3c.domts.level3.core.noderemovechild15",
                        "org.w3c.domts.level3.core.noderemovechild31",
                    });
    public static final DOMFeature NOTATIONS =
            new DOMFeature(
                    new Signature[] {
                        new Signature(DocumentType.class, "getNotations"),
                    },
                    new String[0]);
    public static final DOMFeature TYPE_INFO =
            new DOMFeature(
                    new Signature[] {
                        new Signature(Attr.class, "getSchemaTypeInfo"),
                        new Signature(Element.class, "getSchemaTypeInfo"),
                    },
                    new String[0]);
    public static final DOMFeature BASE_URI =
            new DOMFeature(
                    new Signature[] {
                        new Signature(Node.class, "getBaseURI"),
                    },
                    new String[0]);

    private static final DOMFeature[] allFeatures =
            new DOMFeature[] {
                ENTITIES, NOTATIONS, TYPE_INFO, BASE_URI,
            };

    private final Signature[] signatures;
    private final Set<String> testClasses;

    private DOMFeature(Signature[] signatures, String[] testClasses) {
        this.signatures = signatures;
        this.testClasses = new HashSet<>(Arrays.asList(testClasses));
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
                if (!usedFeatures.contains(feature)
                        && feature.matches(Class.forName(clazz.replace('/', '.')), method)) {
                    usedFeatures.add(feature);
                }
            } catch (ClassNotFoundException ex) {
                // Ignore
            }
        }
    }

    static void matchFeatures(Class<?> testClass, Set<DOMFeature> usedFeatures) {
        for (DOMFeature feature : allFeatures) {
            if (feature.testClasses.contains(testClass.getName())) {
                usedFeatures.add(feature);
            }
        }
    }
}
