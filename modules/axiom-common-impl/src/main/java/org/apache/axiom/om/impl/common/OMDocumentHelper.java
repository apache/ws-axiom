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

package org.apache.axiom.om.impl.common;

import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.impl.common.serializer.OutputException;
import org.apache.axiom.om.impl.common.serializer.StAXSerializer;

/**
 * Utility class with default implementations for some of the methods defined by the
 * {@link OMDocument} interface.
 */
public class OMDocumentHelper {
    private OMDocumentHelper() {}
    
    public static void internalSerialize(IDocument document, StAXSerializer serializer,
            boolean cache, boolean includeXMLDeclaration) throws XMLStreamException, OutputException {
        
        MTOMXMLStreamWriter writer = (MTOMXMLStreamWriter)serializer.getWriter();
        if (includeXMLDeclaration) {
            //Check whether the OMOutput char encoding and OMDocument char
            //encoding matches, if not use char encoding of OMOutput
            String encoding = writer.getCharSetEncoding();
            if (encoding == null || "".equals(encoding)) {
                encoding = document.getCharsetEncoding();
            }
            String version = document.getXMLVersion();
            if (version == null) {
                version = "1.0";
            }
            if (encoding == null) {
                writer.getXmlStreamWriter().writeStartDocument(version);
            } else {
                writer.getXmlStreamWriter().writeStartDocument(encoding, version);
            }
        }

        serializer.serializeChildren(document, cache);
    }
}
