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

import java.io.IOException;

import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.OMXMLStreamReaderEx;
import org.apache.axiom.util.stax.xop.ContentIDGenerator;
import org.apache.axiom.util.stax.xop.OptimizationPolicy;
import org.apache.axiom.util.stax.xop.XOPEncodingStreamReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * {@link XMLStreamReader} implementation that generates events from a given Axiom tree.
 * This class does intentionally does not implement XMLStreamReaderContainer because
 * it does not wrap a parser (it wraps an OM graph).
 */
class OMStAXWrapper extends StreamReaderDelegate implements OMXMLStreamReaderEx {
    private static final Log log = LogFactory.getLog(OMStAXWrapper.class);
    
    private final StreamSwitch streamSwitch = new StreamSwitch();
    private XOPEncodingStreamReader xopEncoder;
    
    /**
     * Constructor OMStAXWrapper.
     *
     * @param builder
     * @param startNode
     * @param cache
     * @param preserveNamespaceContext
     */
    public OMStAXWrapper(OMXMLParserWrapper builder, OMContainer startNode,
                         boolean cache, boolean preserveNamespaceContext) {
        streamSwitch.setParent(new SwitchingWrapper(builder, startNode, cache, preserveNamespaceContext));
        setParent(streamSwitch);
    }

    public boolean isInlineMTOM() {
        return xopEncoder == null;
    }

    public void setInlineMTOM(boolean value) {
        // For inlineMTOM=false, we insert an XOPEncodingStreamReader proxy between
        // us and SwitchingWrapper.
        // For inlineMTOM=true, we remove it and delegate directly to SwitchingWrapper.
        if (value) {
            if (xopEncoder != null) {
                xopEncoder = null;
                setParent(streamSwitch);
            }
        } else {
            if (xopEncoder == null) {
                // Since the intention is to support an efficient way to pass binary content to a
                // consumer that is not aware of our data handler extension (see AXIOM-202), we
                // use OptimizationPolicy.ALL, i.e. we ignore OMText#isOptimized().
                xopEncoder = new XOPEncodingStreamReader(streamSwitch, ContentIDGenerator.DEFAULT,
                        OptimizationPolicy.ALL);
                setParent(xopEncoder);
            }
        }
    }

    public DataHandler getDataHandler(String contentID) {
        if (contentID.startsWith("cid:")) {
            log.warn("Invalid usage of OMStAXWrapper#getDataHandler(String): the argument must " +
            		"be a content ID, not an href; see OMAttachmentAccessor.");
            contentID = contentID.substring(4);
        }
        
        if (xopEncoder == null) {
            throw new IllegalStateException("The wrapper is in inlineMTOM=true mode");
        }
        if (xopEncoder.getContentIDs().contains(contentID)) {
            try {
                return xopEncoder.getDataHandler(contentID);
            } catch (IOException ex) {
                throw new OMException(ex);
            }
        } else {
            return null;
        }
    }
    
    // TODO: need to check which of these delegate methods are really necessary;
    //       some of them should also be defined properly by an interface
    
    public boolean isClosed() {
        return streamSwitch.isClosed();
    }

    public void releaseParserOnClose(boolean value) {
        streamSwitch.releaseParserOnClose(value);
    }

    public OMDataSource getDataSource() {
        return streamSwitch.getDataSource();
    }
    
    public void enableDataSourceEvents(boolean value) {
        streamSwitch.enableDataSourceEvents(value);
    }
}
