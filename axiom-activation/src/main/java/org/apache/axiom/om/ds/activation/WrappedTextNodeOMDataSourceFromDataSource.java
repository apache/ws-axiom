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
package org.apache.axiom.om.ds.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.activation.DataSource;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.ds.WrappedTextNodeOMDataSource;
import org.apache.axiom.util.stax.WrappedTextNodeStreamReader;

/**
 * {@link WrappedTextNodeOMDataSource} that pulls the text data from a {@link DataSource} object.
 * The {@link #getObject()} method returns the {@link DataSource} instance.
 */
public class WrappedTextNodeOMDataSourceFromDataSource extends WrappedTextNodeOMDataSource {
    private final DataSource binaryData;
    private final Charset charset;

    public WrappedTextNodeOMDataSourceFromDataSource(QName wrapperElementName, DataSource binaryData,
            Charset charset) {
        super(wrapperElementName);
        this.binaryData = binaryData;
        this.charset = charset;
    }
    
    @Override
    public XMLStreamReader getReader() throws XMLStreamException {
        InputStream is;
        try {
            is = binaryData.getInputStream();
        }
        catch (IOException ex) {
            throw new XMLStreamException(ex);
        }
        return new WrappedTextNodeStreamReader(wrapperElementName, new InputStreamReader(is, charset));
    }

    @Override
    public Object getObject() {
        return binaryData;
    }

    @Override
    public boolean isDestructiveRead() {
        return false;
    }

    @Override
    public OMDataSourceExt copy() {
        return new WrappedTextNodeOMDataSourceFromDataSource(wrapperElementName, binaryData, charset);
    }
}
