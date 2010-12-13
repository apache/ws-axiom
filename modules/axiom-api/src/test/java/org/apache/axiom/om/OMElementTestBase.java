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

package org.apache.axiom.om;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAP11Constants;

public abstract class OMElementTestBase extends AbstractTestCase {
    protected final OMMetaFactory omMetaFactory;

    public OMElementTestBase(OMMetaFactory omMetaFactory) {
        this.omMetaFactory = omMetaFactory;
    }

    /** Test the plain iterator which includes all the children (including the texts) */
    public void testGetChildren() {
        OMElement elt = getTestResourceAsElement(omMetaFactory, TestConstants.SOAP_SOAPMESSAGE1);
        Iterator iter = elt.getChildren();
        int counter = 0;
        while (iter.hasNext()) {
            counter ++;
            assertNotNull("Must return not null objects!", iter.next());
        }
        assertEquals("This element should contain only five children including the text ", 5,
                     counter);
        elt.close(false);
    }

    /** test the remove exception behavior */
    public void testGetChildrenRemove1() {
        OMElement elt = getTestResourceAsElement(omMetaFactory, TestConstants.SOAP_SOAPMESSAGE1);
        Iterator iter = elt.getChildren();

        //this is supposed to throw an illegal state exception
        try {
            iter.remove();
            fail("remove should throw an exception");
        } catch (IllegalStateException e) {
            //ok. this is what should happen
        }

        elt.close(false);
    }

    /** test the remove exception behavior, consecutive remove calls */
    public void testGetChildrenRemove2() {
        OMElement elt = getTestResourceAsElement(omMetaFactory, TestConstants.SOAP_SOAPMESSAGE1);
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

        elt.close(false);
    }

    /** Remove all! */
    public void testGetChildrenRemove3() {
        OMElement elt = getTestResourceAsElement(omMetaFactory, TestConstants.SOAP_SOAPMESSAGE1);
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

        elt.close(false);
    }

    /** test whether the children count reduces. */
    public void testGetChildrenRemove4() {
        OMElement elt = getTestResourceAsElement(omMetaFactory, TestConstants.SOAP_SOAPMESSAGE1);
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

        elt.close(false);
    }

    /** Test the element iterator */
    public void testGetChildElements() {
        OMElement elt = getTestResourceAsElement(omMetaFactory, TestConstants.SOAP_SOAPMESSAGE1);
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
        elt.close(false);
    }
    
    /** Test the element iterator */
    public void testGetChildrenWithName() {
        OMElement elt = getTestResourceAsElement(omMetaFactory, TestConstants.SOAP_SOAPMESSAGE1);
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
        elt.close(false);
    }

    public void testGetChildrenWithLocalName() {
        OMElement elt = getTestResourceAsElement(omMetaFactory, TestConstants.SOAP_SOAPMESSAGE1);
        Iterator it = elt.getChildrenWithLocalName(SOAP11Constants.BODY_LOCAL_NAME);
        assertTrue(it.hasNext());
        OMElement child = (OMElement)it.next();
        assertEquals(new QName(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI,
                SOAP11Constants.BODY_LOCAL_NAME), child.getQName());
        assertFalse(it.hasNext());
        elt.close(false);
    }

    public void testSetText() {
        OMFactory factory = omMetaFactory.getOMFactory();
        String localName = "TestLocalName";
        String namespace = "http://ws.apache.org/axis2/ns";
        String prefix = "axis2";
        OMElement elem = factory.createOMElement(localName, namespace, prefix);

        String text = "The quick brown fox jumps over the lazy dog";

        elem.setText(text);

        assertEquals("Text value mismatch", text, elem.getText());
    }

    public void testCDATA() throws Exception {
        OMFactory factory = omMetaFactory.getOMFactory();
        OMElement omElement = factory.createOMElement("TestElement", null);
        final String text = "this is <some> text in a CDATA";
        factory.createOMText(omElement, text, XMLStreamConstants.CDATA);
        assertEquals(text, omElement.getText());

        // OK, CDATA on its own worked - now confirm that a plain text + a CDATA works
        omElement = factory.createOMElement("element2", null);
        final String normalText = "regular text and ";
        factory.createOMText(omElement, normalText);
        factory.createOMText(omElement, text, XMLStreamConstants.CDATA);
        assertEquals(normalText + text, omElement.getText());
    }
    
    public void testAddChild() {
        OMFactory factory = omMetaFactory.getOMFactory();
        String localName = "TestLocalName";
        String childLocalName = "TestChildLocalName";
        String namespace = "http://ws.apache.org/axis2/ns";
        String prefix = "axis2";

        OMElement elem = factory.createOMElement(localName, namespace, prefix);
        OMElement childElem = factory.createOMElement(childLocalName, namespace, prefix);

        elem.addChild(childElem);

        Iterator it = elem.getChildrenWithName(new QName(namespace, childLocalName));

        int count = 0;
        while (it.hasNext()) {
            OMElement child = (OMElement) it.next();
            assertEquals("Child local name mismatch", childLocalName, child.getLocalName());
            assertEquals("Child namespace mismatch", namespace,
                         child.getNamespace().getNamespaceURI());
            count ++;
        }
        assertEquals("In correct number of children", 1, count);
    }
    
