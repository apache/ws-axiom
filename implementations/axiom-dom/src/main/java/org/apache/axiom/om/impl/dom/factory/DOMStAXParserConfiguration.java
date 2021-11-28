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
package org.apache.axiom.om.impl.dom.factory;

import javax.xml.stream.XMLInputFactory;

import org.apache.axiom.om.util.StAXParserConfiguration;
import org.apache.axiom.util.stax.dialect.StAXDialect;

final class DOMStAXParserConfiguration implements StAXParserConfiguration {
    private final boolean coalescing;
    private final boolean expandEntityReferences;
    
    public DOMStAXParserConfiguration(boolean coalescing, boolean expandEntityReferences) {
        this.coalescing = coalescing;
        this.expandEntityReferences = expandEntityReferences;
    }

    @Override
    public XMLInputFactory configure(XMLInputFactory factory, StAXDialect dialect) {
        if (!coalescing) {
            factory = StAXParserConfiguration.PRESERVE_CDATA_SECTIONS.configure(factory, dialect);
        }
        factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.valueOf(expandEntityReferences));
        return factory;
    }

    @Override
    public int hashCode() {
        return (coalescing ? 1 : 0) | (expandEntityReferences ? 2 : 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof DOMStAXParserConfiguration) {
            DOMStAXParserConfiguration other = (DOMStAXParserConfiguration)obj;
            return other.coalescing == coalescing && other.expandEntityReferences == expandEntityReferences;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "DOM(coalescing=" + coalescing + ",expandEntityReferences=" + expandEntityReferences + ")";
    }
}
