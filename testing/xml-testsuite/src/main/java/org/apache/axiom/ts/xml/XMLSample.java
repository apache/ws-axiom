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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.axiom.testing.multiton.Instances;
import org.apache.axiom.testing.multiton.Multiton;

public final class XMLSample extends Multiton {
    /**
     * A simple XML document without any particular features.
     */
    public static final XMLSample SIMPLE = new XMLSample("simple.xml");
    
    /**
     * An XML document that is larger than the input buffer of typical XML parsers.
     */
    public static final XMLSample LARGE = new XMLSample("large.xml");
    
    private final String resourceName;
    private final String shortName;
    private XMLSampleProperties properties;
    
    private XMLSample(String relativeResourceName) {
        resourceName = "org/apache/axiom/ts/xml/" + relativeResourceName;
        shortName = resourceName.substring(resourceName.lastIndexOf('/')+1);
    }

    public String getShortName() {
        return shortName;
    }

    private synchronized XMLSampleProperties getProperties() {
        // The purpose of this code is to defer scanning the sample until it is necessary
        if (properties == null) {
            properties = new XMLSampleProperties(this);
        }
        return properties;
    }

    public String getEncoding() {
        return getProperties().getEncoding();
    }

    public boolean hasDTD() {
        return getProperties().hasDTD();
    }

    public boolean hasExternalSubset() {
        return getProperties().hasExternalSubset();
    }

    public boolean hasInternalSubset() {
        return getProperties().hasInternalSubset();
    }

    public boolean hasEntityReferences() {
        return getProperties().hasEntityReferences();
    }

    public InputStream getAsStream() {
        return XMLSample.class.getClassLoader().getResourceAsStream(resourceName);
    }
    
    public URL getUrl() {
        return XMLSample.class.getClassLoader().getResource(resourceName);
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
