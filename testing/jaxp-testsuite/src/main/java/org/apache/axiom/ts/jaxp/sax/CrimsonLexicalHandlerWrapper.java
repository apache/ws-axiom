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

import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

final class CrimsonLexicalHandlerWrapper implements LexicalHandler {
    private final LexicalHandler parent;
    
    CrimsonLexicalHandlerWrapper(LexicalHandler parent) {
        this.parent = parent;
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        parent.startDTD(name, publicId, systemId);
    }

    @Override
    public void endDTD() throws SAXException {
        parent.endDTD();
    }

    @Override
    public void startEntity(String name) throws SAXException {
        if (!name.equals("lt")) {
            parent.startEntity(name);
        }
    }

    @Override
    public void endEntity(String name) throws SAXException {
        if (!name.equals("lt")) {
            parent.endEntity(name);
        }
    }

    @Override
    public void startCDATA() throws SAXException {
        parent.startCDATA();
    }

    @Override
    public void endCDATA() throws SAXException {
        parent.endCDATA();
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        parent.comment(ch, start, length);
    }
}
