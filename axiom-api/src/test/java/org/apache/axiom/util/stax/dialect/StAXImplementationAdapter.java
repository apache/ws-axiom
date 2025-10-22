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
package org.apache.axiom.util.stax.dialect;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.apache.axiom.testing.multiton.AdapterType;
import org.apache.axiom.ts.jaxp.stax.StAXImplementation;

@AdapterType
public final class StAXImplementationAdapter {
    private final StAXImplementation impl;
    private StAXDialect dialect;

    public StAXImplementationAdapter(StAXImplementation impl) {
        this.impl = impl;
    }

    public String getName() {
        return impl.getName();
    }

    public XMLInputFactory newNormalizedXMLInputFactory() {
        XMLInputFactory factory = impl.newXMLInputFactory();
        if (dialect == null) {
            dialect = StAXDialectDetector.getDialect(factory.getClass());
        }
        return dialect.normalize(factory);
    }

    @SuppressWarnings("deprecation")
    public XMLOutputFactory newNormalizedXMLOutputFactory() {
        XMLOutputFactory factory = impl.newXMLOutputFactory();
        if (dialect == null) {
            dialect = StAXDialectDetector.getDialect(factory.getClass());
        }
        return dialect.normalize(factory);
    }

    public StAXDialect getDialect() {
        return dialect;
    }
}
