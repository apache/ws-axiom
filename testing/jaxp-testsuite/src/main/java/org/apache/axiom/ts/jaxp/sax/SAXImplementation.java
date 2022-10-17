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
package org.apache.axiom.ts.jaxp.sax;

import javax.xml.parsers.SAXParserFactory;

import org.apache.axiom.testing.multiton.Multiton;
import org.xml.sax.ext.LexicalHandler;

public abstract class SAXImplementation extends Multiton {
    public static final SAXImplementation XERCES =
            new SAXImplementation("xerces", true) {
                @Override
                public SAXParserFactory newSAXParserFactory() {
                    return new org.apache.xerces.jaxp.SAXParserFactoryImpl();
                }
            };

    public static final SAXImplementation CRIMSON =
            new SAXImplementation("crimson", false) {
                @Override
                public SAXParserFactory newSAXParserFactory() {
                    return new CrimsonSAXParserFactoryWrapper(
                            new org.apache.crimson.jaxp.SAXParserFactoryImpl());
                }
            };

    private final String name;
    private final boolean reportsExternalSubsetEntity;

    private SAXImplementation(String name, boolean reportsExternalSubsetEntity) {
        this.name = name;
        this.reportsExternalSubsetEntity = reportsExternalSubsetEntity;
    }

    public final String getName() {
        return name;
    }

    public abstract SAXParserFactory newSAXParserFactory();

    /**
     * Determine if this implementation correctly reports the boundaries of the external subset.
     *
     * @return {@code true} if the implementation signals the start of the external subset by
     *     calling {@link LexicalHandler#startEntity(String)} with argument {@code "[dtd]"}, {@code
     *     false} otherwise
     */
    public final boolean reportsExternalSubsetEntity() {
        return reportsExternalSubsetEntity;
    }
}
