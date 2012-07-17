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
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public final class ConformanceTestFile {
    private static ConformanceTestFile[] instances;
    
    private final String resourceName;
    private final String shortName;
    private final boolean hasDTD;
    private final boolean hasEntityReferences;
    
    private ConformanceTestFile(String resourceName, String shortName, boolean hasDTD, boolean hasEntityReferences) {
        this.resourceName = resourceName;
        this.shortName = shortName;
        this.hasDTD = hasDTD;
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

    public boolean hasEntityReferences() {
        return hasEntityReferences;
    }

    public InputStream getAsStream() {
        InputStream in = ConformanceTestFile.class.getClassLoader().getResourceAsStream(resourceName);
        if (in == null) {
            throw new Error("The test resource " + resourceName + " could not be found");
        }
        return in;
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
                    boolean hasEntityReferences = false;
                    InputStream in2 = ConformanceTestFile.class.getResourceAsStream(name);
                    try {
                        XMLStreamReader reader = inputFactory.createXMLStreamReader(in2);
                        while (reader.hasNext()) {
                            switch (reader.next()) {
                                case XMLStreamReader.DTD:
                                    hasDTD = true;
                                    break;
                                case XMLStreamReader.ENTITY_REFERENCE:
                                    hasEntityReferences = true;
                                    break;
                            }
                        }
                        reader.close();
                    } catch (XMLStreamException ex) {
                        throw new Error("Unable to parse " + resourceName);
                    } finally {
                        in2.close();
                    }
                    result.add(new ConformanceTestFile(resourceName, name, hasDTD, hasEntityReferences));
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
