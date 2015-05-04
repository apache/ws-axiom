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
package org.apache.axiom.ts.om.element;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.util.XMLStreamWriterRemoveIllegalChars;
import org.apache.axiom.ts.AxiomTestCase;

public class TestSerializeWithXmlStreamWriterFilter extends AxiomTestCase {
    private char c;
    
    public TestSerializeWithXmlStreamWriterFilter(OMMetaFactory metaFactory, char c) {
        super(metaFactory);
        addTestParameter("char", c);
    }
    
    @Override
    protected void runTest() throws Throwable {
        // Create object model containing an invalid character
        OMElement root = metaFactory.getOMFactory().createOMElement("test", null);
        root.setText("[" + c + "]");
        
        // Serialize
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OMOutputFormat omFormat = new OMOutputFormat();
        omFormat.setXmlStreamWriterFilter(new XMLStreamWriterRemoveIllegalChars());
        root.serialize(baos, omFormat);
        
        // Parse the result; this will fail if the invalid character has not been removed
        OMXMLBuilderFactory.createOMBuilder(metaFactory.getOMFactory(),
                new ByteArrayInputStream(baos.toByteArray())).getDocument().build();
    }
}
