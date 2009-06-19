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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMSerializable;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class OMSerializableImpl implements OMSerializable {
    private static final Log log = LogFactory.getLog(OMSerializableImpl.class);
    private static boolean DEBUG_ENABLED = log.isDebugEnabled();
    
    /** Field parserWrapper */
    public OMXMLParserWrapper builder;

    /** Field done */
    protected boolean done = false;

    protected OMFactory factory;

    public final OMFactory getOMFactory() {
        return factory;
    }

    public boolean isComplete() {
        return done;
    }

    /**
     * Parses this node and builds the object structure in memory. However a node, created
     * programmatically, will have done set to true by default and this will cause populateyourself
     * not to work properly!
     *
     * @throws OMException
     */
    public void build() throws OMException {
        if (builder != null && builder.isCompleted()) {
            if (DEBUG_ENABLED) {
                log.debug("Builder is already complete.");
            }
        }
        while (!done) {

            builder.next();    
            if (builder.isCompleted() && !done) {
                if (DEBUG_ENABLED) {
                    log.debug("Builder is complete.  Setting OMObject to complete.");
                }
                setComplete(true);
            }
        }
    }
    
    public abstract void setComplete(boolean state);

    /**
     * Serializes the node with caching.
     *
     * @param writer
     * @throws XMLStreamException
     */
    public abstract void internalSerialize(XMLStreamWriter writer) throws XMLStreamException;

    /**
     * Serializes the node without caching.
     *
     * @param writer
     * @throws XMLStreamException
     */
    public abstract void internalSerializeAndConsume(XMLStreamWriter writer)
            throws XMLStreamException;

    public void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException {
        
        // If the input xmlWriter is not an MTOMXMLStreamWriter, then wrapper it
        MTOMXMLStreamWriter writer = xmlWriter instanceof MTOMXMLStreamWriter ?
                (MTOMXMLStreamWriter) xmlWriter : 
                    new MTOMXMLStreamWriter(xmlWriter);
        internalSerialize(writer);
        writer.flush();
    }

    public void serializeAndConsume(XMLStreamWriter xmlWriter) throws XMLStreamException {
        // If the input xmlWriter is not an MTOMXMLStreamWriter, then wrapper it
        MTOMXMLStreamWriter writer = xmlWriter instanceof MTOMXMLStreamWriter ?
                (MTOMXMLStreamWriter) xmlWriter : 
                    new MTOMXMLStreamWriter(xmlWriter);
        internalSerializeAndConsume(writer);
        writer.flush();
    }
}
