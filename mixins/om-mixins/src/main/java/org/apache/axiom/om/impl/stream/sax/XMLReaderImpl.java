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
package org.apache.axiom.om.impl.stream.sax;

import java.io.IOException;

import org.apache.axiom.core.CoreElement;
import org.apache.axiom.core.CoreModelException;
import org.apache.axiom.core.stream.StreamException;
import org.apache.axiom.core.stream.XmlHandler;
import org.apache.axiom.core.stream.sax.output.ContentHandlerXmlHandler;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.stream.NamespaceContextPreservationFilterHandler;
import org.apache.axiom.util.sax.AbstractXMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLReaderImpl extends AbstractXMLReader {
    private final AxiomContainer root;
    private final boolean cache;

    public XMLReaderImpl(AxiomContainer root, boolean cache) {
        this.root = root;
        this.cache = cache;
    }

    @Override
    public void parse(InputSource input) throws IOException, SAXException {
        parse();
    }

    @Override
    public void parse(String systemId) throws IOException, SAXException {
        parse();
    }

    private void parse() throws SAXException {
        XmlHandler handler = new ContentHandlerXmlHandler(contentHandler, lexicalHandler);
        CoreElement contextElement = root.getContextElement();
        if (contextElement != null) {
            handler = new NamespaceContextPreservationFilterHandler(handler, contextElement);
        }
        try {
            root.internalSerialize(handler, cache);
        } catch (CoreModelException ex) {
            throw new SAXException(ex);
        } catch (StreamException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof SAXException saxException) {
                throw saxException;
            } else if (cause instanceof Exception exception) {
                throw new SAXException(exception);
            } else {
                throw new SAXException(ex);
            }
        }
    }
}