    public void testFindNamespaceByPrefix() throws Exception {
        OMElement root =
                AXIOMUtil.stringToOM(omMetaFactory.getOMFactory(), "<a:root xmlns:a='urn:a'><child/></a:root>");
        OMNamespace ns = root.getFirstElement().findNamespace(null, "a");
        assertNotNull(ns);
        assertEquals("urn:a", ns.getNamespaceURI());
        root.close(false);
    }
    
    /**
     * Test that calling {@link OMElement#addAttribute(OMAttribute)} with an attribute that is
     * already owned by another element will clone the attribute.
     */
    public void testAddAttributeAlreadyOwnedByOtherElement() {
        OMFactory factory = omMetaFactory.getOMFactory();
        OMElement element1 = factory.createOMElement(new QName("test"));
        OMElement element2 = factory.createOMElement(new QName("test"));
        OMAttribute att1 = element1.addAttribute("test", "test", null);
        OMAttribute att2 = element2.addAttribute(att1);
        assertSame(element1, att1.getOwner());
        assertNotSame(att1, att2);
        assertSame(element2, att2.getOwner());
    }
    
    /**
     * Test that calling {@link OMElement#addAttribute(OMAttribute)} with an attribute that is
     * already owned by the element is a no-op.
     */
    public void testAddAttributeAlreadyOwnedByElement() {
        OMFactory factory = omMetaFactory.getOMFactory();
        OMElement element = factory.createOMElement(new QName("test"));
        OMAttribute att = element.addAttribute("test", "test", null);
        OMAttribute result = element.addAttribute(att);
        assertSame(result, att);
        assertSame(element, att.getOwner());
        Iterator it = element.getAllAttributes();
        assertTrue(it.hasNext());
        assertSame(att, it.next());
        assertFalse(it.hasNext());
    }
    
    /**
     * Test that {@link OMElement#addAttribute(OMAttribute)} behaves correctly when an attribute
     * with the same name and namespace URI already exists.
     */
    public void testAddAttributeReplace1() {
        OMFactory factory = omMetaFactory.getOMFactory();
        // Use same namespace URI but different prefixes
        OMNamespace ns1 = factory.createOMNamespace("urn:ns", "p1");
        OMNamespace ns2 = factory.createOMNamespace("urn:ns", "p2");
        OMElement element = factory.createOMElement(new QName("test"));
        OMAttribute att1 = factory.createOMAttribute("test", ns1, "test");
        OMAttribute att2 = factory.createOMAttribute("test", ns2, "test");
        element.addAttribute(att1);
        element.addAttribute(att2);
        Iterator it = element.getAllAttributes();
        assertTrue(it.hasNext());
        assertSame(att2, it.next());
        assertFalse(it.hasNext());
        assertNull(att1.getOwner());
        assertSame(element, att2.getOwner());
    }
    
    /**
     * Test that {@link OMElement#addAttribute(String, String, OMNamespace)} behaves correctly when
     * an attribute with the same name and namespace URI already exists.
     */
    public void testAddAttributeReplace2() {
        OMFactory factory = omMetaFactory.getOMFactory();
        // Use same namespace URI but different prefixes
        OMNamespace ns1 = factory.createOMNamespace("urn:ns", "p1");
        OMNamespace ns2 = factory.createOMNamespace("urn:ns", "p2");
        OMElement element = factory.createOMElement(new QName("test"));
        OMAttribute att1 = element.addAttribute("test", "value1", ns1);
        OMAttribute att2 = element.addAttribute("test", "value2", ns2);
        Iterator it = element.getAllAttributes();
        assertTrue(it.hasNext());
        assertSame(att2, it.next());
        assertFalse(it.hasNext());
        assertNull(att1.getOwner());
        assertSame(element, att2.getOwner());
        assertEquals("value1", att1.getAttributeValue());
        assertEquals("value2", att2.getAttributeValue());
    }
    
    private int getNumberOfOccurrences(String xml, String pattern) {
        int index = -1;
        int count = 0;
        while ((index = xml.indexOf(pattern, index + 1)) != -1) {
            count++;
        }

        return count;
    }

