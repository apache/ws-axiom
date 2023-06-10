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
package org.apache.axiom.truth.xml;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.ctc.wstx.api.WstxInputProperties;
import com.ctc.wstx.stax.WstxInputFactory;

abstract class Parsable extends XMLStreamReaderProvider {

    @Override
    final XMLStreamReader getXMLStreamReader(boolean expandEntityReferences)
            throws XMLStreamException {
        WstxInputFactory factory = new WstxInputFactory();
        factory.setProperty(
                XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,
                Boolean.valueOf(expandEntityReferences));
        factory.setProperty(WstxInputFactory.P_AUTO_CLOSE_INPUT, Boolean.TRUE);
        factory.setProperty(WstxInputFactory.P_REPORT_PROLOG_WHITESPACE, Boolean.TRUE);
        factory.setProperty(WstxInputFactory.P_REPORT_CDATA, Boolean.TRUE);
        factory.setProperty(WstxInputProperties.P_MIN_TEXT_SEGMENT, Integer.MAX_VALUE);
        return createXMLStreamReader(factory);
    }

    abstract XMLStreamReader createXMLStreamReader(XMLInputFactory factory)
            throws XMLStreamException;
}
