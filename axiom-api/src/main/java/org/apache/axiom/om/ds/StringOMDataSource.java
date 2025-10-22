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

import java.io.StringReader;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.util.StAXUtils;

/** {@link OMDataSource} implementation backed by a {@link String}. */
public final class StringOMDataSource extends AbstractPullOMDataSource {
    private String data;

    public StringOMDataSource(String data) {
        this.data = data;
    }

    @Override
    public String getObject() {
        return data;
    }

    @Override
    public StringOMDataSource copy() {
        return new StringOMDataSource(data);
    }

    @Override
    public boolean isDestructiveRead() {
        return false;
    }

    @Override
    public XMLStreamReader getReader() throws XMLStreamException {
        return StAXUtils.createXMLStreamReader(new StringReader(data));
    }

    @Override
    public void close() {
        data = null;
    }
}
