package org.apache.axiom.test;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.felix.ipojo.junit4osgi.OSGiTestCase;

public class OMAbstractFactoryTest extends OSGiTestCase {

	public void testgetOMFactory() throws Exception {
		assertNotNull(OMAbstractFactory.getOMFactory());
	}

	public void testgetSOAP11Factory() throws Exception {
		assertNotNull(OMAbstractFactory.getSOAP11Factory());
	}

	public void testgetSOAP12Factory() throws Exception {
		assertNotNull(OMAbstractFactory.getSOAP12Factory());
	}
}
