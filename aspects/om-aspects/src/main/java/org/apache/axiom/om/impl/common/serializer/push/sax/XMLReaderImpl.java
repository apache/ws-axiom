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
package org.apache.axiom.om.impl.common.serializer.push.sax;

import java.io.IOException;

import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.AxiomContainer;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.util.sax.AbstractXMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLReaderImpl extends AbstractXMLReader {
    private final AxiomContainer root;
    private final boolean cache;

    public XMLReaderImpl(AxiomContainer root, boolean cache) {
        this.root = root;
        this.cache = cache;
    }

    public void parse(InputSource input) throws IOException, SAXException {
        parse();
    }

    public void parse(String systemId) throws IOException, SAXException {
        parse();
    }
    
    private void parse() throws SAXException {
        try {
            root.internalSerialize(new SAXSerializer(root, contentHandler, lexicalHandler),
                    new OMOutputFormat(), cache);
        } catch (OutputException ex) {
            throw (SAXException)ex.getCause();
        }
    }
}
