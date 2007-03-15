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

package org.apache.axiom.om;

import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory;
import org.apache.axiom.soap.SOAP11Constants;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;

public class IteratorTest extends AbstractTestCase {
    private OMElement envelope = null;

    public IteratorTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        //lets use a plain OM factory
        envelope =
                new StAXOMBuilder(new OMLinkedListImplFactory(),
                                  XMLInputFactory.newInstance().createXMLStreamReader(
                                          new FileReader(
                                                  getTestResourceFile(
                                                          "soap/soapmessage1.xml"))))
                        .getDocumentElement();
    }

    protected void tearDown() throws Exception {
        envelope = null;
    }

    /** Test the plain iterator which includes all the children (including the texts) */
    public void testIterator() {
        OMElement elt = envelope;
        Iterator iter = elt.getChildren();
        int counter = 0;
        while (iter.hasNext()) {
            counter ++;
            assertNotNull("Must return not null objects!", iter.next());
        }
        assertEquals("This element should contain only five children including the text ", 5,
                     counter);
    }

    /** Test the element iterator */
    public void testElementIterator() {
        OMElement elt = envelope;
        Iterator iter = elt.getChildElements();
        int counter = 0;
        while (iter.hasNext()) {
            counter ++;
            Object o = iter.next();
            assertNotNull("Must return not null objects!", o);
            assertTrue("All these should be elements!",
                       ((OMNode) o).getType() == OMNode.ELEMENT_NODE);
        }
        assertEquals("This element should contain only two elements ", 2, counter);
    }

    /** Test the element iterator */
    public void testElementQNameIterator() {
        OMElement elt = envelope;
        QName qname = new QName(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI,
                                SOAP11Constants.BODY_LOCAL_NAME);
        Iterator iter = elt.getChildrenWithName(qname);
        int counter = 0;
        while (iter.hasNext()) {
            counter ++;
            Object o = iter.next();
            assertNotNull("Must return not null objects!", o);
            assertTrue("All these should be elements!",
                       ((OMNode) o).getType() == OMNode.ELEMENT_NODE);
        }
        assertEquals("This element should contain only one element with the given QName ", 1,
                     counter);
    }

    /** test the remove exception behavior */
    public void testIteratorRemove1() {
        OMElement elt = envelope;
        Iterator iter = elt.getChildren();

        //this is supposed to throw an illegal state exception
        try {
            iter.remove();
            fail("remove should throw an exception");
        } catch (IllegalStateException e) {
            //ok. this is what should happen
        }

    }

    /** test the remove exception behavior, consecutive remove calls */
    public void testIteratorRemove2() {
        OMElement elt = envelope;
        Iterator iter = elt.getChildren();
        if (iter.hasNext()) {
            iter.next();
        }
        iter.remove();

        //this call must generate an exception
        try {
            iter.remove();
            fail("calling remove twice without a call to next is prohibited");
        } catch (IllegalStateException e) {
            //ok if we come here :)
        }

    }

    /** Remove all! */
    public void testIteratorRemove3() {
        OMElement elt = envelope;
        Iterator iter = elt.getChildren();
        while (iter.hasNext()) {
            iter.next();
            iter.remove();
        }
        iter = elt.getChildren();
        if (iter.hasNext()) {
            //we shouldn't reach here!
            fail("No children should remain after removing all!");
        }


    }

    /** test whether the children count reduces. */
    public void testIteratorRemove4() {
        OMElement elt = envelope;
        Iterator iter = elt.getChildren();
        int firstChildrenCount = 0;
        int secondChildrenCount = 0;
        while (iter.hasNext()) {
            assertNotNull(iter.next());
            firstChildrenCount++;
        }

        //remove the last node
        iter.remove();

        //reloop and check the count
        //Note- here we should get a fresh iterator since there is no method to
        //reset the iterator
        iter = elt.getChildren(); //reset the iterator
        while (iter.hasNext()) {
            assertNotNull(iter.next());
            secondChildrenCount++;
        }
        assertEquals("children count must reduce from 1",
                     firstChildrenCount - 1,
                     secondChildrenCount);

    }

    /** This will test the errrors mentioned in @link http://issues.apache.org/jira/browse/WSCOMMONS-12 */
    public void testScenariosInJIRA() {
        try {
            OMElement mtomSampleElement = createSampleXMLForTesting();
            testScenarioOne(mtomSampleElement);
            testScenarioTwo(mtomSampleElement);


        } catch (Exception e) {
            fail("Exception occurred whilst running the test " + e);
        }

    }

    private OMElement createSampleXMLForTesting() throws Exception {
        try {
            File imageSource = new File("test-resources/mtom/img/test.jpg");
            OMFactory fac = OMAbstractFactory.getOMFactory();
            OMNamespace omNs = fac.createOMNamespace("http://localhost/my", "my");

            OMElement data = fac.createOMElement("mtomSample", omNs);
            OMElement image = fac.createOMElement("image", omNs);

            FileDataSource dataSource = new FileDataSource(imageSource);
            DataHandler expectedDH = new DataHandler(dataSource);
            OMText textData = fac.createOMText(expectedDH, true);
            image.addChild(textData);

            OMElement imageName = fac.createOMElement("fileName", omNs);
            imageName.setText(imageSource.getAbsolutePath());
            data.addChild(image);
            data.addChild(imageName);

            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void testScenarioOne(OMElement mtomSampleElement) {
        Iterator imageElementIter = mtomSampleElement.getChildrenWithName(new QName("image"));
        // do something with the iterator
        while (imageElementIter.hasNext()) {
            imageElementIter.next();
            // do nothing
        }

        Iterator fileNameElementIter = mtomSampleElement.getChildrenWithName(new QName("fileName"));
        // do something with the iterator
        while (fileNameElementIter.hasNext()) {
            OMNode omNode = (OMNode) fileNameElementIter.next();
            if (omNode instanceof OMElement) {
                OMElement fileNameElement = (OMElement) omNode;
                assertTrue("fileName".equalsIgnoreCase(fileNameElement.getLocalName()));
            }
        }
    }

    private void testScenarioTwo(OMElement mtomSampleElement) {
        Iterator childElementsIter = mtomSampleElement.getChildElements();

        boolean imageElementFound = false;
        boolean fileNameElementFound = false;

        while (childElementsIter.hasNext()) {
            OMNode omNode = (OMNode) childElementsIter.next();
            if (omNode instanceof OMElement) {
                OMElement omElement = (OMElement) omNode;
                if (omElement.getLocalName().equals("fileName")) {
                    fileNameElementFound = true;
                } else if (omElement.getLocalName().equals("image")) {
                    imageElementFound = true;
                }
            }
        }

        assertTrue(fileNameElementFound && imageElementFound);
    }


}
