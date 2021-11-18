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

package org.apache.axiom.core.stream.sax.input;

import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.XmlReader;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;

import javax.xml.transform.sax.SAXSource;

public final class SAXReader implements XmlReader {
    private final XmlHandler handler;
    private final boolean expandEntityReferences;
    private final SAXSource source;
    
    SAXReader(XmlHandler handler, SAXSource source, boolean expandEntityReferences) {
        this.handler = handler;
        this.expandEntityReferences = expandEntityReferences;
        this.source = source;
    }
    
    @Override
    public boolean proceed() throws StreamException {
        XMLReader reader = source.getXMLReader();
        XmlHandlerContentHandler contentHandler = new XmlHandlerContentHandler(handler, expandEntityReferences);
        reader.setContentHandler(contentHandler);
        reader.setDTDHandler(contentHandler);
        try {
            reader.setProperty("http://xml.org/sax/properties/lexical-handler", contentHandler);
        } catch (SAXException ex) {
            // Ignore
        }
        try {
            reader.setProperty("http://xml.org/sax/properties/declaration-handler", contentHandler);
        } catch (SAXException ex) {
            // Ignore
        }
        try {
            reader.parse(source.getInputSource());
        } catch (IOException ex) {
            throw new StreamException(ex);
        } catch (SAXException ex) {
            throw new StreamException(ex);
        }
        return true;
    }

    @Override
    public void dispose() {
    }
}
