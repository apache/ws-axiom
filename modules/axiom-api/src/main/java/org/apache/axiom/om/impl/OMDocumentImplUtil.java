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

package org.apache.axiom.om.impl;

import java.util.Iterator;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMDocument;

/**
 * Utility class with default implementations for some of the methods defined by the
 * {@link OMDocument} interface.
 */
public class OMDocumentImplUtil {
    private OMDocumentImplUtil() {}
    
    public static void internalSerialize(OMDocument document, XMLStreamWriter writer2,
            boolean cache, boolean includeXMLDeclaration) throws XMLStreamException {
        
        MTOMXMLStreamWriter writer = (MTOMXMLStreamWriter) writer2;
        if (includeXMLDeclaration) {
            //Check whether the OMOutput char encoding and OMDocument char
            //encoding matches, if not use char encoding of OMOutput
            String outputCharEncoding = writer.getCharSetEncoding();
            if (outputCharEncoding == null || "".equals(outputCharEncoding)) {
                writer.getXmlStreamWriter().writeStartDocument(document.getCharsetEncoding(),
                                                               document.getXMLVersion());
            } else {
                writer.getXmlStreamWriter().writeStartDocument(outputCharEncoding,
                                                               document.getXMLVersion());
            }
        }

        Iterator children = document.getChildren();

        if (cache) {
            while (children.hasNext()) {
                OMNodeEx omNode = (OMNodeEx) children.next();
                omNode.internalSerialize(writer);
            }
        } else {
            while (children.hasNext()) {
                OMNodeEx omNode = (OMNodeEx) children.next();
                omNode.internalSerializeAndConsume(writer);
            }
        }
    }
}
