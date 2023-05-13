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
package org.apache.axiom.ts.dom.w3c;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axiom.ts.dom.DocumentBuilderFactoryFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.domts.DOMTestDocumentBuilderFactory;
import org.w3c.domts.DOMTestIncompatibleException;
import org.w3c.domts.DOMTestLoadException;
import org.w3c.domts.DocumentBuilderSetting;

final class DOMTestDocumentBuilderFactoryImpl extends DOMTestDocumentBuilderFactory {
    private final DocumentBuilderFactoryFactory dbff;
    private final DocumentBuilderFactory dbf;
    private final DocumentBuilder builder;

    public DOMTestDocumentBuilderFactoryImpl(
            DocumentBuilderFactoryFactory dbff, DocumentBuilderSetting[] settings)
            throws DOMTestIncompatibleException {
        super(settings);
        this.dbff = dbff;
        dbf = dbff.newInstance();
        for (DocumentBuilderSetting setting : settings) {
            setting.applySetting(dbf);
        }
        try {
            builder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new DOMTestIncompatibleException(ex, null);
        }
    }

    @Override
    public DOMTestDocumentBuilderFactory newInstance(DocumentBuilderSetting[] settings)
            throws DOMTestIncompatibleException {
        return new DOMTestDocumentBuilderFactoryImpl(dbff, mergeSettings(settings));
    }

    @Override
    public DOMImplementation getDOMImplementation() {
        return builder.getDOMImplementation();
    }

    @Override
    public boolean hasFeature(String feature, String version) {
        return builder.getDOMImplementation().hasFeature(feature, version);
    }

    @Override
    public Document load(URL url) throws DOMTestLoadException {
        try {
            return builder.parse(url.openStream(), url.toString());
        } catch (Exception ex) {
            throw new DOMTestLoadException(ex);
        }
    }

    @Override
    public boolean isCoalescing() {
        return dbf.isCoalescing();
    }

    @Override
    public boolean isExpandEntityReferences() {
        return dbf.isExpandEntityReferences();
    }

    @Override
    public boolean isIgnoringElementContentWhitespace() {
        return dbf.isIgnoringElementContentWhitespace();
    }

    @Override
    public boolean isNamespaceAware() {
        return dbf.isNamespaceAware();
    }

    @Override
    public boolean isValidating() {
        return dbf.isValidating();
    }
}
