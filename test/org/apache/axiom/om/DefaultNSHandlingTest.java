package org.apache.axiom.om;

import junit.framework.TestCase;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayInputStream;
import java.util.Iterator;
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

public class DefaultNSHandlingTest extends TestCase {

    public void testDefaultNamespaceWithSameDefaultNSForAll() {
        String testXML = "<html xmlns='http://www.w3.org/TR/REC-html40'>" +
                "<head><title>Frobnostication</title></head>" +
                   "<body><p>Moved to <a href='http://frob.com'>here</a>.</p></body>" +
                "</html>";
        try {
            StAXOMBuilder stAXOMBuilder = new StAXOMBuilder(new ByteArrayInputStream(testXML.getBytes()));
            OMElement documentElement = stAXOMBuilder.getDocumentElement();

            checkNS(documentElement);

            checkNSWithChildren(documentElement);

        } catch (XMLStreamException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void checkNSWithChildren(OMElement documentElement) {
        Iterator childElementsIter = documentElement.getChildElements();
        while (childElementsIter.hasNext()) {
            OMElement omElement = (OMElement) childElementsIter.next();
            checkNS(omElement);
            checkNSWithChildren(omElement);
        }
    }

    private void checkNS(OMElement element) {
        assertTrue("http://www.w3.org/TR/REC-html40".equals(element.getNamespace().getName()));
    }

    public void testMultipleDefaultNS() {
        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        OMNamespace defaultNS1 = omFactory.createOMNamespace("http://defaultNS1.org", null);
        OMNamespace defaultNS2 = omFactory.createOMNamespace("http://defaultNS2.org", null);

        OMElement omElementOne = omFactory.createOMElement("DocumentElement", null);
        omElementOne.declareDefaultNamespace("http://defaultNS1.org");
        OMElement omElementOneChild = omFactory.createOMElement("ChildOne", null, omElementOne);


        OMElement omElementTwo = omFactory.createOMElement("Foo", defaultNS2, omElementOne);
        omElementTwo.declareDefaultNamespace("http://defaultNS2.org");
        OMElement omElementTwoChild = omFactory.createOMElement("ChildOne", null, omElementTwo);

        OMElement omElementThree = omFactory.createOMElement("Bar", defaultNS1, omElementTwo);
        omElementThree.declareDefaultNamespace("http://defaultNS1.org");

        assertTrue("http://defaultNS1.org".equals(omElementOneChild.getNamespace().getName()));
        assertTrue("http://defaultNS2.org".equals(omElementTwoChild.getNamespace().getName()));



    }

    public static void main(String[] args) {
        try {
            XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(System.out);

            xmlStreamWriter.writeStartElement("Foo");
            xmlStreamWriter.writeDefaultNamespace("test.org");
            xmlStreamWriter.setDefaultNamespace("test.org");
            xmlStreamWriter.writeStartElement("Bar");

            xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeEndElement();

            xmlStreamWriter.flush();


        } catch (XMLStreamException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}

