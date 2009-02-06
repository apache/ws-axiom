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
package org.apache.axiom.test;

import org.apache.axiom.soap.SOAPFactory;
import org.apache.felix.ipojo.junit4osgi.OSGiTestCase;
import org.osgi.framework.ServiceReference;

public class ServiceTest extends OSGiTestCase {
	
	public void testLLOMOMFactoryServicePresent() throws Exception {
		ServiceReference[] omfactRefs = context
				.getServiceReferences("org.apache.axiom.om.OMFactory", "(implementationName=llom)");
		assertNotNull(omfactRefs);
		assertEquals(3, omfactRefs.length);
	}
	
	public void testDOOMOMFactoryServicePresent() throws Exception {
		ServiceReference[] omfactRefs = context
				.getServiceReferences("org.apache.axiom.om.OMFactory", "(implementationName=llom)");
		assertNotNull(omfactRefs);
		assertEquals(3, omfactRefs.length);
	}
	
	public void testLLOMSOAP11FactoryServicePresent() throws Exception {
		ServiceReference[] soapfactRefs = context.getServiceReferences(
				"org.apache.axiom.soap.SOAPFactory", "(&(axiom.soapVersion=soap11)(implementationName=llom))");

		assertNotNull(soapfactRefs);
		assertEquals(1, soapfactRefs.length);
		SOAPFactory sf = (SOAPFactory) context.getService(soapfactRefs[0]);
		assertEquals("http://schemas.xmlsoap.org/soap/envelope/",sf.createSOAPEnvelope().getNamespace().getNamespaceURI());
	}
	
	public void testDOOMSOAP11FactoryServicePresent() throws Exception {
		ServiceReference[] soapfactRefs = context.getServiceReferences(
				"org.apache.axiom.soap.SOAPFactory", "(&(axiom.soapVersion=soap11)(implementationName=doom))");

		assertNotNull(soapfactRefs);
		assertEquals(1, soapfactRefs.length);
		SOAPFactory sf = (SOAPFactory) context.getService(soapfactRefs[0]);
		assertEquals("http://schemas.xmlsoap.org/soap/envelope/",sf.createSOAPEnvelope().getNamespace().getNamespaceURI());
	}
	
	public void testLLOMSOAP12FactoryServicePresent() throws Exception {
		ServiceReference[] soapfactRefs = context.getServiceReferences(
				"org.apache.axiom.soap.SOAPFactory", "(&(axiom.soapVersion=soap12)(implementationName=doom))");

		assertNotNull(soapfactRefs);
		assertEquals(1, soapfactRefs.length);
		SOAPFactory sf = (SOAPFactory) context.getService(soapfactRefs[0]);
		assertEquals("http://www.w3.org/2003/05/soap-envelope",sf.createSOAPEnvelope().getNamespace().getNamespaceURI());
	}
	
	public void testDOOMSOAP12FactoryServicePresent() throws Exception {
		ServiceReference[] soapfactRefs = context.getServiceReferences(
				"org.apache.axiom.soap.SOAPFactory", "(&(axiom.soapVersion=soap12)(implementationName=llom))");

		assertNotNull(soapfactRefs);
		assertEquals(1, soapfactRefs.length);
		SOAPFactory sf = (SOAPFactory) context.getService(soapfactRefs[0]);
		assertEquals("http://www.w3.org/2003/05/soap-envelope",sf.createSOAPEnvelope().getNamespace().getNamespaceURI());
	}
}
