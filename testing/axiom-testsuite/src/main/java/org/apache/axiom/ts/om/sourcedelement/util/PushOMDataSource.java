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
package org.apache.axiom.ts.om.sourcedelement.util;

import java.io.StringReader;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.ds.AbstractPushOMDataSource;

public final class PushOMDataSource extends AbstractPushOMDataSource {
    private final OMFactory factory;
    private final String data;
    private final boolean destructive;
    private boolean destroyed;

    public PushOMDataSource(OMFactory factory, String data, boolean destructive) {
        this.factory = factory;
        this.data = data;
        this.destructive = destructive;
    }

    @Override
    public void serialize(XMLStreamWriter writer) throws XMLStreamException {
        if (destroyed) {
            throw new IllegalStateException("The OMDataSource has already been consumed");
        }
        if (destructive) {
            destroyed = true;
        }
        OMXMLParserWrapper builder =
                OMXMLBuilderFactory.createOMBuilder(factory, new StringReader(data));
        builder.getDocumentElement().serializeAndConsume(writer);
        builder.close();
    }

    @Override
    public boolean isDestructiveWrite() {
        return destructive;
    }
}
