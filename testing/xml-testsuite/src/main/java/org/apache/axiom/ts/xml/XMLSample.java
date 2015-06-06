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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axiom.testing.multiton.Instances;
import org.w3c.dom.Document;

public class XMLSample extends MessageSample {
    /**
     * A simple XML document without any particular features.
     */
    public static final XMLSample SIMPLE = new XMLSample("simple.xml");
    
    /**
     * An XML document that is larger than the input buffer of typical XML parsers.
     */
    public static final XMLSample LARGE = new XMLSample("large.xml");
    
    public static final XMLSample ENTITY_REFERENCE_NESTED = new XMLSample("entity-reference-nested.xml");
    
    private static final DocumentBuilder documentBuilder;
    
    static {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            documentBuilder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new Error(ex);
        }
    }
    
    private final String name;
    private XMLSampleProperties properties;
    
    protected XMLSample(MessageContent content, String name) {
        super(content);
        this.name = name;
    }
    
    private XMLSample(String relativeResourceName) {
        this(MessageContent.fromClasspath(XMLSample.class, relativeResourceName),
                relativeResourceName.substring(relativeResourceName.lastIndexOf('/')+1));
    }

    /**
     * Get the name of this message (for use in test case naming e.g.).
     * 
     * @return the name of this test message
     */
    public final String getName() {
        return name;
    }

    private synchronized XMLSampleProperties getProperties() {
        // The purpose of this code is to defer scanning the sample until it is necessary
        if (properties == null) {
            properties = new XMLSampleProperties(this);
        }
        return properties;
    }

    public final String getEncoding() {
        return getProperties().getEncoding();
    }

    public final boolean hasDTD() {
        return getProperties().hasDTD();
    }

    public final boolean hasExternalSubset() {
        return getProperties().hasExternalSubset();
    }

    public final boolean hasInternalSubset() {
        return getProperties().hasInternalSubset();
    }

    public final boolean hasEntityReferences() {
        return getProperties().hasEntityReferences();
    }

    public final Document getDocument() {
        try {
            InputStream in = getInputStream();
            try {
                return documentBuilder.parse(in);
            } finally {
                in.close();
            }
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }
    
    @Instances
    private static XMLSample[] instances() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(
                XMLSample.class.getResourceAsStream("bulk/filelist")));
        List<XMLSample> result = new ArrayList<XMLSample>(10);
        String name;
        while ((name = in.readLine()) != null) {
            result.add(new XMLSample("bulk/" + name));
        }
        in.close();
        return result.toArray(new XMLSample[result.size()]);
    }
}
