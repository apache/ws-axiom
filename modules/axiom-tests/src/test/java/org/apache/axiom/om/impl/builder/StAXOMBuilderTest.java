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

package org.apache.axiom.om.impl.builder;

import org.apache.axiom.om.AbstractTestCase;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.llom.factory.OMXMLBuilderFactory;
import org.apache.axiom.om.util.StAXUtils;

import java.io.FileReader;
import java.util.Iterator;

import javax.xml.stream.XMLInputFactory;

public class StAXOMBuilderTest extends AbstractTestCase {
    StAXOMBuilder stAXOMBuilder;
    private OMElement rootElement;

    /** Constructor. */
    public StAXOMBuilderTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        stAXOMBuilder =
                OMXMLBuilderFactory.createStAXOMBuilder(
                        OMAbstractFactory.getSOAP11Factory(),
                        StAXUtils.createXMLStreamReader(
                                getTestResource("non_soap.xml")));
    }

    protected void tearDown() throws Exception {
        stAXOMBuilder.close();
    }

    public void testGetRootElement() throws Exception {
        rootElement = stAXOMBuilder.getDocumentElement();
        assertTrue("Root element can not be null", rootElement != null);
        assertTrue(" Name of the root element is wrong",
                   rootElement.getLocalName().equalsIgnoreCase("Root"));
        // get the first OMElement child
        OMNode omnode = rootElement.getFirstOMChild();
        while (omnode instanceof OMText) {
            omnode = omnode.getNextOMSibling();
        }
        Iterator children = ((OMElement) omnode).getChildren();
        int childrenCount = 0;
        while (children.hasNext()) {
            OMNode node = (OMNode) children.next();
            if (node instanceof OMElement)
                childrenCount++;
        }
        assertTrue(childrenCount == 5);
    }
    
    public void testClose1() throws Exception {
        rootElement = stAXOMBuilder.getDocumentElement();
        assertTrue("Root element can not be null", rootElement != null);
        assertTrue(" Name of the root element is wrong",
                   rootElement.getLocalName().equalsIgnoreCase("Root"));
        // get the first OMElement child
        OMNode omnode = rootElement.getFirstOMChild();
        while (omnode instanceof OMText) {
            omnode = omnode.getNextOMSibling();
        }
        // Close the element immediately
        OMElement omElement = (OMElement) omnode;
        omElement.close(false);
        
        Iterator children = ((OMElement) omnode).getChildren();
        int childrenCount = 0;
        while (children.hasNext()) {
            OMNode node = (OMNode) children.next();
            if (node instanceof OMElement)
                childrenCount++;
        }
        
        assertTrue(childrenCount == 0);
    }
    
    public void testClose2() throws Exception {
        rootElement = stAXOMBuilder.getDocumentElement();
        assertTrue("Root element can not be null", rootElement != null);
        assertTrue(" Name of the root element is wrong",
                   rootElement.getLocalName().equalsIgnoreCase("Root"));
        // get the first OMElement child
        OMNode omnode = rootElement.getFirstOMChild();
        while (omnode instanceof OMText) {
            omnode = omnode.getNextOMSibling();
        }
        // Close the element after building the element
        OMElement omElement = (OMElement) omnode;
        omElement.close(true);
        
        Iterator children = ((OMElement) omnode).getChildren();
        int childrenCount = 0;
        while (children.hasNext()) {
            OMNode node = (OMNode) children.next();
            if (node instanceof OMElement)
                childrenCount++;
        }
        
        assertTrue(childrenCount == 5);
    }
    
 public void testInvalidXML() throws Exception {
        
        StAXOMBuilder stAXOMBuilder =
                OMXMLBuilderFactory.createStAXOMBuilder(OMAbstractFactory.getSOAP11Factory(),
                                                        XMLInputFactory.newInstance()
                                                                       .createXMLStreamReader(
                                                                         getTestResource("invalid_xml.xml")));
        
        Exception exception = null;
        while (exception == null || stAXOMBuilder.isCompleted()) {
            try {
                stAXOMBuilder.next();
            } catch (Exception e) {
                exception =e;
            }
        }
        
        assertTrue("Expected an exception because invalid_xml.xml is wrong", exception != null);
        
        // Intentionally call builder again to make sure the same error is returned.
        Exception exception2 = null;
        try {
            stAXOMBuilder.next();
        } catch (Exception e) {
            exception2 = e;
        }
        
        assertTrue("Expected a second exception because invalid_xml.xml is wrong", exception2 != null);
        assertTrue("Expected the same exception. first=" + exception + " second=" + exception2, 
                    exception.getMessage().equals(exception2.getMessage()));
        
    }
}