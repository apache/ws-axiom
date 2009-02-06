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
