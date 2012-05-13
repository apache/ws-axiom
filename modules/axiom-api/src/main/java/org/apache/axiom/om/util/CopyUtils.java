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
package org.apache.axiom.om.util;

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.soap.SOAPCloneOptions;
import org.apache.axiom.soap.SOAPEnvelope;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import java.util.Iterator;

/**
 * CopyUtils provides static utility methods that are useful for creating a copy of 
 * an OM/SOAPEnvelope tree.  
 * During the expansion, the Source tree retains its shape
 * (OMSourcedElement nodes are not expanded).  
 * The Target tree has nodes that retain the class identity of the source node.  For 
 * example, a SOAPFault in the source tree will have a SOAPFault in the target tree.
 */
public class CopyUtils {
    /**
     * Private Constructor
     */
    private CopyUtils() {
    }


    /**
     * Creates a copy of the source envelope.
     * If there are OMSourcedElements in the source tree, 
     * similar MSourcedElements are used in the target tree.
     *
     * @param sourceEnv
     * @return targetEnv
     */
    public static SOAPEnvelope copy(SOAPEnvelope sourceEnv) {
        SOAPCloneOptions options = new SOAPCloneOptions();
        options.setFetchDataHandlers(true);
        options.setPreserveModel(true);
        options.setCopyOMDataSources(true);
        return (SOAPEnvelope)sourceEnv.cloneOMElement(options);
    }

    /**
     * Simple utility that takes an XMLStreamReader and writes it
     * to an XMLStreamWriter
     * @param reader
     * @param writer
     * @throws XMLStreamException
     */
    public static void reader2writer(XMLStreamReader reader, 
                                     XMLStreamWriter writer)
    throws XMLStreamException {
        StAXOMBuilder builder = new StAXOMBuilder(reader);
        builder.releaseParserOnClose(true);
        try {
            OMDocument omDocument = builder.getDocument();
            Iterator it = omDocument.getChildren();
            while (it.hasNext()) {
                OMNode omNode = (OMNode) it.next();
                omNode.serializeAndConsume(writer);
            }
        } finally {
            builder.close();
        }
    }
}
