package org.apache.axiom.om.util;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.dom.DOOMAbstractFactory;
import org.custommonkey.xmlunit.XMLTestCase;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class OMElementHelperTest extends XMLTestCase {

    private String testXMLFilePath = "test-resources/soap/soapmessage.xml";



    public void testImportOMElement() {
        try {
            XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(testXMLFilePath));
            OMElement documentElement = new StAXOMBuilder(OMAbstractFactory.getOMFactory(), xmlStreamReader).getDocumentElement();

            // first lets try to import an element created from llom in to llom factory. This should return the same element
            assertTrue(ElementHelper.importOMElement(documentElement, OMAbstractFactory.getOMFactory()) == documentElement);

            // then lets pass in an OMElement created using llom and pass DOOMFactory
            OMElement importedElement = ElementHelper.importOMElement(documentElement, DOOMAbstractFactory.getOMFactory());
            assertTrue(importedElement != documentElement);
            assertTrue(importedElement.getOMFactory().getClass().isInstance(DOOMAbstractFactory.getOMFactory()));

        } catch (XMLStreamException e) {
            e.printStackTrace();
            fail();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail();
        }
    }
}
