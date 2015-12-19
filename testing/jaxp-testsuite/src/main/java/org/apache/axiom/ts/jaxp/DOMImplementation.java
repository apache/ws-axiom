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
package org.apache.axiom.ts.jaxp;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.testing.multiton.Multiton;

public abstract class DOMImplementation extends Multiton {
    public static final DOMImplementation XERCES = new DOMImplementation("xerces", true, true) {
        @Override
        public DocumentBuilderFactory newDocumentBuilderFactory() {
            return new org.apache.xerces.jaxp.DocumentBuilderFactoryImpl();
        }
    };
    
    public static final DOMImplementation CRIMSON = new DOMImplementation("crimson", false, false) {
        @Override
        public DocumentBuilderFactory newDocumentBuilderFactory() {
            return new org.apache.crimson.jaxp.DocumentBuilderFactoryImpl();
        }
    };
    
    private final String name;
    private final boolean dom3;
    private final boolean internalSubset;

    private DOMImplementation(String name, boolean dom3, boolean internalSubset) {
        this.name = name;
        this.dom3 = dom3;
        this.internalSubset = internalSubset;
    }

    public final String getName() {
        return name;
    }
    
    public final boolean isDOM3() {
        return dom3;
    }

    public final boolean supportsGetInternalSubset() {
        return internalSubset;
    }
    
    public abstract DocumentBuilderFactory newDocumentBuilderFactory();
}
