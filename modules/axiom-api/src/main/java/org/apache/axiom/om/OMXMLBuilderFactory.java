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
package org.apache.axiom.om;

import java.io.InputStream;
import java.io.Reader;

import javax.xml.stream.XMLStreamReader;

/**
 * Provides static factory methods to create various kinds of object model builders from different
 * types of input sources. The methods defined by this class are the starting point to parse XML
 * documents into Axiom trees.
 */
public class OMXMLBuilderFactory {
    private OMXMLBuilderFactory() {}
    
    /**
     * Create an object model builder for plain XML that pulls events from a StAX stream reader.
     * 
     * @param parser
     *            the stream reader to read the XML data from
     * @return the builder
     */
    public static OMXMLParserWrapper createStAXOMBuilder(XMLStreamReader parser) {
        return OMAbstractFactory.getMetaFactory().createStAXOMBuilder(parser);
    }
    
    /**
     * Create an object model builder that reads a plain XML document from the provided input
     * stream.
     * 
     * @param in
     *            the input stream representing the XML document
     * @return the builder
     */
    public static OMXMLParserWrapper createOMBuilder(InputStream in) {
        return OMAbstractFactory.getMetaFactory().createOMBuilder(in);
    }
    
    /**
     * Create an object model builder that reads a plain XML document from the provided character
     * stream.
     * 
     * @param in
     *            the character stream representing the XML document
     * @return the builder
     */
    public static OMXMLParserWrapper createOMBuilder(Reader in) {
        return OMAbstractFactory.getMetaFactory().createOMBuilder(in);
    }
}
