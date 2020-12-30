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
import org.apache.axiom.core.stream.xop.AbstractXOPEncodingFilterHandler;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.om.OMAttachmentAccessor;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.intf.TextContent;

public final class XOPEncodingFilterHandler extends AbstractXOPEncodingFilterHandler implements XOPHandler, OMAttachmentAccessor {
    private final Map<String,Object> dataHandlerObjects = new LinkedHashMap<String,Object>();
    private final ContentIDGenerator contentIDGenerator;
    private final OptimizationPolicy optimizationPolicy;

    public XOPEncodingFilterHandler(XmlHandler parent, ContentIDGenerator contentIDGenerator,
            OptimizationPolicy optimizationPolicy) {
        super(parent);
        this.contentIDGenerator = contentIDGenerator;
        this.optimizationPolicy = optimizationPolicy;
    }

    @Override
    public String prepareDataHandler(DataHandler dataHandler) {
        boolean doOptimize;
        try {
            doOptimize = optimizationPolicy.isOptimized(dataHandler, true);
        } catch (IOException ex) {
            doOptimize = true;
        }
        if (doOptimize) {
            String contentID = contentIDGenerator.generateContentID(null);
            dataHandlerObjects.put(contentID, dataHandler);
            return contentID;
        } else {
            return null;
        }
    }

    /**
     * Get the set of content IDs referenced in {@code xop:Include} element information items
     * produced by this wrapper.
     * 
     * @return The set of content IDs in their order of appearance in the infoset. If no
     *         {@code xop:Include} element information items have been produced yet, an empty
     *         set will be returned.
     */
    public Set<String> getContentIDs() {
        return Collections.unmodifiableSet(dataHandlerObjects.keySet());
    }

    @Override
    public DataHandler getDataHandler(String contentID) {
        Object dataHandlerObject = dataHandlerObjects.get(contentID);
        if (dataHandlerObject == null) {
            return null;
        } else if (dataHandlerObject instanceof DataHandler) {
            return (DataHandler)dataHandlerObject;
        } else {
            try {
                return ((DataHandlerProvider)dataHandlerObject).getDataHandler();
            } catch (IOException ex) {
                throw new OMException(ex);
            }
        }
    }

    @Override
    protected String processCharacterData(Object data) throws StreamException {
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
                    return contentID;
                }
            }
        }
        return null;
    }
}
