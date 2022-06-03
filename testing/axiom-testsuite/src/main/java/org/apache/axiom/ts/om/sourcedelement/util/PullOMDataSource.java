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
import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.ds.AbstractPullOMDataSource;
import org.apache.axiom.om.util.StAXUtils;

public final class PullOMDataSource extends AbstractPullOMDataSource {
    private final String data;
    private final boolean destructive;
    private final Set<CloseTestXMLStreamReaderWrapper> unclosedReaders =
            new HashSet<CloseTestXMLStreamReaderWrapper>();
    private int readerRequestCount;
    private boolean destroyed;

    public PullOMDataSource(String data) {
        this(data, true);
    }

    public PullOMDataSource(String data, boolean destructive) {
        this.data = data;
        this.destructive = destructive;
    }

    @Override
    public XMLStreamReader getReader() throws XMLStreamException {
        if (destroyed) {
            throw new IllegalStateException("The OMDataSource has already been consumed");
        }
        if (destructive) {
            destroyed = true;
        }
        readerRequestCount++;
        CloseTestXMLStreamReaderWrapper reader =
                new CloseTestXMLStreamReaderWrapper(
                        this, StAXUtils.createXMLStreamReader(new StringReader(data)));
        unclosedReaders.add(reader);
        return reader;
    }

    @Override
    public boolean isDestructiveRead() {
        return destructive;
    }

    public int getReaderRequestCount() {
        return readerRequestCount;
    }

    public boolean hasUnclosedReaders() {
        return !unclosedReaders.isEmpty();
    }

    void readerClosed(CloseTestXMLStreamReaderWrapper reader) {
        unclosedReaders.remove(reader);
    }
}
