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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.ext.stax.DTDReader;

final class XLXP1DTDReaderImpl implements DTDReader {
    private final XMLStreamReader reader;
    private String rootName;
    private String publicId;
    private String systemId;

    XLXP1DTDReaderImpl(XMLStreamReader reader) {
        this.reader = reader;
    }

    private void parse() {
        if (rootName == null) {
            try {
                Scanner scanner = new Scanner((String)reader.getProperty("javax.xml.stream.dtd.declaration"));
                scanner.expect("<!DOCTYPE");
                scanner.skipSpace();
                rootName = scanner.getName();
                scanner.skipSpace();
                switch (scanner.peek()) {
                    case 'S':
                        scanner.expect("SYSTEM");
                        scanner.skipSpace();
                        systemId = scanner.getQuotedString();
                        break;
                    case 'P':
                        scanner.expect("PUBLIC");
                        scanner.skipSpace();
                        publicId = scanner.getQuotedString();
                        scanner.skipSpace();
                        systemId = scanner.getQuotedString();
                }
            } catch (XMLStreamException ex) {
                throw new RuntimeException("Unable to parse DOCTYPE declaration", ex);
            }
        }
    }
    
    public String getRootName() {
        parse();
        return rootName;
    }

    public String getPublicId() {
        parse();
        return publicId;
    }

    public String getSystemId() {
        parse();
        return systemId;
    }
}
