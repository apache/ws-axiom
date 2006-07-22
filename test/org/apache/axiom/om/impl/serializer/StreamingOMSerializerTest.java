/*
 * Copyright 2004,2005 The Apache Software Foundation.
 * Copyright 2006 International Business Machines Corp.
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
package org.apache.axiom.om.impl.serializer;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.impl.serialize.StreamingOMSerializer;

import junit.framework.TestCase;

/**
 * @author scheu
 *
 */
public class StreamingOMSerializerTest extends TestCase {

	private final String START = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
    "<soapenv:Body>\n";
	private final String ELEMENT_START = "<purchase-order xmlns=\"http://openuri.org/easypo\">\n" +
    "  <customer>\n" +
    "    <name>Gladys Kravitz</name>\n" +
    "    <address>Anytown, PA</address>\n" +
    "  </customer>\n";
    private final String ELEMENT_END ="  </purchase-order>";
	private final String END = " </soapenv:Body></soapenv:Envelope>";
	private final int COUNT = 10000;
	
	private XMLStreamReader reader = null;
	private XMLStreamWriter writer = null;
	
	
	private XMLInputFactory inputFactory= null;
	private XMLOutputFactory outputFactory = null;
	
	protected void setUp() throws Exception {
		// Get the Factories
		inputFactory = XMLInputFactory.newInstance();
		outputFactory = XMLOutputFactory.newInstance();
		
		// Build a large message
		StringBuffer buffer = new StringBuffer();
		buffer.append(START);
		for (int i=0;i<COUNT; i++) {
			buffer.append(ELEMENT_START);
		}
		for (int i=0;i<COUNT; i++) {
			buffer.append(ELEMENT_END);
		}
		buffer.append(END);
		StringReader sr = new StringReader(buffer.toString());
		// Create an XMLStringReader
		reader = inputFactory.createXMLStreamReader(sr);
		
		// Create an XMLStreamWriter
		StringWriter sw = new StringWriter();
		writer = outputFactory.createXMLStreamWriter(sw);
	}
	public void testLargeMessage() throws Exception {
		StreamingOMSerializer sos = new StreamingOMSerializer();
		sos.serialize(reader, writer);
	}
}
