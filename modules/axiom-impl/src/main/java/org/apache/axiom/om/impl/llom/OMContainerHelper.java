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
package org.apache.axiom.om.impl.llom;

import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.OMXMLStreamReader;
import org.apache.axiom.om.OMXMLStreamReaderConfiguration;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.common.OMStAXWrapper;
import org.apache.axiom.om.util.OMXMLStreamReaderValidator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class OMContainerHelper {
    private static final Log log = LogFactory.getLog(OMContainerHelper.class);
    
    private static final OMXMLStreamReaderConfiguration defaultReaderConfiguration = new OMXMLStreamReaderConfiguration();
    
    private OMContainerHelper() {}
    
    public static XMLStreamReader getXMLStreamReader(OMContainer container, boolean cache) {
        return getXMLStreamReader(container, cache, defaultReaderConfiguration);
    }
    
    public static XMLStreamReader getXMLStreamReader(OMContainer container, boolean cache, OMXMLStreamReaderConfiguration configuration) {
        OMXMLParserWrapper builder = ((OMSerializableImpl)container).builder;
        if (builder != null && builder instanceof StAXOMBuilder) {
            if (!container.isComplete()) {
                if (((StAXOMBuilder) builder).isLookahead()) {
                    container.buildNext();
                }
            }
        }
        
        // The om tree was built by hand and is already complete
        OMXMLStreamReader reader = null;
        boolean done = ((OMSerializableImpl)container).done;
        if ((builder == null) && done) {
            reader = new OMStAXWrapper(null, container, false, configuration.isPreserveNamespaceContext());
        } else {
            if ((builder == null) && !cache) {
                throw new UnsupportedOperationException(
                "This element was not created in a manner to be switched");
            }
            if (builder != null && builder.isCompleted() && !cache && !done) {
                throw new UnsupportedOperationException(
                "The parser is already consumed!");
            }
            reader = new OMStAXWrapper(builder, container, cache, configuration.isPreserveNamespaceContext());
        }
        
        // If debug is enabled, wrap the OMXMLStreamReader in a validator.
        // The validator will check for mismatched events to help determine if the OMStAXWrapper
        // is functioning correctly.  All problems are reported as debug.log messages
        
        if (log.isDebugEnabled()) {
            reader = 
                new OMXMLStreamReaderValidator(reader, // delegate to actual reader
                     false); // log problems (true will cause exceptions to be thrown)
        }
        
        return reader;
    }
    
}
