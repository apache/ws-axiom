package org.apache.axiom.c14n.omwrapper;

import org.apache.axiom.c14n.omwrapper.interfaces.NamedNodeMap;
import org.apache.axiom.c14n.omwrapper.interfaces.Element;
import org.apache.axiom.c14n.DataParser;
import org.apache.axiom.om.OMElement;

import javax.xml.namespace.QName;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * @author Saliya Ekanayake (esaliya@gmail.com)
 */
public class NamedNodeMapImplTest extends TestCase {
    private DataParser dp = null;
    private NamedNodeMap nnm = null;

    public NamedNodeMapImplTest(String name){
        super(name);
    }

    public static Test suite() {
        return new TestSuite(NamedNodeMapImplTest.class);
    }

    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public void setUp() throws Exception {
        dp = new DataParser("/sample1.xml");
        dp.init();
        // get e5
        OMElement e5 = dp.omDocEle.getFirstChildWithName(new QName("http://example.org", "e5"));
        // get the wrapped element of e5
        Element e = (Element) dp.fac.getNode(e5);
        nnm = e.getAttributes();
    }

    public void testGetLength(){
        assertEquals(7, nnm.getLength());
    }

    public void testItem(){
        assertNotNull("valid index should not return null", nnm.item(nnm.getLength() - 1));
        assertNull("invalid index should return null", nnm.item(-2));
        assertNull("invalid index should return null", nnm.item(nnm.getLength()));
    }


}
