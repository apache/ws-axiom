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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMDataSourceExt;

/**
 * Base class for {@link OMDataSourceExt} implementations that can easily produce the content as an
 * {@link XMLStreamReader} and that don't implement any kind of optimization for serializing the
 * content.
 */
public abstract class AbstractPullOMDataSource extends AbstractOMDataSource {
    @Override
    public final boolean isDestructiveWrite() {
        // Since we serialize by copying the events from the XMLStreamReader returned by
        // getReader(), obviously write is destructive if and only if read is destructive
        return isDestructiveRead();
    }

    // Note: this method is never executed by Axiom itself
    @Override
    public final void serialize(XMLStreamWriter xmlWriter) throws XMLStreamException {
        OMAbstractFactory.getOMFactory().createOMElement(this).serialize(xmlWriter, false);
    }
}
