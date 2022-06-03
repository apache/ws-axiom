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
package org.apache.axiom.ts.om.builder;

import java.io.IOException;

import org.apache.axiom.util.sax.AbstractXMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

final class DummyXMLReader extends AbstractXMLReader {
    private boolean parsed;

    @Override
    public void parse(InputSource input) throws IOException, SAXException {
        parse();
    }

    @Override
    public void parse(String systemId) throws IOException, SAXException {
        parse();
    }

    private void parse() throws SAXException {
        parsed = true;
        contentHandler.startDocument();
        contentHandler.startElement("", "test", "test", new AttributesImpl());
        contentHandler.endElement("", "test", "test");
        contentHandler.endDocument();
    }

    public boolean isParsed() {
        return parsed;
    }
}
