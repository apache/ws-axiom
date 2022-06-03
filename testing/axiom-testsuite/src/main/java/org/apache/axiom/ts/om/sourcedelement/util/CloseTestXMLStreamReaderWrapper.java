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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;

class CloseTestXMLStreamReaderWrapper extends XMLStreamReaderWrapper {
    private final PullOMDataSource ds;
    private boolean closed;

    CloseTestXMLStreamReaderWrapper(PullOMDataSource ds, XMLStreamReader parent) {
        super(parent);
        this.ds = ds;
    }

    @Override
    public void close() throws XMLStreamException {
        super.close();
        ds.readerClosed(this);
        closed = true;
    }

    @Override
    public int next() throws XMLStreamException {
        if (closed) {
            throw new IllegalStateException();
        }
        return super.next();
    }
}
