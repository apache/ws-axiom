package org.apache.axiom.om.impl.llom.util;

import javax.xml.stream.XMLStreamException;

import junit.framework.TestCase;

public class AXIOMUtilTest extends TestCase {
	public void testStringToOM() throws XMLStreamException {
		String testString = "\u00e0 peine arriv\u00e9s nous entr\u00e2mes dans sa chambre";
		assertEquals(testString, AXIOMUtil.stringToOM("<a>" + testString + "</a>").getText());
	}
}
