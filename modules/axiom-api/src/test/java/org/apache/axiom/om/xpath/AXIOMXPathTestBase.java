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

package org.apache.axiom.om.xpath;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.util.stax.wrapper.XMLStreamReaderWrapper;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;
import org.jaxen.test.XPathTestBase;

public class AXIOMXPathTestBase extends XPathTestBase {
    static class RootWhitespaceFilter extends XMLStreamReaderWrapper {
        private int depth;
        
        public RootWhitespaceFilter(XMLStreamReader parent) {
            super(parent);
        }

        public int next() throws XMLStreamException {
            int event;
            loop: while (true) {
                event = super.next();
                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        depth++;
                        break loop;
                    case XMLStreamConstants.END_ELEMENT:
                        depth--;
                        break loop;
                    case XMLStreamConstants.CHARACTERS:
                    case XMLStreamConstants.SPACE:
                        if (depth > 0) {
                            break loop;
                        } else {
                            continue loop;
                        }
                    default:
                        break loop;
                }
            }
            return event;
        }
    }
    
    static String TESTS_ROOT;
    
    static {
        URL testsXmlUrl = XPathTestBase.class.getClassLoader().getResource("jaxen/xml/test/tests.xml");
        try {
            TESTS_ROOT = new URL(testsXmlUrl, "../..").toExternalForm();
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    final OMMetaFactory omMetaFactory;
    final List documents = new ArrayList();
    
    public AXIOMXPathTestBase(String name, OMMetaFactory omMetaFactory) {
        super(name);
        this.omMetaFactory = omMetaFactory;
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        for (Iterator it = documents.iterator(); it.hasNext(); ) {
            ((OMDocument)it.next()).close(false);
        }
        documents.clear();
    }

    protected Object getDocument(String url) throws Exception {
        // This method is actually never used in XPathTestBase; it only uses Navigator#getDocument
        return null;
    }

    protected Navigator getNavigator() {
        return new DocumentNavigator() {
            // We need to tweak the getDocument method a bit to load the document from the right
            // place. Also, Jaxen's unit tests assume that whitespace in the prolog/epilog is not
            // represented in the tree (as in DOM), so we need to filter these events.
            public Object getDocument(String uri) throws FunctionCallException {
                try {
                    URL url = new URL(TESTS_ROOT + uri);
                    XMLStreamReader reader = new RootWhitespaceFilter(
                            StAXUtils.createXMLStreamReader(url.openStream()));
                    OMDocument document = new StAXOMBuilder(omMetaFactory.getOMFactory(),
                            reader).getDocument();
                    documents.add(document);
                    return document;
                } catch (Exception ex) {
                    throw new FunctionCallException(ex);
                }
            }
        };
    }
}
