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
package org.apache.axiom.ts.jaxp;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.axiom.testing.multiton.Multiton;
import org.xml.sax.ext.LexicalHandler;

/**
 * Specifies an XSLT implementation for use in a {@link MatrixTestCase}.
 */
public abstract class XSLTImplementation extends Multiton {
    public static final XSLTImplementation JRE = new XSLTImplementation("jre") {
        @Override
        public TransformerFactory newTransformerFactory() {
            return TransformerFactory.newDefaultInstance();
        }
    };

    public static final XSLTImplementation XALAN = new XSLTImplementation("xalan") {
        @Override
        public TransformerFactory newTransformerFactory() {
            return new org.apache.xalan.processor.TransformerFactoryImpl();
        }
    };
    
    public static final XSLTImplementation SAXON = new XSLTImplementation("saxon") {
        @Override
        public TransformerFactory newTransformerFactory() {
            return new net.sf.saxon.TransformerFactoryImpl();
        }
    };
    
    private final String name;
    private Boolean supportsLexicalHandlerWithStreamSource;
    private Boolean supportsStAXSource;
    
    private XSLTImplementation(String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public abstract TransformerFactory newTransformerFactory();
    
    /**
     * Determine if an identity transformation from a {@link StreamSource} to a {@link SAXResult}
     * will generate events defined by {@link LexicalHandler}.
     * 
     * @return <code>true</code> if the XSLT implementation will invoke the methods on the
     *         {@link LexicalHandler} set on the {@link SAXResult}, <code>false</code> otherwise
     */
    public final synchronized boolean supportsLexicalHandlerWithStreamSource() {
        if (supportsLexicalHandlerWithStreamSource == null) {
            StreamSource source = new StreamSource(new StringReader("<!DOCTYPE root><root><![CDATA[test]]></root>"));
            TestContentHandler handler = new TestContentHandler();
            SAXResult result = new SAXResult(handler);
            result.setLexicalHandler(handler);
            try {
                newTransformerFactory().newTransformer().transform(source, result);
            } catch (TransformerException ex) {
                throw new RuntimeException(ex);
            }
            supportsLexicalHandlerWithStreamSource = handler.getLexicalEventsReceived() == 4;
        }
        return supportsLexicalHandlerWithStreamSource;
    }

    public final synchronized boolean supportsStAXSource() {
        if (supportsStAXSource == null) {
            try {
                StAXSource source = new StAXSource(XMLInputFactory.newInstance().createXMLStreamReader(new StringReader("<root/>")));
                StreamResult result = new StreamResult(new ByteArrayOutputStream());
                newTransformerFactory().newTransformer().transform(source, result);
                supportsStAXSource = true;
            } catch (Exception ex) {
                supportsStAXSource = false;
            }
        }
        return supportsStAXSource;
    }
}
