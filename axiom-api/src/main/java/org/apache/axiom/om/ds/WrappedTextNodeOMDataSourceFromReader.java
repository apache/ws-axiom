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
package org.apache.axiom.om.ds;

import java.io.IOException;
import java.io.Reader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.util.stax.WrappedTextNodeStreamReader;

/**
 * {@link WrappedTextNodeOMDataSource} that pulls text data from a {@link Reader} object. Since the
 * stream can only be read once, this data source is destructive. The {@link #getObject()} method
 * returns the {@link Reader} object if it has not been accessed yet.
 */
public class WrappedTextNodeOMDataSourceFromReader extends WrappedTextNodeOMDataSource {
    private final Reader reader;
    private boolean isAccessed;

    public WrappedTextNodeOMDataSourceFromReader(QName wrapperElementName, Reader reader) {
        super(wrapperElementName);
        this.reader = reader;
    }

    @Override
    public XMLStreamReader getReader() throws XMLStreamException {
        isAccessed = true;
        return new WrappedTextNodeStreamReader(wrapperElementName, reader);
    }

    @Override
    public Object getObject() {
        return isAccessed ? null : reader;
    }

    @Override
    public boolean isDestructiveRead() {
        return true;
    }

    @Override
    public void close() {
        try {
            reader.close();
        } catch (IOException ex) {
            // Ignore
        }
    }
}
