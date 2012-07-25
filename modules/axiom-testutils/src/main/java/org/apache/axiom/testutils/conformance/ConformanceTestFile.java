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
package org.apache.axiom.testutils.conformance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.codehaus.stax2.DTDInfo;

public final class ConformanceTestFile {
    private static ConformanceTestFile[] instances;
    
    private final String resourceName;
    private final String shortName;
    private final boolean hasDTD;
    private final boolean hasExternalSubset;
    private final boolean hasInternalSubset;
    private final boolean hasEntityReferences;
    
    private ConformanceTestFile(String resourceName, String shortName, boolean hasDTD,
            boolean hasExternalSubset, boolean hasInternalSubset, boolean hasEntityReferences) {
        this.resourceName = resourceName;
        this.shortName = shortName;
        this.hasDTD = hasDTD;
        this.hasExternalSubset = hasExternalSubset;
        this.hasInternalSubset = hasInternalSubset;
        this.hasEntityReferences = hasEntityReferences;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getShortName() {
        return shortName;
    }

    public boolean hasDTD() {
        return hasDTD;
    }

    public boolean hasExternalSubset() {
        return hasExternalSubset;
    }

    public boolean hasInternalSubset() {
        return hasInternalSubset;
    }

    public boolean hasEntityReferences() {
        return hasEntityReferences;
    }

    public InputStream getAsStream() {
        return ConformanceTestFile.class.getClassLoader().getResourceAsStream(resourceName);
    }
    
    public URL getUrl() {
        return ConformanceTestFile.class.getClassLoader().getResource(resourceName);
    }
    
    public static synchronized ConformanceTestFile[] getConformanceTestFiles() {
        if (instances == null) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        ConformanceTestFile.class.getResourceAsStream("filelist")));
                List result = new ArrayList(10);
                XMLInputFactory inputFactory = XMLInputFactory.newInstance();
                inputFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
                String name;
                while ((name = in.readLine()) != null) {
                    String resourceName = "org/apache/axiom/testutils/conformance/" + name;
                    boolean hasDTD = false;
                    boolean hasExternalSubset = false;
                    boolean hasInternalSubset = false;
                    boolean hasEntityReferences = false;
                    try {
                        XMLStreamReader reader = inputFactory.createXMLStreamReader(new StreamSource(
                                ConformanceTestFile.class.getResource(name).toString()));
                        while (reader.hasNext()) {
                            switch (reader.next()) {
                                case XMLStreamReader.DTD:
                                    hasDTD = true;
                                    hasInternalSubset = reader.getText().length() > 0;
                                    hasExternalSubset = ((DTDInfo)reader).getDTDSystemId() != null;
                                    break;
                                case XMLStreamReader.ENTITY_REFERENCE:
                                    hasEntityReferences = true;
                                    break;
                            }
                        }
                        reader.close();
                    } catch (XMLStreamException ex) {
                        throw new Error("Unable to parse " + resourceName);
                    }
                    result.add(new ConformanceTestFile(resourceName, name, hasDTD, hasExternalSubset, hasInternalSubset, hasEntityReferences));
                }
                in.close();
                return (ConformanceTestFile[])result.toArray(new ConformanceTestFile[result.size()]);
            } catch (IOException ex) {
                throw new Error("Unable to load file list: " + ex.getMessage());
            }
        }
        return (ConformanceTestFile[])instances.clone();
    }
}
