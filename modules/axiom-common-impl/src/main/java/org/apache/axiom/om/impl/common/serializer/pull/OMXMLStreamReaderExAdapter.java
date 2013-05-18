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
package org.apache.axiom.om.impl.common.serializer.pull;

import java.io.IOException;

import javax.activation.DataHandler;
import javax.xml.stream.util.StreamReaderDelegate;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMXMLStreamReader;
import org.apache.axiom.om.impl.OMXMLStreamReaderEx;
import org.apache.axiom.util.stax.xop.ContentIDGenerator;
import org.apache.axiom.util.stax.xop.OptimizationPolicy;
import org.apache.axiom.util.stax.xop.XOPEncodingStreamReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Adapter that adds the {@link OMXMLStreamReaderEx} interface to a {@link PullSerializer}.
 * <p>
 * Note that this class will disappear in Axiom 1.3 because the methods defined by
 * {@link OMXMLStreamReader} are deprecated.
 */
public class OMXMLStreamReaderExAdapter extends StreamReaderDelegate implements OMXMLStreamReaderEx {
    private static final Log log = LogFactory.getLog(OMXMLStreamReaderExAdapter.class);
    
    private final PullSerializer serializer;
    private XOPEncodingStreamReader xopEncoder;
    
    public OMXMLStreamReaderExAdapter(PullSerializer serializer) {
        super(serializer);
        this.serializer = serializer;
    }

    public boolean isInlineMTOM() {
        return xopEncoder == null;
    }

    public void setInlineMTOM(boolean value) {
        // For inlineMTOM=false, we insert an XOPEncodingStreamReader proxy between
        // us and PullSerializer.
        // For inlineMTOM=true, we remove it and delegate directly to PullSerializer.
        if (value) {
            if (xopEncoder != null) {
                xopEncoder = null;
                setParent(serializer);
            }
        } else {
            if (xopEncoder == null) {
                // Since the intention is to support an efficient way to pass binary content to a
                // consumer that is not aware of our data handler extension (see AXIOM-202), we
                // use OptimizationPolicy.ALL, i.e. we ignore OMText#isOptimized().
                xopEncoder = new XOPEncodingStreamReader(serializer, ContentIDGenerator.DEFAULT,
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
    
    public OMDataSource getDataSource() {
        return serializer.getDataSource();
    }
    
    public void enableDataSourceEvents(boolean value) {
        serializer.enableDataSourceEvents(value);
    }
}
