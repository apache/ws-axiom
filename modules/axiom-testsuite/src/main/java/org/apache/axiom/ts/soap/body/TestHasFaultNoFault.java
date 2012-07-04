package org.apache.axiom.ts.soap.body;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.ts.soap.SOAPSpec;
import org.apache.axiom.ts.soap.SOAPTestCase;

public class TestHasFaultNoFault extends SOAPTestCase {
    private final QName qname;
    
    public TestHasFaultNoFault(OMMetaFactory metaFactory, SOAPSpec spec, QName qname) {
        super(metaFactory, spec);
        this.qname = qname;
        addTestProperty("prefix", qname.getPrefix());
        addTestProperty("uri", qname.getNamespaceURI());
        addTestProperty("localName", qname.getLocalPart());
    }

    protected void runTest() throws Throwable {
        SOAPBody body = soapFactory.getDefaultEnvelope().getBody();
        body.addChild(soapFactory.createOMElement(
                qname.getLocalPart(), qname.getNamespaceURI(), qname.getPrefix()));
        assertFalse(body.hasFault());
    }
}