    public void testDeclareDefaultNamespace1() throws XMLStreamException {

        /**
         * <RootElement xmlns="http://one.org">
         *   <ns2:ChildElementOne xmlns:ns2="http://ws.apache.org/axis2" xmlns="http://two.org">
         *      <ChildElementTwo xmlns="http://one.org" />
         *   </ns2:ChildElementOne>
         * </RootElement>
         */

        OMFactory omFac = omMetaFactory.getOMFactory();

        OMElement documentElement = omFac.createOMElement("RootElement", null);
        documentElement.declareDefaultNamespace("http://one.org");

        OMNamespace ns = omFac.createOMNamespace("http://ws.apache.org/axis2", "ns2");
        OMElement childOne = omFac.createOMElement("ChildElementOne", ns, documentElement);
        childOne.declareDefaultNamespace("http://two.org");

        OMElement childTwo = omFac.createOMElement("ChildElementTwo", null, childOne);
        childTwo.declareDefaultNamespace("http://one.org");


        assertEquals(2, getNumberOfOccurrences(documentElement.toStringWithConsume(),
                "xmlns=\"http://one.org\""));
    }

    public void testDeclareDefaultNamespace2() throws XMLStreamException {

        /**
         * <RootElement xmlns:ns1="http://one.org" xmlns:ns2="http://one.org">
         *   <ns2:ChildElementOne xmlns="http://one.org">
         *      <ns2:ChildElementTwo />
         *   </ns2:ChildElementOne>
         * </RootElement>
         */

        OMFactory omFac = omMetaFactory.getOMFactory();

        OMElement documentElement = omFac.createOMElement("RootElement", null);
        OMNamespace ns1 = documentElement.declareNamespace("http://one.org", "ns1");
        OMNamespace ns2 = documentElement.declareNamespace("http://one.org", "ns2");

        OMElement childOne = omFac.createOMElement("ChildElementOne", ns2, documentElement);
        childOne.declareDefaultNamespace("http://one.org");

        OMElement childTwo = omFac.createOMElement("ChildElementTwo", ns1, childOne);

        assertEquals(1, getNumberOfOccurrences(documentElement.toStringWithConsume(),
                "xmlns:ns2=\"http://one.org\""));
    }

    public void testMultipleDefaultNS() {
        OMFactory omFactory = omMetaFactory.getOMFactory();
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

        OMNamespace omElementOneChildNS = omElementOneChild.getNamespace();
        OMNamespace omElementTwoChildNS = omElementTwoChild.getNamespace();
        // TODO: LLOM's and DOOM's behaviors are slightly different here; need to check if both are allowed
        assertTrue(omElementOneChildNS == null || "".equals(omElementOneChildNS.getNamespaceURI()));
        assertTrue(omElementTwoChildNS == null || "".equals(omElementTwoChildNS.getNamespaceURI()));
    }

    public void testChildReDeclaringParentsDefaultNSWithPrefix() throws Exception {
        OMFactory fac = omMetaFactory.getOMFactory();
        OMElement elem = fac.createOMElement("RequestSecurityToken", null);
        elem.declareDefaultNamespace("http://schemas.xmlsoap.org/ws/2005/02/trust");
        fac.createOMElement(new QName("TokenType"), elem).setText("test");
        fac.createOMElement(new QName("RequestType"), elem).setText("test1");

        fac.createOMElement(
                new QName("http://schemas.xmlsoap.org/ws/2005/02/trust", "Entropy", "wst"),
                elem);
        String xml = elem.toString();

        OMXMLParserWrapper builder = omMetaFactory.createOMBuilder(omMetaFactory.getOMFactory(),
                new ByteArrayInputStream(xml.getBytes()));

        builder.getDocumentElement().build();

        // The StAX implementation may or may not have a trailing blank in the tag
        String assertText1 =
                "<wst:Entropy xmlns:wst=\"http://schemas.xmlsoap.org/ws/2005/02/trust\" />";
        String assertText2 =
                "<wst:Entropy xmlns:wst=\"http://schemas.xmlsoap.org/ws/2005/02/trust\"/>";
        String assertText3 =
                "<wst:Entropy xmlns:wst=\"http://schemas.xmlsoap.org/ws/2005/02/trust\"></wst:Entropy>";

        assertTrue((xml.indexOf(assertText1) != -1) ||
                (xml.indexOf(assertText2) != -1) ||
                (xml.indexOf(assertText3) != -1));
    }

    public void testChildReDeclaringGrandParentsDefaultNSWithPrefix() {
        OMFactory fac = omMetaFactory.getOMFactory();
        OMElement elem = fac.createOMElement("RequestSecurityToken", null);
        elem.declareDefaultNamespace("http://schemas.xmlsoap.org/ws/2005/02/trust");
        fac.createOMElement(new QName("TokenType"), elem).setText("test");
        fac.createOMElement(new QName("RequestType"), elem).setText("test1");

        OMElement entElem = fac.createOMElement(
                new QName("http://schemas.xmlsoap.org/ws/2005/02/trust", "Entropy", "wst"),
                elem);
        OMElement binSecElem = fac.createOMElement(
                new QName("http://schemas.xmlsoap.org/ws/2005/02/trust", "Binarysecret", "wst"),
                entElem);
        binSecElem.setText("secret value");
        String xml = elem.toString();
        assertTrue("Binarysecret element should have \'wst\' ns prefix",
                   xml.indexOf("<wst:Binarysecret") != -1);
    }
}
