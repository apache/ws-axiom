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
package org.apache.axiom.om.impl.mixin;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.impl.builder.Builder;
import org.apache.axiom.om.impl.common.AxiomExceptionTranslator;
import org.apache.axiom.om.impl.common.serializer.push.stax.StAXSerializer;
import org.apache.axiom.om.impl.intf.AxiomSerializable;
import org.apache.axiom.om.impl.stream.StreamException;

public aspect AxiomSerializableSupport {
    public final void AxiomSerializable.serialize(XMLStreamWriter xmlWriter) throws XMLStreamException {
        serialize(xmlWriter, true);
    }

    public final void AxiomSerializable.serializeAndConsume(XMLStreamWriter xmlWriter) throws XMLStreamException {
        serialize(xmlWriter, false);
    }

    public final void AxiomSerializable.serialize(XMLStreamWriter xmlWriter, boolean cache) throws XMLStreamException {
        // If the input xmlWriter is not an MTOMXMLStreamWriter, then wrapper it
        MTOMXMLStreamWriter writer = xmlWriter instanceof MTOMXMLStreamWriter ?
                (MTOMXMLStreamWriter) xmlWriter : 
                    new MTOMXMLStreamWriter(xmlWriter);
        try {
            internalSerialize(new StAXSerializer(writer).buildHandler(this), writer.getOutputFormat(), cache);
        } catch (StreamException ex) {
            throw AxiomExceptionTranslator.toXMLStreamException(ex);
        }
        writer.flush();
    }

    public void AxiomSerializable.close(boolean build) {
        OMXMLParserWrapper builder = getBuilder();
        if (build) {
            this.build();
        }
        setComplete(true);
        
        // If this is a StAXBuilder, close it.
        if (builder instanceof Builder) {
            ((Builder) builder).close();
        }
    }
}
