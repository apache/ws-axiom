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

package org.apache.axiom.soap;

import java.util.Iterator;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMText;

public class SOAPFaultDetailTestBase extends SOAPFaultTestCase {
    protected SOAPFaultDetail soapFaultDetail;
    protected SOAPFaultDetail soapFaultDetailWithParser;
    protected OMNamespace omNamespace;

    public SOAPFaultDetailTestBase(OMMetaFactory omMetaFactory) {
        super(omMetaFactory);
    }

    //SOAP Fault Detail Test (Programaticaly Created)
    public void testAddDetailEntry() {
        soapFaultDetail.addDetailEntry(
                omFactory.createOMElement("DetailEntry1", omNamespace));
        soapFaultDetail.addDetailEntry(
                omFactory.createOMElement("DetailEntry2", omNamespace));
        Iterator iterator = soapFaultDetail.getAllDetailEntries();
        OMElement detailEntry1 = (OMElement) iterator.next();
        assertFalse(
                "SOAP Fault Detail Test : - After calling addDetailEntry method twice, getAllDetailEntries method returns empty iterator",
                detailEntry1 == null);
        assertTrue(
                "SOAP Fault Detail Test : - detailEntry1 local name mismatch",
                detailEntry1.getLocalName().equals("DetailEntry1"));
        assertTrue(
                "SOAP Fault Detail Test : - detailEntry1 namespace uri mismatch",
                detailEntry1.getNamespace().getNamespaceURI().equals(
                        "http://www.test.org"));
        OMElement detailEntry2 = (OMElement) iterator.next();
        assertFalse(
                "SOAP Fault Detail Test : - After calling addDetailEntry method twice, getAllDetailEntries method returns an iterator with only one object",
                detailEntry2 == null);
        assertTrue(
                "SOAP Fault Detail Test : - detailEntry2 local name mismatch",
                detailEntry2.getLocalName().equals("DetailEntry2"));
        assertTrue(
                "SOAP Fault Detail Test : - detailEntry2 namespace uri mismatch",
                detailEntry2.getNamespace().getNamespaceURI().equals(
                        "http://www.test.org"));
        assertTrue(
                "SOAP Fault Detail Test : - After calling addDetailEntry method twice, getAllDetailEntries method returns an iterator with three objects",
                !iterator.hasNext());
    }

    public void testGetAllDetailEntries() {
        Iterator iterator = soapFaultDetail.getAllDetailEntries();
        assertTrue(
                "SOAP Fault Detail Test : - After creating SOAP11FaultDetail element, it has DetailEntries",
                !iterator.hasNext());
        soapFaultDetail.addDetailEntry(
                omFactory.createOMElement("DetailEntry", omNamespace));
        iterator = soapFaultDetail.getAllDetailEntries();
        OMElement detailEntry = (OMElement) iterator.next();
        assertFalse(
                "SOAP Fault Detail Test : - After calling addDetailEntry method, getAllDetailEntries method returns empty iterator",
                detailEntry == null);
        assertTrue(
                "SOAP Fault Detail Test : - detailEntry local name mismatch",
                detailEntry.getLocalName().equals("DetailEntry"));
        assertTrue(
                "SOAP Fault Detail Test : - detailEntry namespace uri mismatch",
                detailEntry.getNamespace().getNamespaceURI().equals(
                        "http://www.test.org"));
        assertTrue(
                "SOAP Fault Detail Test : - After calling addDetailEntry method once, getAllDetailEntries method returns an iterator with two objects",
                !iterator.hasNext());
    }

    //SOAP Fault Detail Test (With Parser)
    public void testGetAllDetailEntriesWithParser() {
        Iterator iterator = soapFaultDetailWithParser.getAllDetailEntries();
        OMText textEntry = (OMText) iterator.next();
        assertFalse(
                "SOAP Fault Detail Test With Parser : - getAllDetailEntries method returns empty iterator",
                textEntry == null);
        assertTrue(
                "SOAP Fault Detail Test With Parser : - text value mismatch",
                textEntry.getText().trim().equals("Details of error"));
        OMElement detailEntry1 = (OMElement) iterator.next();
        assertFalse(
                "SOAP Fault Detail Test With Parser : - getAllDetailEntries method returns an itrator without detail entries",
                detailEntry1 == null);
        assertTrue(
                "SOAP Fault Detail Test With Parser : - detailEntry1 localname mismatch",
                detailEntry1.getLocalName().equals("MaxTime"));
        iterator.next();
        OMElement detailEntry2 = (OMElement) iterator.next();
        assertFalse(
                "SOAP Fault Detail Test With Parser : - getAllDetailEntries method returns an itrator with only one detail entries",
                detailEntry2 == null);
        assertTrue(
                "SOAP Fault Detail Test With Parser : - detailEntry2 localname mismatch",
                detailEntry2.getLocalName().equals("AveTime"));
        iterator.next();
        assertTrue(
                "SOAP Fault Detail Test With Parser : - getAllDetailEntries method returns an itrator with more than two detail entries",
                !iterator.hasNext());
    }
}
