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
package org.apache.axiom.om.impl.stream.xop;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;

import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.XmlHandlerWrapper;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.om.impl.intf.TextContent;
import org.apache.axiom.util.stax.xop.ContentIDGenerator;
import org.apache.axiom.util.stax.xop.OptimizationPolicy;
import org.apache.axiom.util.stax.xop.XOPUtils;

public final class XOPEncodingFilterHandler extends XmlHandlerWrapper {
    private final Map<String,Object> dataHandlerObjects = new LinkedHashMap<String,Object>();
    private final ContentIDGenerator contentIDGenerator;
    private final OptimizationPolicy optimizationPolicy;
    private final CompletionListener completionListener;

    public XOPEncodingFilterHandler(XmlHandler parent, ContentIDGenerator contentIDGenerator,
            OptimizationPolicy optimizationPolicy, CompletionListener completionListener) {
        super(parent);
        this.contentIDGenerator = contentIDGenerator;
        this.optimizationPolicy = optimizationPolicy;
        this.completionListener = completionListener;
    }

    /**
     * Get the set of content IDs referenced in <tt>xop:Include</tt> element information items
     * produced by this wrapper.
     * 
     * @return The set of content IDs in their order of appearance in the infoset. If no
     *         <tt>xop:Include</tt> element information items have been produced yet, an empty
     *         set will be returned.
     */
    public Set<String> getContentIDs() {
        return Collections.unmodifiableSet(dataHandlerObjects.keySet());
    }

    public DataHandler getDataHandler(String contentID) throws IOException {
        Object dataHandlerObject = dataHandlerObjects.get(contentID);
        if (dataHandlerObject == null) {
            throw new IllegalArgumentException("No DataHandler object found for content ID '" +
                    contentID + "'");
        } else if (dataHandlerObject instanceof DataHandler) {
            return (DataHandler)dataHandlerObject;
        } else {
            return ((DataHandlerProvider)dataHandlerObject).getDataHandler();
        }
    }

    @Override
    public void processCharacterData(Object data, boolean ignorable) throws StreamException {
        if (data instanceof TextContent) {
            TextContent textContent = (TextContent)data;
            if (textContent.isBinary()) {
                Object dataHandlerObject = textContent.getDataHandlerObject();
                boolean optimize;
                try {
                    if (dataHandlerObject instanceof DataHandlerProvider) {
                        optimize = optimizationPolicy.isOptimized((DataHandlerProvider)dataHandlerObject, textContent.isOptimize());
                    } else {
                        optimize = optimizationPolicy.isOptimized((DataHandler)dataHandlerObject, textContent.isOptimize());
                    }
                } catch (IOException ex) {
                    throw new StreamException(ex);
                }
                if (optimize) {
                    String contentID = contentIDGenerator.generateContentID(textContent.getContentID());
                    dataHandlerObjects.put(contentID, dataHandlerObject);
                    super.startElement(XOPConstants.NAMESPACE_URI, XOPConstants.INCLUDE, XOPConstants.DEFAULT_PREFIX);
                    super.processNamespaceDeclaration(XOPConstants.DEFAULT_PREFIX, XOPConstants.NAMESPACE_URI);
                    super.processAttribute("", XOPConstants.HREF, "", XOPUtils.getURLForContentID(contentID), "CDATA", true);
                    super.attributesCompleted();
                    super.endElement();
                    return;
                }
            }
        }
        super.processCharacterData(data, ignorable);
    }

    @Override
    public void completed() throws StreamException {
        super.completed();
        completionListener.completed(this);
    }
}
