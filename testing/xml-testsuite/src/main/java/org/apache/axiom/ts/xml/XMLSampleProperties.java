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
package org.apache.axiom.ts.xml;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.codehaus.stax2.DTDInfo;

import com.ctc.wstx.stax.WstxInputFactory;

final class XMLSampleProperties {
    private static final XMLInputFactory inputFactory;

    static {
        // We make use of Woodstox' DTDInfo interface here, but we want to be able to use system
        // properties
        // to specify the StAX implementation to be used by the tests. Therefore we need to create
        // an instance of the Woodstox InputFactory implementation directly.
        inputFactory = new WstxInputFactory();
        inputFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
    }

    private final String encoding;
    private final boolean hasDTD;
    private final boolean hasExternalSubset;
    private final boolean hasInternalSubset;
    private final boolean hasEntityReferences;

    XMLSampleProperties(XMLSample sample) {
        boolean hasDTD = false;
        boolean hasExternalSubset = false;
        boolean hasInternalSubset = false;
        boolean hasEntityReferences = false;
        try {
            XMLStreamReader reader =
                    inputFactory.createXMLStreamReader(
                            new StreamSource(sample.getUrl().toString()));
            encoding = reader.getEncoding();
            while (reader.hasNext()) {
                switch (reader.next()) {
                    case XMLStreamReader.DTD:
                        hasDTD = true;
                        hasInternalSubset = reader.getText().length() > 0;
                        hasExternalSubset = ((DTDInfo) reader).getDTDSystemId() != null;
                        break;
                    case XMLStreamReader.ENTITY_REFERENCE:
                        hasEntityReferences = true;
                        break;
                }
            }
            reader.close();
        } catch (XMLStreamException ex) {
            throw new Error("Unable to parse " + sample.getUrl(), ex);
        }
        this.hasDTD = hasDTD;
        this.hasExternalSubset = hasExternalSubset;
        this.hasInternalSubset = hasInternalSubset;
        this.hasEntityReferences = hasEntityReferences;
    }

    String getEncoding() {
        return encoding;
    }

    boolean hasDTD() {
        return hasDTD;
    }

    boolean hasExternalSubset() {
        return hasExternalSubset;
    }

    boolean hasInternalSubset() {
        return hasInternalSubset;
    }

    boolean hasEntityReferences() {
        return hasEntityReferences;
    }
}
