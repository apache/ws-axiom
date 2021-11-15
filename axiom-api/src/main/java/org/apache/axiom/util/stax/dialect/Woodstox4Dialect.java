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
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

class Woodstox4Dialect extends AbstractStAXDialect {
    public static final Woodstox4Dialect INSTANCE = new Woodstox4Dialect();

    private Woodstox4Dialect() {}

    @Override
    public String getName() {
        return "Woodstox";
    }

    @Override
    public XMLInputFactory enableCDataReporting(XMLInputFactory factory) {
        factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
        factory.setProperty("http://java.sun.com/xml/stream/properties/report-cdata-event", Boolean.TRUE);
        factory.setProperty("com.ctc.wstx.minTextSegment", Integer.MAX_VALUE);
        return factory;
    }

    @Override
    public XMLInputFactory disallowDoctypeDecl(XMLInputFactory factory) {
        return StAXDialectUtils.disallowDoctypeDecl(factory);
    }

    @Override
    public XMLInputFactory makeThreadSafe(XMLInputFactory factory) {
        // Woodstox' factories are designed to be thread safe
        return factory;
    }

    @Override
    public XMLOutputFactory makeThreadSafe(XMLOutputFactory factory) {
        // Woodstox' factories are designed to be thread safe
        return factory;
    }

    @Override
    public XMLStreamReader normalize(XMLStreamReader reader) {
        return new Woodstox4StreamReaderWrapper(reader);
    }

    @Override
    public XMLStreamWriter normalize(XMLStreamWriter writer) {
        return new Woodstox4StreamWriterWrapper(writer);
    }

    @Override
    public XMLInputFactory normalize(XMLInputFactory factory) {
        // Woodstox 3 used to report whitespace in prolog, but this is no longer the case by default
        // in Woodstox 4. The following property changes that behavior.
        factory.setProperty("org.codehaus.stax2.reportPrologWhitespace", Boolean.TRUE);
        return new NormalizingXMLInputFactoryWrapper(factory, this);
    }

    @Override
    public XMLOutputFactory normalize(XMLOutputFactory factory) {
        
        // TODO: quick fix for AXIOM-415; need a proper solution
        factory.setProperty("com.ctc.wstx.outputFixContent", Boolean.TRUE);
        
        return new Woodstox4OutputFactoryWrapper(factory, this);
    }
}
