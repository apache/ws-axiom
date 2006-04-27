package org.apache.axiom.om;

import junit.framework.TestCase;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import java.io.StringReader;

import org.apache.axiom.om.impl.builder.StAXOMBuilder;
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

public class OMWrapperTest extends TestCase {

    public void testSingleElementWrapper(){
        try {
            String xml = "<root>" +
                    "<wrap1>" +
                    "<wrap3>" +
                    "<wrap2>" +
                    "IncludedText" +
                    "</wrap2>" +
                    "</wrap3>" +
                    "</wrap1>" +
                    "</root>";

            XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xml));
            StAXOMBuilder b = new StAXOMBuilder(xmlStreamReader);

            OMElement documentElement = b.getDocumentElement();
            OMElement wrap2Element =
                    documentElement.getFirstElement().
                            getFirstElement().
                            getFirstElement();

            OMElement elt = OMAbstractFactory.getOMFactory().createOMElement(
                    "testName","urn:testNs","ns1"
            );

            elt.addChild(wrap2Element);


            XMLStreamReader reader = wrap2Element.getXMLStreamReaderWithoutCaching();
//            XMLStreamReader reader = wrap2Element.getXMLStreamReader();
            while(reader.hasNext()){
                System.out.println(getEventString(reader.next()));
                System.out.println(reader.hasName()?reader.getLocalName():"");
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    private static String getEventString(int eventCode) {
            String event = "";

            switch (eventCode) {
                case 1 :
                    event = "START_ELEMENT";
                    break;
                case 2 :
                    event = "END_ELEMENT";
                    break;
                case 3 :
                    event = "PROCESSING_INSTRUCTION";
                    break;
                case 4 :
                    event = "CHARACTERS";
                    break;
                case 5 :
                    event = "COMMENT";
                    break;
                case 6 :
                    event = "SPACE";
                    break;
                case 7 :
                    event = "START_DOCUMENT";
                    break;
                case 8 :
                    event = "END_DOCUMENT";
                    break;
                case 9 :
                    event = "ENTITY_REFERENCE";
                    break;
                default:
                    break;
            }
        return event;
    }
}
