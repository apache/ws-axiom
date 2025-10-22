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

class UnknownStAXDialect implements StAXDialect {
    public static final UnknownStAXDialect INSTANCE = new UnknownStAXDialect();

    @Override
    public String getName() {
        return "Unknown";
    }

    @Override
    public XMLInputFactory enableCDataReporting(XMLInputFactory factory) {
        // This is in principle only the prerequisite; let's hope that it is sufficient
        factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
        return factory;
    }

    @Override
    public XMLInputFactory disallowDoctypeDecl(XMLInputFactory factory) {
        return StAXDialectUtils.disallowDoctypeDecl(factory);
    }

    @Override
    public XMLInputFactory makeThreadSafe(XMLInputFactory factory) {
        // Cross fingers and assume that the factory is already thread safe
        return factory;
    }

    @Override
    public XMLOutputFactory makeThreadSafe(XMLOutputFactory factory) {
        // Cross fingers and assume that the factory is already thread safe
        return factory;
    }

    @Override
    public XMLInputFactory normalize(XMLInputFactory factory) {
        return factory;
    }

    @Override
    public XMLOutputFactory normalize(XMLOutputFactory factory) {
        return factory;
    }
}
