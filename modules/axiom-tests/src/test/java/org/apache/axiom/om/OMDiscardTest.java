package org.apache.axiom.om;

import org.apache.axiom.om.impl.builder.StAXOMBuilder;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLInputFactory;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 *Test the discard method
 */
public class OMDiscardTest extends AbstractTestCase {


    public OMDiscardTest(String testName) {
        super(testName);   
    }

    
    public void testDiscard(){
        OMElement documentElement = null;
        try {
            // first build the OM tree without caching and see whether we can discard
            // an element from it
            StAXOMBuilder builder = new StAXOMBuilder(getXMLStreamReader());
            documentElement = builder.getDocumentElement();

            documentElement.getFirstElement().discard();

            String envelopeString = documentElement.toStringWithConsume();
        } catch (Exception e) {
            fail("discarding an element should work!");
        }
    }

    private XMLStreamReader getXMLStreamReader() throws XMLStreamException, FileNotFoundException {
        return XMLInputFactory.newInstance().
                createXMLStreamReader(
                        new FileReader(
                                getTestResourceFile("soap/soapmessage.xml")));
    }

}
