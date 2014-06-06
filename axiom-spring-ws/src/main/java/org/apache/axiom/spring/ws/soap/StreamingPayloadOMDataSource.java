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
package org.apache.axiom.spring.ws.soap;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.QNameAwareOMDataSource;
import org.apache.axiom.om.ds.AbstractPushOMDataSource;
import org.springframework.ws.stream.StreamingPayload;

final class StreamingPayloadOMDataSource extends AbstractPushOMDataSource implements QNameAwareOMDataSource {
    private final StreamingPayload payload;

    public StreamingPayloadOMDataSource(StreamingPayload payload) {
        this.payload = payload;
    }

    public boolean isDestructiveWrite() {
        // Note that the StreamingPayload contract doesn't specify if the payload can be
        // written multiple times. We assume that it can. In particular this is true
        // for JaxbStreamingPayload.
        return false;
    }

    public void serialize(XMLStreamWriter streamWriter) throws XMLStreamException {
        payload.writeTo(streamWriter);
    }

    public String getLocalName() {
        return payload.getName().getLocalPart();
    }

    public String getNamespaceURI() {
        return payload.getName().getNamespaceURI();
    }

    public String getPrefix() {
        // It is unlikely that the StreamingPayload correctly predicts the prefix
        // that will be used.
        return null;
    }
}
