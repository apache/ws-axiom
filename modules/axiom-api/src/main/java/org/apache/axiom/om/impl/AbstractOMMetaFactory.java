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
package org.apache.axiom.om.impl;

import java.io.InputStream;
import java.io.Reader;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.util.StAXUtils;

/**
 * Base class for {@link OMMetaFactory} implementations that make use of the standard builders
 * ({@link org.apache.axiom.om.impl.builder.StAXOMBuilder} and its subclasses).
 */
public abstract class AbstractOMMetaFactory implements OMMetaFactory {
    public OMXMLParserWrapper createStAXOMBuilder(OMFactory omFactory, XMLStreamReader parser) {
        return new StAXOMBuilder(omFactory, parser);
    }

    public OMXMLParserWrapper createOMBuilder(OMFactory omFactory, InputStream in) {
        try {
            return new StAXOMBuilder(omFactory, StAXUtils.createXMLStreamReader(in));
        } catch (XMLStreamException ex) {
            throw new OMException(ex);
        }
    }

    public OMXMLParserWrapper createOMBuilder(OMFactory omFactory, Reader in) {
        try {
            return new StAXOMBuilder(omFactory, StAXUtils.createXMLStreamReader(in));
        } catch (XMLStreamException ex) {
            throw new OMException(ex);
        }
    }
}
