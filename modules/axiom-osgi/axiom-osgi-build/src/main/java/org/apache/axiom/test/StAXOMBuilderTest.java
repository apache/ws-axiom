package org.apache.axiom.test;

import java.io.ByteArrayInputStream;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.felix.ipojo.junit4osgi.OSGiTestCase;

public class StAXOMBuilderTest extends OSGiTestCase {
	
	private String xmlString = "<a:testElement xmlns:a=\"http://test/namespace\" />";
	
	public void testLLOMOMFactoryServicePresent() throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(xmlString.getBytes());
		StAXOMBuilder sb = new StAXOMBuilder(bais);
		OMElement oe = sb.getDocumentElement();
		assertEquals("testElement",oe.getLocalName());
		assertEquals("http://test/namespace", oe.getNamespace().getNamespaceURI());
	}
}
