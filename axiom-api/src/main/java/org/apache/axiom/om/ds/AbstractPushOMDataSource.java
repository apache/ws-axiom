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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.util.StAXUtils;

/**
 * Base class for {@link OMDataSourceExt} implementations that can easily serialize the content to
 * an {@link XMLStreamWriter} but that are unable to produce the content as an {@link
 * XMLStreamReader}.
 *
 * <p>{@link OMSourcedElement} will handle {@link OMDataSource} implementations extending this class
 * differently when it comes to expansion: instead of using {@link OMDataSource#getReader()} to
 * expand the element, it will use {@link OMDataSource#serialize(XMLStreamWriter)} (with a special
 * {@link XMLStreamWriter} that builds the descendants of the {@link OMSourcedElement}). This means
 * that such an {@link OMSourcedElement} will be expanded instantly, and that deferred building of
 * the descendants is not applicable.
 */
public abstract class AbstractPushOMDataSource extends AbstractOMDataSource {
    @Override
    public final boolean isDestructiveRead() {
        return isDestructiveWrite();
    }

    @Override
    public final XMLStreamReader getReader() throws XMLStreamException {
        // Note: we don't actually expect this code to be called because OMSourcedElement should
        // handle AbstractPushOMDataSource instances differently. Nevertheless the code is
        // functionally correct (but not very good from a performance point of view, especially for
        // XOP).
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        serialize(bos, new OMOutputFormat());
        return StAXUtils.createXMLStreamReader(new ByteArrayInputStream(bos.toByteArray()));
    }
}
